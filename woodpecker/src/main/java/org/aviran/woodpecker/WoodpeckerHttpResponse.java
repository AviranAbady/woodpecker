package org.aviran.woodpecker;

/**
 * Created by Aviran Abady on 5/26/17.
 */

public interface WoodpeckerHttpResponse {
    void httpSuccess(String response);
    void httpError();
}
