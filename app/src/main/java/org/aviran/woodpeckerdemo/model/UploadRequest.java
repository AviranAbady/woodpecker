package org.aviran.woodpeckerdemo.model;

import org.aviran.woodpecker.WoodpeckerFileStream;
import org.aviran.woodpecker.WoodpeckerProgressListener;
import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.File;
import org.aviran.woodpecker.annotations.Param;
import org.aviran.woodpecker.annotations.Post;
import org.aviran.woodpecker.annotations.Progress;

/**
 * Created by Aviran Abady on 6/3/17.
 */

@Post("/upload")
public class UploadRequest extends WoodpeckerRequest {
    @Param
    private String uploadToken;

    @File
    private WoodpeckerFileStream fileUpload;

    @File
    private WoodpeckerFileStream anotherFileUpload;

    @Progress
    private WoodpeckerProgressListener progressListener;

    public UploadRequest(String uploadToken,
                         WoodpeckerFileStream fileUpload,
                         WoodpeckerFileStream anotherFileUpload,
                         WoodpeckerProgressListener progressListener) {
        this.uploadToken = uploadToken;
        this.fileUpload = fileUpload;
        this.anotherFileUpload = anotherFileUpload;
        this.progressListener = progressListener;
    }
}
