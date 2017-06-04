package org.aviran.woodpecker;

import org.aviran.woodpecker.annotations.File;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Head;
import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Post;
import org.aviran.woodpecker.annotations.Put;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Aviran Abady on 5/30/17.
 */

class Peck {
    private final Woodpecker woodpecker;
    private final WoodpeckerRequest request;
    private final RequestType type;
    private WoodpeckerResponse response;
    private boolean requestMultipart;
    private boolean requestWithParameters;

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
                return;
            }
            else if(field.getAnnotation(Param.class) != null) {
                requestWithParameters = true;
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
        this.response = response;
    }

    public Woodpecker getWoodpecker() {
        return woodpecker;
    }
}
