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

import java.util.HashMap;
import java.util.Map;

public class WoodpeckerSettings {
    private String baseURL;
    private Map<String, String> headers;

    public WoodpeckerSettings(String baseURL) {
        this.baseURL = baseURL;
        headers = new HashMap<>();
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeader(String name) {
        if(headers.containsKey(name)) {
            headers.remove(name);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
