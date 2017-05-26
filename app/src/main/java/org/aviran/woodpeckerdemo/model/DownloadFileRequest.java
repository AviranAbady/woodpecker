package org.aviran.woodpeckerdemo.model;

import org.aviran.woodpecker.WoodpeckerProgressListener;
import org.aviran.woodpecker.WoodpeckerRequest;
import org.aviran.woodpecker.annotations.Get;
import org.aviran.woodpecker.annotations.Progress;

/**
 * Created by Aviran Abady on 6/7/17.
 */

@Get("/woodpecker.jpg")
public class DownloadFileRequest extends WoodpeckerRequest {
    @Progress
    WoodpeckerProgressListener progressListener;

    public DownloadFileRequest(WoodpeckerProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
