package lv.reseller.netherwars.decoration.scoreboard;

import lv.reseller.netherwars.logic.TeamColor;
import lv.reseller.netherwars.util.Chat;
import lv.reseller.lib.scoreboard.ScoreboardImplementation;

public class PlayerScoreboardImplementation extends ScoreboardImplementation implements PlayerScoreboard{

    private final GameScoreboardImplementation gameScoreboard;
    private final String playerName;
    private TeamColor teamColor;
    private int kills;
    private int finalKills;
    private int deaths;
    private int mexusHits;
    private int mexusDestroyed;

    public PlayerScoreboardImplementation(GameScoreboardImplementation gameScoreboard, String playerName) {
        this.gameScoreboard = gameScoreboard;
        this.playerName = playerName;
    }

    @Override
    public GameScoreboard getGameScoreboard() {
        return gameScoreboard;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor;
        updateTeamColor();
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
        updateKills();
    }

    @Override
    public void setFinalKills(int finalKills) {
        this.finalKills = finalKills;
        updateFinalKills();
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
        updateDeaths();
    }

    @Override
    public void setMexusHits(int mexusHits) {
        this.mexusHits = mexusHits;
        updateMexusHits();
    }

    @Override
    public void setMexusDestroyed(int mexusDestroyed) {
        this.mexusDestroyed = mexusDestroyed;
        updateMexusDestroyed();
    }

    void updateTeamColor() {
        set(0, Chat.colorize("Team: " + representTeamColor()));
    }

    void updateKills() {
        set(1, Chat.colorize("Kills: &a" + kills));
    }

    void updateFinalKills() {
        set(2, Chat.colorize("Final kills: &a" + finalKills));
    }

    void updateDeaths() {
        set(3, Chat.colorize("Deaths: &a" + deaths));
    }

    void updateMexusHits() {
        set(4, Chat.colorize("Mexus hits: &a" + mexusHits));
    }

    void updateMexusDestroyed() {
        set(5, Chat.colorize("Mexus destroyed: &a" + mexusDestroyed));
    }

    String representTeamColor() {
        return teamColor != null ? teamColor.getChatColor() + teamColor.toString() : "&cN/A";
    }

}
