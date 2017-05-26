package org.aviran.woodpecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aviran Abady on 5/27/17.
 */

public class Woodpecker {
    // todo refactor
    public String getBaseURL() {
        return "http://www.abady.org/woodpecker";
    }

    public static Woodpecker begin() {
        return new Woodpecker();
    }

    private List<Peck> pecks;
    private WoodpeckerError errorHandler;

    public Woodpecker() {
        pecks = new ArrayList<>();
    }

    public Woodpecker then(WoodpeckerResponse response) {
        if(pecks.size() == 0) {
            throw new WoodpeckerException("There is no request to add a response for");
        }

        Peck lastPeck = pecks.get(pecks.size() - 1);
        if(lastPeck.getResponse() != null) {
            throw new WoodpeckerException("Request already has a response");
        }

        lastPeck.setResponse(response);
        return this;
    }

    public Woodpecker request(WoodpeckerRequest request) {
        pecks.add(new Peck(request,this));
        return this;
    }

    public void error(WoodpeckerError error) {

        errorHandler = error;
        peck();
    }

    protected void peck() {
        if(pecks.size() == 0) {
            return;
        }

        WoodpeckerNetwork.get(pecks.remove(0));
    }
}
