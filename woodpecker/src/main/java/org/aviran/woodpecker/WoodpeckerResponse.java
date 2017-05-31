package org.aviran.woodpecker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Aviran Abady on 5/26/17.
 */

public abstract class WoodpeckerResponse<T> {
    private int responseCode;
    private String rawResponse;

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
}