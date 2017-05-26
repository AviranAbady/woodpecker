/*
 * Copyright (C) 2017 Aviran Abady.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aviran.woodpecker;

import java.util.ArrayList;
import java.util.List;

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
                GetRequest.create(peck);
                return;
            case POST:
                PostRequest.create(peck);
                return;
            case HEAD:
                HeadRequest.create(peck);
            default:
                throw new WoodpeckerException("Unknown request type");
        }
    }

    protected void handleError(WoodpeckerResponse response) {
        if (errorHandler != null) {
            errorHandler.onError(response);
        }
    }
}
