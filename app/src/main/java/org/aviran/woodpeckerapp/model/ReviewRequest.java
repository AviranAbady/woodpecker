package org.aviran.woodpeckerapp.model;

import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.Post;

/**
 * Created by Aviran Abady on 5/31/17.
 */


@Post("/review")
public class ReviewRequest extends WoodpeckerRequest {
    private int itemId;
    private String name;
    private String text;

    public ReviewRequest(int itemId, String name, String text) {
        this.itemId = itemId;
        this.name = name;
        this.text = text;
    }
}
