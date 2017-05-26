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
import org.aviran.woodpecker.annotations.Put;

public class PutRequest extends PostRequest {
    public static void create(final Peck peck) {
        final PutRequest httpTask = new PutRequest(peck, new WoodpeckerHttpResponse(peck));
        httpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private PutRequest(Peck peck, WoodpeckerHttpResponse listener) {
        super(peck, listener);
        super.setRequestMethod("PUT");
    }

    @Override
    public String getRelativePath() {
        Put requestAnnotation = peck.getRequest().getClass().getAnnotation(Put.class);
        return requestAnnotation.value();
    }
}
