package org.aviran.woodpecker;

import org.aviran.woodpecker.annotations.Get;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Aviran Abady on 5/26/17.
 */

class GetRequest extends AbsHttpRequest {

    private URL url;

    public GetRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);

        try {
            url = createURL(peck.getRequest());
        } catch (MalformedURLException |
                IllegalAccessException |
                UnsupportedEncodingException  e) {
            listener.httpError();
        }
    }

    private URL createURL(WoodpeckerRequest request)
            throws
            MalformedURLException,
            IllegalAccessException,
            UnsupportedEncodingException {

        Get requestAnnotation = request.getClass().getAnnotation(Get.class);
        StringBuilder url = new StringBuilder();
        url.append(peck.getWoodpecker().getBaseURL());
        url.append(requestAnnotation.value());
        String parameters = parseRequestParameters(request,url, true);
        if(parameters.length() > 0) {
            url.append("?").append(parameters);
        }
        return new URL(url.toString());
    }

    public String performRequest() {
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            addHeaders(httpConnection);
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
    }
}