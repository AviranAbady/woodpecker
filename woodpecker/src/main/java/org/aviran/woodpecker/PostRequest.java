package org.aviran.woodpecker;

import com.google.gson.Gson;

import org.aviran.woodpecker.annotations.Post;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
        } finally {
            httpConnection.disconnect();
        }
    }

    private void writeMultipartData(OutputStream outputStream) {
        String separator = "-----------------------------" + getRandomNumber();
        String content = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n\r\n";
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
