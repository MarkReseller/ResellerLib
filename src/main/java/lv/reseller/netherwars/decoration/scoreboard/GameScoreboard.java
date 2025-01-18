package lv.reseller.netherwars.decoration.scoreboard;

import lv.reseller.lib.animation.Frame;
import lv.reseller.netherwars.logic.TeamColor;
import org.bukkit.plugin.Plugin;

public interface GameScoreboard {

    Plugin getPlugin();

    PlayerScoreboard getScoreboard(String playerName);

    void addScoreboard(String playerName);

    void removeScoreboard(String playerName);

    String getHeader();

    void setHeader(String header);

    boolean isHeaderAnimated();

    void setHeaderAnimated(boolean animated);

    void setHeaderFrames(Frame<String>[] frames);

    Frame<String>[] getHeaderFrames();

    void putTeamState(TeamColor teamColor, int health, int maxHealth, int teamSize);

    void removeTeamState(TeamColor teamColor);

    void clearTeamStates();

}
