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

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.aviran.woodpecker.annotations.Head;

class HeadRequest extends HttpRequest {

    public static void create(final Peck peck) {
        final HeadRequest httpTask = new HeadRequest(peck, new WoodpeckerHttpResponse(peck));
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private HeadRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);
        String parameters = parseRequestPayload(true);
        if(parameters.length() > 0) {
            urlBuilder.append("?").append(parameters);
        }
    }

    public String performRequest(HttpURLConnection httpConnection) {
        try {
            httpConnection.setRequestMethod("HEAD");
            addHeaders(httpConnection);
            peck.getResponse().setResponseCode(httpConnection.getResponseCode());
            peck.getResponse().setHeaders(httpConnection.getHeaderFields());
            return "";
        } catch (IOException e) {
            generateErrorResponse(peck.getResponse(), httpConnection);
            return null;
        }
        finally {
            httpConnection.disconnect();
        }
    }

    @Override
    public String getRelativePath() {
        Head requestAnnotation = peck.getRequest().getClass().getAnnotation(Head.class);
        return requestAnnotation.value();

    }
}
