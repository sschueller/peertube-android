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

import java.util.ArrayList;

public class StreamingPlaylist {

    private Integer id;
    private Integer type;
    private String playlistUrl;
    private String segmentsSha256Url;
    private ArrayList<String> redundancies;
    private ArrayList<File> files;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(final Integer type) {
        this.type = type;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(final String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public String getSegmentsSha256Url() {
        return segmentsSha256Url;
    }

    public void setSegmentsSha256Url(final String segmentsSha256Url) {
        this.segmentsSha256Url = segmentsSha256Url;
    }

    public ArrayList<String> getRedundancies() {
        return redundancies;
    }

    public void setRedundancies(final ArrayList<String> redundancies) {
        this.redundancies = redundancies;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(final ArrayList<File> files) {
        this.files = files;
    }
}
