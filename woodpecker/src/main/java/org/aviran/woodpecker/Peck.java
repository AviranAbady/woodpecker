package org.aviran.woodpecker;

import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Head;
import org.aviran.woodpecker.annotations.Post;

import java.lang.annotation.Annotation;

/**
 * Created by Aviran Abady on 5/30/17.
 */

class Peck {
    private final Woodpecker woodpecker;
    private final WoodpeckerRequest request;
    private final RequestType type;
    private WoodpeckerResponse response;

    public Peck(WoodpeckerRequest request, Woodpecker woodpecker) {
        this.request = request;
        this.woodpecker = woodpecker;
        this.type = setType();
    }

    private RequestType setType() {
        // todo refactor to []getAnnotations()
        Annotation annotation = request.getClass().getAnnotation(Get.class);
        if(annotation != null) {
            return RequestType.GET;
        }

        annotation = request.getClass().getAnnotation(Post.class);
        if(annotation != null) {
            return RequestType.POST;
        }

        annotation = request.getClass().getAnnotation(Head.class);
        if(annotation != null) {
            return RequestType.HEAD;
        }

        throw new WoodpeckerException("Request is not annotation for request type");
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
