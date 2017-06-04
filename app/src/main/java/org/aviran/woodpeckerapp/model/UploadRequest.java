package org.aviran.woodpeckerapp.model;

import org.aviran.woodpecker.WoodpeckerFileStream;
import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.File;
import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Post;

/**
 * Created by Aviran Abady on 6/3/17.
 */

@Post("/upload")
public class UploadRequest extends WoodpeckerRequest {
    @Param
    private String uploadToken;

    @File
    private WoodpeckerFileStream fileUpload;

    public UploadRequest(String uploadToken, WoodpeckerFileStream fileUpload) {
        this.uploadToken = uploadToken;
        this.fileUpload = fileUpload;
    }
}
