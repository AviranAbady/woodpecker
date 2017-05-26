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

import org.aviran.woodpecker.annotations.File;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Head;
import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Post;
import org.aviran.woodpecker.annotations.Progress;
import org.aviran.woodpecker.annotations.Put;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class Peck {
    private final Woodpecker woodpecker;
    private final WoodpeckerRequest request;
    private final RequestType type;
    private WoodpeckerResponse response;
    private boolean requestMultipart;
    private boolean requestWithParameters;
    private WoodpeckerProgressListener progressListener;
    private boolean responseTypeStream;
    private InputStream responseStream;

    public Peck(WoodpeckerRequest request, Woodpecker woodpecker) {
        this.request = request;
        this.woodpecker = woodpecker;
        this.type = setType();
        scanRequestFields();
    }

    private RequestType setType() {
        Annotation[] annotations = request.getClass().getDeclaredAnnotations();
        if(annotations.length != 1) {
            throw new WoodpeckerException("Request class should have only one annotation");
        }

        Annotation annotation = annotations[0];

        if (annotation instanceof Get) {
            return RequestType.GET;
        }
        else if (annotation instanceof Head) {
            return RequestType.HEAD;
        }
        else if(annotation instanceof Post) {
            return RequestType.POST;
        }
        else if(annotation instanceof Put) {
            return RequestType.PUT;
        }

        throw new WoodpeckerException("Request class missing request method annotation");
    }

    private void scanRequestFields() {
        Field[] fields = request.getClass().getDeclaredFields();
        if(fields == null || fields.length == 0) {
            return;
        }

        for (Field field : fields) {
            if(field.getAnnotation(File.class) != null) {
                requestMultipart = true;
            }
            else if(field.getAnnotation(Param.class) != null) {
                requestWithParameters = true;
            }
            else if(field.getAnnotation(Progress.class) != null) {
                field.setAccessible(true);
                try {
                    if(this.progressListener != null) {
                        throw new WoodpeckerException("Request already has a progress listener");
                    }
                    this.progressListener = (WoodpeckerProgressListener) field.get(request);
                } catch (IllegalAccessException e) {
                    throw new WoodpeckerException("Unable to set request progress listener");
                }
            }
        }
    }

    public boolean isRequestMultipart() {
        return requestMultipart;
    }

    public boolean isRequestWithParameters() {
        return requestWithParameters;
    }

    public RequestType getType() {
        return type;
    }

    public WoodpeckerRequest getRequest() {
        return request;
    }

    public WoodpeckerResponse getResponse() {
        return response;
    }

    public void setResponse(WoodpeckerResponse response) {
        if(response.getType().equals(InputStream.class)) {
            responseTypeStream = true;
        }
        this.response = response;
    }

    public Woodpecker getWoodpecker() {
        return woodpecker;
    }

    public WoodpeckerProgressListener getProgressListener() {
        return progressListener;
    }

    public boolean isResponseTypeStream() {
        return responseTypeStream;
    }

    public void setResponseStream(ByteArrayInputStream responseStream) {
        this.responseStream = responseStream;
    }

    public InputStream getResponseStream() {
        return responseStream;
    }
}
