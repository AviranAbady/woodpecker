package org.aviran.woodpeckerapp.model;

import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Path;

/**
 * Created by Aviran Abady on 5/31/17.
 */

@Get("/item/{id}")
public class ItemRequest extends WoodpeckerRequest {
    @Path
    private int id;

    public ItemRequest(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
