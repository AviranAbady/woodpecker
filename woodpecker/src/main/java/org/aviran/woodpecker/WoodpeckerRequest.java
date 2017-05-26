package org.aviran.woodpecker;

/**
 * Created by Aviran Abady on 5/27/17.
 */

public abstract class WoodpeckerRequest {
    int requestId;

    protected void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}
