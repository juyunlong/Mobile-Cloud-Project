package com.jlu.mcloud.communicate.fileio;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Created by koko on 17-3-20.
 */
public interface FileIoHandler {
    public FileEntity readFile(String fileId) throws IOException;

    public String writeFile(FileEntity fileEntity) throws IOException;
}
