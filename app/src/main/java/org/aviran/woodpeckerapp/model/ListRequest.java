package org.aviran.woodpeckerapp.model;

import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Param;

/**
 * Created by Aviran Abady on 5/27/17.
 */

@Get("/list")
public class ListRequest extends WoodpeckerRequest {
    @Param
    private int page;

    @Param
    private int pageSize;

    public ListRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
