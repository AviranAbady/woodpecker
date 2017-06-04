package org.aviran.woodpecker;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.aviran.woodpecker.annotations.Head;

/**
 * Created by Aviran Abady on 6/2/17.
 */

class HeadRequest extends HttpRequest {

    public HeadRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);
        String parameters = parseRequestPayload(true);
        if(parameters.length() > 0) {
            urlBuilder.append("?").append(parameters);
        }
    }

    public String performRequest(HttpURLConnection httpConnection) {
        try {
            httpConnection.setRequestMethod("HEAD");
            addHeaders(httpConnection);
            peck.getResponse().setResponseCode(httpConnection.getResponseCode());
            peck.getResponse().setHeaders(httpConnection.getHeaderFields());
            return "";
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
        Head requestAnnotation = peck.getRequest().getClass().getAnnotation(Head.class);
        return requestAnnotation.value();

    }
}
