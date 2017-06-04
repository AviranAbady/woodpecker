package org.aviran.woodpecker;

import java.io.InputStream;

/**
 * Created by Aviran Abady on 6/3/17.
 */

public class WoodpeckerFileStream {
    private String fileName;
    private InputStream stream;
    private String mimeType;

    public WoodpeckerFileStream(String fileName, InputStream stream, String mimeType) {
        this.fileName = fileName;
        this.stream = stream;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getMimeType() {
        return mimeType;
    }
}
