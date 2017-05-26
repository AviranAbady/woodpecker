package org.aviran.woodpecker;

import com.google.gson.Gson;

import org.aviran.woodpecker.annotations.Post;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Aviran Abady on 5/31/17.
 */

public class PostRequest extends AbsHttpRequest {
    private URL url;
    private String body;

    public PostRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);

        try {
            url = createURL(peck.getRequest());
        } catch (MalformedURLException e) {
            listener.httpError();
        }
    }

    protected URL createURL(WoodpeckerRequest request) throws MalformedURLException {
        Post requestAnnotation = request.getClass().getAnnotation(Post.class);
        StringBuilder url = new StringBuilder();
        url.append(peck.getWoodpecker().getBaseURL());
        url.append(requestAnnotation.value());
        body = requestBody(url);

        return new URL(url.toString());
    }

    private String requestBody(StringBuilder url) {
        WoodpeckerRequest request = peck.getRequest();
        String parameters;
        try {
            parameters = parseRequestParameters(request, url, false);
        } catch (UnsupportedEncodingException | IllegalAccessException e) {
            throw new WoodpeckerException("");
        }

        if (parameters.length() > 0) {
            return parameters;
        } else {
            return new Gson().toJson(peck.getRequest());
        }
    }

    @Override
    public String performRequest() {
        HttpURLConnection httpConnection = null;
        try {

            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            addHeaders(httpConnection);
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

            if(body != null) {
                DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
                dataOutputStream.write(body.getBytes());
                dataOutputStream.close();
            }

            String response = readInputSteam(httpConnection.getInputStream());
            httpConnection.getInputStream().close();
            httpConnection.disconnect();
            peck.getResponse().setRawResponse(response);
            peck.getResponse().setResponseCode(httpConnection.getResponseCode());
            return response;
        } catch (MalformedURLException e) {
            return null;
        } catch (ProtocolException e) {
            return null;
        } catch (IOException e) {
            generateErrorResponse(peck.getResponse(), httpConnection);
            return null;
        }
        finally {
//            httpConnection.getInputStream().close();
//            httpConnection.disconnect();
        }
    }
}
