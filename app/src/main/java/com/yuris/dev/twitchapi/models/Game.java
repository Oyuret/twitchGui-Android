package com.yuris.dev.twitchapi.models;

/**
 * Created by yuris on 2016-07-12.
 */
public class Game {
    private String name;
    private String logo;
    private Integer viewers;
    private Integer channels;

    private Game() {}

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public Integer getViewers() {
        return viewers;
    }

    public Integer getChannels() {
        return channels;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                ", viewers=" + viewers +
                ", channels=" + channels +
                '}';
    }

    public static class GameFactory {
        private Game game;

        public GameFactory() {
            this.game = new Game();
        }

        public GameFactory withName(String name) {
            this.game.name = name;
            return this;
        }

        public GameFactory withLogo(String logo) {
            this.game.logo = logo;
            return this;
        }

        public GameFactory withViewers(Integer viewers) {
            this.game.viewers = viewers;
            return this;
        }

        public GameFactory withChannels(Integer channels) {
            this.game.channels = channels;
            return this;
        }

        public Game build() {
            return this.game;
        }
    }
}
