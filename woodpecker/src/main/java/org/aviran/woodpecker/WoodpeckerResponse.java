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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

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