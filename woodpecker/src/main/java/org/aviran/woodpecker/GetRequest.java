package org.aviran.woodpecker;

import org.aviran.woodpecker.annotations.Get;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Aviran Abady on 5/26/17.
 */

class GetRequest extends HttpRequest {

    protected URL _url;

    public GetRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);
        String parameters = parseRequestPayload(true);
        if(parameters.length() > 0) {
            urlBuilder.append("?").append(parameters);
        }
    }

    public String performRequest(HttpURLConnection httpConnection) {
        try {
            httpConnection.setRequestMethod("GET");
            addHeaders(httpConnection);
            String response = readInputSteam(httpConnection.getInputStream());
            httpConnection.getInputStream().close();
            peck.getResponse().setRawResponse(response);
            peck.getResponse().setResponseCode(httpConnection.getResponseCode());
            peck.getResponse().setHeaders(httpConnection.getHeaderFields());
            return response;
        } catch (IOException e) {
            generateErrorResponse(peck.getResponse(), httpConnection);
            return null;
        }
        finally {
            httpConnection.disconnect();
        }
    }

    @Override
    public String getRelativePath() {
        Get requestAnnotation = peck.getRequest().getClass().getAnnotation(Get.class);
        return requestAnnotation.value();
    }
}