package net.schueller.peertube.model;

public class File {
    private Integer resolution;
    private String resolutionLabel;
    private String magnetUri;
    private Integer size;
    private String torrentUrl;
    private String fileUrl;

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public String getResolutionLabel() {
        return resolutionLabel;
    }

    public void setResolutionLabel(String resolutionLabel) {
        this.resolutionLabel = resolutionLabel;
    }

    public String getMagnetUri() {
        return magnetUri;
    }

    public void setMagnetUri(String magnetUri) {
        this.magnetUri = magnetUri;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getTorrentUrl() {
        return torrentUrl;
    }

    public void setTorrentUrl(String torrentUrl) {
        this.torrentUrl = torrentUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
