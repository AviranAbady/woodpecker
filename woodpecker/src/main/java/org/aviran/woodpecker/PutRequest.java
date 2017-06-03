package org.aviran.woodpecker;

import org.aviran.woodpecker.annotations.Put;

/**
 * Created by Aviran Abady on 6/2/17.
 */

public class PutRequest extends PostRequest {
    public PutRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);
        super.setRequestMethod("PUT");
    }

    @Override
    public String getRelativePath() {
        Put requestAnnotation = peck.getRequest().getClass().getAnnotation(Put.class);
        return requestAnnotation.value();
    }
}
