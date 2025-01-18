package lv.reseller.netherwars.decoration.scoreboard;

import lv.reseller.netherwars.logic.TeamColor;
import lv.reseller.lib.scoreboard.IScoreboard;

public interface PlayerScoreboard extends IScoreboard {

    GameScoreboard getGameScoreboard();

    String getPlayerName();

    TeamColor getTeamColor();

    void setTeamColor(TeamColor teamColor);

    void setKills(int kills);

    void setFinalKills(int finalKills);

    void setDeaths(int deaths);

    void setMexusHits(int mexusHits);

    void setMexusDestroyed(int mexusDestroyed);
}
