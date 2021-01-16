/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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


