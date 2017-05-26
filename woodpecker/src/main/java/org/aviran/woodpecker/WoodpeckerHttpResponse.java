package org.aviran.woodpecker;

/**
 * Created by Aviran Abady on 5/26/17.
 */

public interface WoodpeckerHttpResponse {
    void success(String response);
    void error();
}
