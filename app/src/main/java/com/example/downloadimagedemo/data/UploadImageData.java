package com.example.downloadimagedemo.data;

/**
 * Created on 23/06/2017.
 *
 * @author Joao Elvas
 */

public class UploadImageData {

    String file64Base;

    String mediaType;

    String tokenId;

    public UploadImageData(String file64Base, String mediaType, String tokenId) {
        this.file64Base = file64Base;
        this.mediaType = mediaType;
        this.tokenId = tokenId;
    }

    public String getFile64Base() {
        return file64Base;
    }

    public void setFile64Base(String file64Base) {
        this.file64Base = file64Base;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public String toString() {
        return "UploadImageData{" +
                "file64Base='" + file64Base + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
