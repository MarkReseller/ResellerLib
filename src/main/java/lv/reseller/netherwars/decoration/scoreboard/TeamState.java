package lv.reseller.netherwars.decoration.scoreboard;

import lv.reseller.netherwars.logic.TeamColor;

import java.util.Objects;

public class TeamState {

    private TeamColor teamColor;
    private int mexusHealth;
    private int maxHealth;
    private int teamSize;

    public TeamState(TeamColor teamColor, int mexusHealth, int maxHealth, int teamSize) {
        this.teamColor = teamColor;
        this.mexusHealth = mexusHealth;
        this.maxHealth = maxHealth;
        this.teamSize = teamSize;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public int getMexusHealth() {
        return mexusHealth;
    }

    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void setMexusHealth(int mexusHealth) {
        this.mexusHealth = mexusHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamState)) return false;
        TeamState teamState = (TeamState) o;
        return teamColor == teamState.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(teamColor);
    }

}
