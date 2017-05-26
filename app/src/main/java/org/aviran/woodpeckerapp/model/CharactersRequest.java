package org.aviran.woodpeckerapp.model;

import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Param;

/**
 * Created by Aviran Abady on 5/27/17.
 */

@Get("/characters")
public class CharactersRequest extends WoodpeckerRequest {
    @Param
    private int page;

    @Param
    private int pageSize;

    public CharactersRequest(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }
}
