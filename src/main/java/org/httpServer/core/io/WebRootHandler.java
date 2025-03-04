package org.httpServer.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {

    private File webRoot;

    WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webRoot = new File(webRootPath);
        if(!webRoot.exists() || !webRoot.isDirectory()){
            throw new WebRootNotFoundException("Webroot provided does not exists or is not a folder");
        }
    }

    private boolean CheckIfEndsWithSlash(String relativePath){
        return relativePath.endsWith("/");
    }

    private boolean CheckIfProvidedRelativePathExists(String relativePath){
        File file = new File(webRoot,relativePath);

        if(!file.exists()){
            return false;
        }
        try{
            if(file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())){
                return true;
            }
        }catch(IOException e){
            return false;
        }
        return false;
    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if(CheckIfEndsWithSlash(relativePath)){
            relativePath += "index.html";
        }

        if(!CheckIfProvidedRelativePathExists(relativePath)){
            throw new FileNotFoundException("File not found:" + relativePath);
        }

        File file = new File(webRoot,relativePath);

        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if(mimeType == null){
            return "application/octet-stream";
        }

        return mimeType;
    }

    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException, ReadFileException {
        if(CheckIfEndsWithSlash(relativePath)){
            relativePath += "index.html";
        }

        if(!CheckIfProvidedRelativePathExists(relativePath)){
            throw new FileNotFoundException("File not found:" + relativePath);
        }

        File file = new File(webRoot,relativePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        } catch (IOException e) {
            throw new ReadFileException();
        }
        return fileBytes;
    }
}
