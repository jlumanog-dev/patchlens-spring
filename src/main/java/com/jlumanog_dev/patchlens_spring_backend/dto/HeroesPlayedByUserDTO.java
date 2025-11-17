package com.jlumanog_dev.patchlens_spring_backend.dto;

public class HeroesPlayedByUserDTO {
    private int hero_id;
    private long last_played;
    private int games;
    private int win;
    private int with_games;
    private int with_win;
    private int against_games;
    private int against_win;
    private String img;
    private String[] roles;

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    private String localized_name;

    public String getLocalized_name() {
        return localized_name;
    }
    public void setLocalized_name(String localized_name) {
        this.localized_name = localized_name;
    }

    public int getHero_id() {
        return hero_id;
    }

    public void setHero_id(int hero_id) {
        this.hero_id = hero_id;
    }

    public long getLast_played() {
        return last_played;
    }

    public void setLast_played(long last_played) {
        this.last_played = last_played;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getWith_games() {
        return with_games;
    }

    public void setWith_games(int with_games) {
        this.with_games = with_games;
    }

    public int getWith_win() {
        return with_win;
    }

    public void setWith_win(int with_win) {
        this.with_win = with_win;
    }

    public int getAgainst_games() {
        return against_games;
    }

    public void setAgainst_games(int against_games) {
        this.against_games = against_games;
    }

    public int getAgainst_win() {
        return against_win;
    }

    public void setAgainst_win(int against_win) {
        this.against_win = against_win;
    }
}
