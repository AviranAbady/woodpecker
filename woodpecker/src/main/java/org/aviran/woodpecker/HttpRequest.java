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

import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Path;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

abstract class HttpRequest extends AsyncTask<Void, Integer, String> {

    private final WoodpeckerProgressListener progressListener;
    private HttpURLConnection connection;

    public abstract String performRequest(HttpURLConnection url);

    protected final Peck peck;
    protected final WoodpeckerHttpResponse responseListener;
    protected StringBuilder urlBuilder;

    public abstract String getRelativePath();

    public HttpRequest(Peck peck, WoodpeckerHttpResponse listener) {
        this.peck = peck;
        this.progressListener = peck.getProgressListener();
        this.responseListener = listener;
        this.urlBuilder = new StringBuilder();
        createURL(urlBuilder);
    }

    private void createURL(StringBuilder url) {
        url.append(peck.getWoodpecker().getBaseURL());
        url.append(getRelativePath());

        Field[] fields = peck.getRequest().getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(Path.class)) {
                try {
                    //todo refactor to pattern/match
                    field.setAccessible(true);
                    String str = urlBuilder.toString().replaceAll("\\{" + field.getName() + "\\}", field.get(peck.getRequest()).toString());
                    urlBuilder.setLength(0);
                    urlBuilder.append(str);
                } catch (IllegalAccessException e) {
                    throw new WoodpeckerException("Could not access values from request");
                }
            }
        }
    }

    protected String parseRequestPayload(boolean encode) {
        WoodpeckerRequest request = peck.getRequest();
        StringBuilder parameters = new StringBuilder();
        Field[] fields = request.getClass().getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(Param.class)) {
                    parameters.append(field.getName());
                    parameters.append("=");
                    field.setAccessible(true);
                    try {
                        if (encode) {
                            parameters.append(URLEncoder.encode(field.get(request).toString(), "utf-8"));
                        } else {
                            parameters.append(field.get(request).toString());
                        }
                    } catch (IllegalAccessException e) {
                        throw new WoodpeckerException("Could not access values from request");
                    } catch (UnsupportedEncodingException e) {
                        throw new WoodpeckerException("Could not encode parameters");
                    }
                    parameters.append("&");
                }
            }
            if (parameters.length() > 0) {
                parameters.deleteCharAt(parameters.length() - 1);
            }
        }
        return parameters.toString();
    }

    protected String readInputSteam(InputStream inputStream) throws IOException {
        int charsRead;
        if (peck.isResponseTypeStream()) {
            int contentLength = getContentLength();
            int progress = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((charsRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, charsRead);
                if (progressListener != null && contentLength != 0) {
                    progress += charsRead;
                    publishProgress(progress, contentLength);
                }
            }
            outputStream.flush();
            peck.setResponseStream(new ByteArrayInputStream(outputStream.toByteArray()));
            outputStream.close();
            return null;

        } else {
            StringBuilder stringBuilder = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(inputStream);
            char[] buffer = new char[1024];
            while ((charsRead = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, charsRead);
            }
            reader.close();
            return stringBuilder.toString();
        }
    }

    private int getContentLength() {
        Map<String, List<String>> headers = connection.getHeaderFields();
        try {
            return Integer.parseInt(headers.get("Content-Length").get(0));
        } catch (NullPointerException | NumberFormatException e) {
            return 0;
        }
    }

    protected void generateErrorResponse(WoodpeckerResponse response, HttpURLConnection httpConnection) {
        try {
            response.setResponseCode(httpConnection.getResponseCode());
            response.setRawResponse(readInputSteam(httpConnection.getErrorStream()));
        } catch (IOException e) {

        }
    }

    protected void addHeaders(HttpURLConnection httpConnection) {
        Map<String, String> headers = Woodpecker.getSettings().getHeaders();
        if (headers == null || headers.size() == 0) {
            return;
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            httpConnection.addRequestProperty(header.getKey(), header.getValue());
        }
    }

    // AsyncTask's calls

    protected String doInBackground(Void... v) {
        URL url;
        try {
            url = new URL(urlBuilder.toString());
            connection = (HttpURLConnection) url.openConnection();
            return performRequest(connection);
        } catch (MalformedURLException e) {
            throw new WoodpeckerException("Malformed URL - " + urlBuilder.toString());
        } catch (IOException e) {
            throw new WoodpeckerException("Could not open connection");
        }
    }

    protected void onProgressUpdate(Integer... progress) {
        if (progressListener == null) {
            return;
        }

        progressListener.onProgress("tbd", progress[0], progress[1]);
    }

    protected void onPostExecute(String response) {
        if (response == null && peck.getResponseStream() == null) {
            responseListener.httpError();
            return;
        }
        responseListener.httpSuccess(response);
        peck.getWoodpecker().peck();
    }


}