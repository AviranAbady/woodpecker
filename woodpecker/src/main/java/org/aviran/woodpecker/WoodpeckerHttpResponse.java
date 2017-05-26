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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.lang.reflect.Type;

public class WoodpeckerHttpResponse {

    private final Peck peck;

    public WoodpeckerHttpResponse(Peck peck) {
        this.peck = peck;
    }

    public <T> void httpSuccess(String data) {
        if (peck.isResponseTypeStream()) {
            peck.getResponse().onSuccess(peck.getResponseStream());
            try {
                peck.getResponseStream().close();
            } catch (IOException e) {

            }
            return;
        }

        if (data == null) {
            peck.getResponse().onError(new WoodpeckerException(""));
        } else {
            WoodpeckerResponse response = peck.getResponse();
            T t;
            try {
                Type type = response.getType();
                if (type.equals(String.class) || peck.getType() == RequestType.HEAD) {
                    response.onSuccess(data);
                } else {
                    t = new Gson().fromJson(data, response.getType());
                    response.onSuccess(t);
                }
            } catch (JsonSyntaxException jse) {
                response.onError(new WoodpeckerException(data, jse));
            }
        }
    }

    public void httpError() {
        peck.getWoodpecker().handleError(peck.getResponse());
    }
}