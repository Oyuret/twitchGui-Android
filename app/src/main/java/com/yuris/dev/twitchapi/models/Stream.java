package com.yuris.dev.twitchapi.models;

/**
 * Created by yuris on 2016-07-12.
 */
public class Stream {
    private String name;
    private String displayName;
    private String game;
    private Integer viewers;
    private Integer views;
    private Integer followers;
    private Double averageFps;
    private Integer delay;
    private Boolean mature;
    private String status;
    private String language;
    private String previewSmall;
    private String previewMedium;
    private String previewLarge;
    private Boolean online;

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGame() {
        return game;
    }

    public Integer getViewers() {
        return viewers;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getFollowers() {
        return followers;
    }

    public Double getAverageFps() {
        return averageFps;
    }

    public Integer getDelay() {
        return delay;
    }

    public Boolean getMature() {
        return mature;
    }

    public String getStatus() {
        return status;
    }

    public String getLanguage() {
        return language;
    }

    public String getPreviewSmall() {
        return previewSmall;
    }

    public String getPreviewMedium() {
        return previewMedium;
    }

    public String getPreviewLarge() {
        return previewLarge;
    }

    public Boolean getOnline() {
        return online;
    }

    @Override
    public String toString() {
        return "Stream{" +
                "displayName='" + displayName + '\'' +
                ", game='" + game + '\'' +
                '}';
    }

    public static class StreamFactory {
        private Stream stream;

        public StreamFactory() {
            this.stream = new Stream();
        }

        public StreamFactory withName(String name) {
            this.stream.name = name;
            return this;
        }

        public StreamFactory withDisplayName(String displayName) {
            this.stream.displayName = displayName;
            return this;
        }

        public StreamFactory withGame(String game) {
            this.stream.game = game;
            return this;
        }

        public StreamFactory withViewers(Integer viewers) {
            this.stream.viewers = viewers;
            return this;
        }

        public StreamFactory withViews(Integer views) {
            this.stream.views = views;
            return this;
        }

        public StreamFactory withFollowers(Integer followers) {
            this.stream.followers = followers;
            return this;
        }

        public StreamFactory withAverageFps(Double averageFps) {
            this.stream.averageFps = averageFps;
            return this;
        }

        public StreamFactory withDelay(Integer delay) {
            this.stream.delay = delay;
            return this;
        }

        public StreamFactory withMature(Boolean mature) {
            this.stream.mature = mature;
            return this;
        }

        public StreamFactory withStatus(String status) {
            this.stream.status = status;
            return this;
        }

        public StreamFactory withLanguage(String language) {
            this.stream.language = language;
            return this;
        }

        public StreamFactory withPreviewSmall(String previewSmall) {
            this.stream.previewSmall = previewSmall;
            return this;
        }

        public StreamFactory withPreviewMedium(String previewMedium) {
            this.stream.previewMedium = previewMedium;
            return this;
        }

        public StreamFactory withPreviewLarge(String previewLarge) {
            this.stream.previewLarge = previewLarge;
            return this;
        }

        public StreamFactory withOnline(Boolean online) {
            this.stream.online = online;
            return this;
        }

        public Stream build() {
            return this.stream;
        }
    }
}
