package org.aviran.woodpecker;

import java.util.List;

/**
 * Created by Aviran Abady on 5/30/17.
 */

class Peck {
    private final Woodpecker woodpecker;
    private final WoodpeckerRequest request;
    private WoodpeckerResponse response;

    public Peck(WoodpeckerRequest request, Woodpecker woodpecker) {
        this.request = request;
        this.woodpecker = woodpecker;
    }

    public WoodpeckerRequest getRequest() {
        return request;
    }

    public WoodpeckerResponse getResponse() {
        return response;
    }

    public void setResponse(WoodpeckerResponse response) {
        this.response = response;
    }

    public Woodpecker getWoodpecker() {
        return woodpecker;
    }
}
