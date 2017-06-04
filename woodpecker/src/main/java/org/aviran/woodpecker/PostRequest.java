package org.aviran.woodpecker;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;

import org.aviran.woodpecker.annotations.File;
import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Post;

/**
 * Created by Aviran Abady on 5/31/17.
 */

class PostRequest extends HttpRequest {
    private String requestMethod = "POST";
    private String body;

    public PostRequest(Peck peck, WoodpeckerHttpResponse listener) {
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
                writeMultipartData(httpConnection.getOutputStream());
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

    private void writeMultipartData(OutputStream outputStream) throws IOException, IllegalAccessException {
        WoodpeckerRequest request = peck.getRequest();
        String separator = "-----------------------------" + getRandomNumber();
        String contentFile = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n\r\n";
        String contentParam = "Content-Disposition: form-data; name=\"%s\";\r\n\r\n";
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        Field[] fields = peck.getRequest().getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return;
        }

        for (Field field : fields) {
            if (field.isAnnotationPresent(Param.class)) {
                dataOutputStream.writeBytes(separator);
                dataOutputStream.writeBytes(String.format(contentParam, field.getName()));
                dataOutputStream.writeBytes(field.get(request).toString() + "\r\n");
                dataOutputStream.writeBytes(separator);

            } else if (field.isAnnotationPresent(File.class)) {
                WoodpeckerFileStream file = (WoodpeckerFileStream) field.get(request);
                dataOutputStream.writeBytes(separator);
                dataOutputStream.writeBytes(String.format(contentFile, field.getName()));

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                InputStream stream = file.getStream();
                while ((bytesRead = stream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
                dataOutputStream.writeBytes(separator);
                stream.close();
            }
        }
        dataOutputStream.writeBytes("--");

        dataOutputStream.close();
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
