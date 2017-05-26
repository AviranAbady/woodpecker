/*
 * Copyright (C) 2017 Aviran Abady.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aviran.woodpecker;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import org.aviran.woodpecker.annotations.File;
import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Post;

class PostRequest extends HttpRequest {

    public static void create(final Peck peck) {
        final PostRequest httpTask = new PostRequest(peck, new WoodpeckerHttpResponse(peck));
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private String requestMethod = "POST";
    private String body;
    private String multipartSeparator;

    protected PostRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);
        if (!peck.isRequestMultipart()) {
            body = requestBody();
        }
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    private String requestBody() {
        if (peck.isRequestWithParameters()) {
            return parseRequestPayload(false);
        } else {
            return new Gson().toJson(peck.getRequest());
        }
    }

    @Override
    public String performRequest(HttpURLConnection httpConnection) {
        try {
            httpConnection.setRequestMethod(requestMethod);
            addHeaders(httpConnection);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

            if (peck.isRequestMultipart()) {
                writeMultipartData(httpConnection);
            } else if (body != null) {
                DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
                dataOutputStream.write(body.getBytes());
                dataOutputStream.close();
            }

            String response = readInputSteam(httpConnection.getInputStream());
            httpConnection.getInputStream().close();
            peck.getResponse().setRawResponse(response);
            peck.getResponse().setResponseCode(httpConnection.getResponseCode());
            peck.getResponse().setHeaders(httpConnection.getHeaderFields());
            return response;
        } catch (IOException e) {
            generateErrorResponse(peck.getResponse(), httpConnection);
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } finally {
            httpConnection.disconnect();
        }
    }

    private void writeMultipartData(HttpURLConnection connection) throws IOException, IllegalAccessException {
        WoodpeckerRequest request = peck.getRequest();
        multipartSeparator = "-----------------------------" + getRandomNumber();
        int contentLength = calculateContentLength(request);
        connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + multipartSeparator);
        connection.addRequestProperty("Content-Length", String.valueOf(contentLength));

        final String contentFile = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n";
        final String contentParam = "Content-Disposition: form-data; name=\"%s\";\r\n\r\n";
        final String crlf = "\r\n";

        Field[] fields = peck.getRequest().getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }

        connection.setUseCaches(false);
        connection.setFixedLengthStreamingMode(contentLength);
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        long startTime = System.currentTimeMillis();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Param.class)) {
                dataOutputStream.writeBytes("--" + multipartSeparator);
                dataOutputStream.writeBytes(crlf);
                dataOutputStream.writeBytes(String.format(contentParam, field.getName()));
                dataOutputStream.writeBytes(field.get(request).toString());
                dataOutputStream.writeBytes(crlf);
                publishProgress(dataOutputStream.size(), contentLength);
            } else if (field.isAnnotationPresent(File.class)) {
                WoodpeckerFileStream file = (WoodpeckerFileStream) field.get(request);
                dataOutputStream.writeBytes("--" + multipartSeparator);
                dataOutputStream.writeBytes(crlf);
                dataOutputStream.writeBytes(String.format(contentFile, field.getName(), file.getFileName()));
                dataOutputStream.writeBytes("Content-Transfer-Encoding: binary\r\n");
                dataOutputStream.writeBytes("Content-Type: " + guessMimeType(file.getFileName()) + "\r\n\r\n");

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                InputStream stream = file.getStream();
                while ((bytesRead = stream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                    dataOutputStream.flush();
                    publishProgress(dataOutputStream.size(), contentLength);
                }
                dataOutputStream.writeBytes(crlf);
                publishProgress(dataOutputStream.size(), contentLength);
                stream.close();
            }
        }
        dataOutputStream.writeBytes("--" + multipartSeparator);
        dataOutputStream.writeBytes("--");
        dataOutputStream.writeBytes(crlf);
        dataOutputStream.flush();
        publishProgress(dataOutputStream.size(), contentLength);
        Log.i("HttpRequest","TotalTime: " + ((System.currentTimeMillis() - startTime)/1000f));
        dataOutputStream.close();
    }

    private int calculateContentLength(WoodpeckerRequest request) throws IllegalAccessException, IOException {
        Field[] fields = request.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return 0;
        }

        int fileHeadersLength = (
                "Content-Disposition: form-data; name=\"\"; filename=\"\"\r\n" +
                        "Content-Transfer-Encoding: binary\r\n" +
                        "Content-Type: \r\n\r\n").length();


        int fieldHeadersLength = "Content-Disposition: form-data; name=\"\";\r\n\r\n".length();
        int separatorLength = multipartSeparator.length() + 2;
        int crlfLength = 2;

        int contentLength = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(Param.class)) {
                contentLength += separatorLength + crlfLength;
                contentLength += fieldHeadersLength + field.getName().length();
                contentLength += field.get(request).toString().length() + crlfLength;
            }
            else if (field.isAnnotationPresent(File.class)) {
                WoodpeckerFileStream file = (WoodpeckerFileStream) field.get(request);
                contentLength += separatorLength + crlfLength;
                contentLength += fileHeadersLength + field.getName().length();
                contentLength += file.getFileName().length();
                contentLength += guessMimeType(file.getFileName()).length();
                contentLength += file.getStream().available() + crlfLength;
            }
        }

        if(contentLength > 0) {
            contentLength += separatorLength + crlfLength + 2;
        }

        return contentLength;
    }

    private String guessMimeType(String fileName) {
        String guess = URLConnection.guessContentTypeFromName(fileName);
        if (guess == null) {
            return "application/octet-stream";
        }
        return guess;
    }

    private String getRandomNumber() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    public String getRelativePath() {
        Post requestAnnotation = peck.getRequest().getClass().getAnnotation(Post.class);
        return requestAnnotation.value();
    }
}