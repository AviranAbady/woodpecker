package org.aviran.woodpecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aviran Abady on 5/27/17.
 */

public class Woodpecker {
    // Static
    public static final String LOG_TAG = "Woodpecker";
    private static WoodpeckerSettings settings;

    public static void initialize(WoodpeckerSettings woodpeckerSettings) {
        if (woodpeckerSettings.getBaseURL() == null) {
            throw new WoodpeckerException("You must initialize woodpecker with a baseURL");
        }
        settings = woodpeckerSettings;
    }

    public static WoodpeckerSettings getSettings() {
        if (settings == null) {
            throw new WoodpeckerException("woodpecker is not initialized");
        }

        return Woodpecker.settings;
    }

    public static Woodpecker begin() {
        if (settings == null) {
            throw new WoodpeckerException("woodpecker is not initialized");
        }
        return new Woodpecker();
    }

    // Non static

    private List<Peck> pecks;
    private WoodpeckerError errorHandler;
    private int requestsCount;
    private List<WoodpeckerRequest> requests;


    private Woodpecker() {
        pecks = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public String getBaseURL() {
        return Woodpecker.settings.getBaseURL();
    }

    public Woodpecker then(WoodpeckerResponse response) {
        if (pecks.size() == 0) {
            throw new WoodpeckerException("There is no request to add a response for");
        }


        response.setRequestsList(requests);
        Peck lastPeck = pecks.get(pecks.size() - 1);
        if (lastPeck.getResponse() != null) {
            throw new WoodpeckerException("Request already has a response");
        }

        response.setRequestId(lastPeck.getRequest().getRequestId());
        lastPeck.setResponse(response);
        return this;
    }

    public Woodpecker request(WoodpeckerRequest request) {
        request.setRequestId(requestsCount++);
        pecks.add(new Peck(request, this));
        requests.add(request);
        return this;
    }

    public void error(WoodpeckerError error) {

        errorHandler = error;
        peck();
    }

    protected void peck() {
        if (pecks.size() == 0) {
            return;
        }

        Peck peck = pecks.remove(0);
        switch (peck.getType()) {
            case GET:
                WoodpeckerNetwork.get(peck);
                return;
            case POST:
                WoodpeckerNetwork.post(peck);
                return;
            case HEAD:
                WoodpeckerNetwork.head(peck);
            default:
                return;
        }
    }

    protected void handleError(WoodpeckerResponse response) {
        if (errorHandler != null) {
            errorHandler.onError(response);
        }
    }
}
