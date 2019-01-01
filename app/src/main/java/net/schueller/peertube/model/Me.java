package net.schueller.peertube.model;

public class Me {

    private Integer id;
    private Account account;
    private Boolean autoPlayVideo;
    private Boolean blocked;
    private String blockedReason;
    private String createdAt;
    private String email;
    private String emailVerified;
    private String nsfwPolicy;
    private Integer role;
    private String roleLabel;
    private String username;

  //  private VideoChannels videoChannels;
    private Integer videoQuota;
    private Integer videoQuotaDaily;
    private String webTorrentEnabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Boolean getAutoPlayVideo() {
        return autoPlayVideo;
    }

    public void setAutoPlayVideo(Boolean autoPlayVideo) {
        this.autoPlayVideo = autoPlayVideo;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public String getBlockedReason() {
        return blockedReason;
    }

    public void setBlockedReason(String blockedReason) {
        this.blockedReason = blockedReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getNsfwPolicy() {
        return nsfwPolicy;
    }

    public void setNsfwPolicy(String nsfwPolicy) {
        this.nsfwPolicy = nsfwPolicy;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getRoleLabel() {
        return roleLabel;
    }

    public void setRoleLabel(String roleLabel) {
        this.roleLabel = roleLabel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getVideoQuota() {
        return videoQuota;
    }

    public void setVideoQuota(Integer videoQuota) {
        this.videoQuota = videoQuota;
    }

    public Integer getVideoQuotaDaily() {
        return videoQuotaDaily;
    }

    public void setVideoQuotaDaily(Integer videoQuotaDaily) {
        this.videoQuotaDaily = videoQuotaDaily;
    }

    public String getWebTorrentEnabled() {
        return webTorrentEnabled;
    }

    public void setWebTorrentEnabled(String webTorrentEnabled) {
        this.webTorrentEnabled = webTorrentEnabled;
    }
}


