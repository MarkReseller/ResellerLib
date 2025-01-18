package lv.reseller.netherwars.decoration.scoreboard;

import lv.reseller.lib.scoreboard.ScoreboardImplementation;
import lv.reseller.netherwars.util.Chat;

public class LobbyScoreboard extends ScoreboardImplementation {

    public LobbyScoreboard() {
        setDisplayName(Chat.colorize("&c&lNetherWars"));

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            builder.append(" ");
            add(builder.toString());
        }
    }

    public void updatePlayerCount(int playerCount, int maxPlayerCount) {
        set(1, Chat.colorize("Players: &a" + playerCount + "&f/&a" + maxPlayerCount));
    }

    public void updateLobbyState(int time) {
        set(3, Chat.colorize((time == -1 ? "Waiting..." : "Starting: &e" + time)));
    }

}
