package org.aviran.woodpecker;

import java.io.InputStream;

/**
 * Created by Aviran Abady on 6/3/17.
 */

public class WoodpeckerFileStream {
    private String fileName;
    private InputStream stream;

    public WoodpeckerFileStream(String fileName, InputStream stream) {
        this.fileName = fileName;
        this.stream = stream;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getStream() {
        return stream;
    }
}
