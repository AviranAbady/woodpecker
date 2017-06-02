package org.aviran.woodpecker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Aviran Abady on 5/26/17.
 */

public abstract class WoodpeckerResponse<T> {
    private int responseCode;
    private String rawResponse;
    private int requestId;
    private List<WoodpeckerRequest> requests;
    private Map<String, List<String>> headers;

    public abstract void onSuccess(T response);

    public void onError(WoodpeckerException error) {

    }

    public <T> Type getType() {
        return ((ParameterizedType)getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    protected void setRequestsList(List<WoodpeckerRequest> requests) {
        this.requests = requests;
    }

    protected void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public WoodpeckerRequest getNextRequest() {
        return getRequestByPosition(requestId + 1);
    }

    private WoodpeckerRequest getRequestByPosition(int position) {
        if(position >= requests.size()) {
            throw new WoodpeckerException("There is not request at position " + position);
        }

        return requests.get(position);
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}