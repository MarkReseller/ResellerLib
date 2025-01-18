package lv.reseller.netherwars.decoration.scoreboard;

import javafx.util.Pair;
import lv.reseller.lib.animation.Animation;
import lv.reseller.lib.animation.Frame;
import lv.reseller.netherwars.util.Chat;
import lv.reseller.netherwars.logic.TeamColor;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScoreboardImplementation implements GameScoreboard {

    private final Plugin plugin;
    private final Map<String, PlayerScoreboardImplementation> scoreboards;

    private String header;
    private final Animation<String> headerAnimation;
    private boolean headerAnimated;
    final List<TeamState> teamStates;

    public GameScoreboardImplementation(Plugin plugin) {
        this.plugin = plugin;
        this.scoreboards = new HashMap<>();

        this.header = Chat.colorize("&c&lNetherWars");
        Frame<String>[] frames = new Frame[1];
        frames[0] = new Frame<>("NetherWars", 20);
        this.headerAnimation = new Animation<>(plugin, frames, (frame) -> {
            for(PlayerScoreboard scoreboard : scoreboards.values()) {
                scoreboard.setDisplayName(frame.getItem());
            }
        });
        this.teamStates = new ArrayList<>();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public PlayerScoreboard getScoreboard(String playerName) {
        return scoreboards.get(playerName);
    }

    @Override
    public void addScoreboard(String playerName) {
        PlayerScoreboardImplementation scoreboard = new PlayerScoreboardImplementation(this, playerName);
        scoreboards.put(playerName, scoreboard);
        updateHeader(scoreboard);
        initLines(scoreboard);
        scoreboard.updateTeamColor();
        updateTeamStateLines(scoreboard);
        scoreboard.updateKills();
        scoreboard.updateFinalKills();
        scoreboard.updateDeaths();
        scoreboard.updateMexusHits();
        scoreboard.updateMexusDestroyed();
    }

    private void initLines(PlayerScoreboard scoreboard) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 16; i++) {
            builder.append(" ");
            scoreboard.add(builder.toString());
        }
    }

    @Override
    public void removeScoreboard(String playerName) {
        scoreboards.remove(playerName);
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public void setHeader(String header) {
        this.header = header;
        updateHeaders();
    }

    private void updateHeader(PlayerScoreboardImplementation scoreboard) {
        if(headerAnimated) {
            scoreboard.setDisplayName(headerAnimation.getCurrentFrame().getItem());
        } else {
            scoreboard.setDisplayName(header);
        }
    }

    private void updateHeaders() {
        if(headerAnimated) {
            for(PlayerScoreboard scoreboard : scoreboards.values()) {
                scoreboard.setDisplayName(headerAnimation.getCurrentFrame().getItem());
            }
        } else {
            for(PlayerScoreboard scoreboard : scoreboards.values()) {
                scoreboard.setDisplayName(header);
            }
        }
    }

    @Override
    public boolean isHeaderAnimated() {
        return headerAnimated;
    }

    @Override
    public void setHeaderAnimated(boolean animated) {
        if(this.headerAnimated == animated) return;
        this.headerAnimated = animated;
        headerAnimation.setPlaying(animated);
        updateHeaders();
    }

    @Override
    public void setHeaderFrames(Frame<String>[] frames) {
        this.headerAnimation.setFrames(frames);
        updateHeaders();
    }

    @Override
    public Frame<String>[] getHeaderFrames() {
        return headerAnimation.getFrames();
    }

    @Override
    public void putTeamState(TeamColor teamColor, int health, int maxHealth, int teamSize) {
        TeamState teamState = new TeamState(teamColor, health, maxHealth, teamSize);
        int index = teamStates.indexOf(teamState);
        if(index == -1) {
            teamStates.add(teamState);
            updateTeamStateLine(getTeamStateLineIndex(teamStates.size() - 1));
        } else {
            teamStates.set(index, teamState);
            updateTeamStateLine(getTeamStateLineIndex(index));
        }
    }

    @Override
    public void removeTeamState(TeamColor teamColor) {
        TeamState teamState = new TeamState(teamColor, 0, 0, 0);
        int index = teamStates.indexOf(teamState);
        if(index != -1) {
            teamStates.remove(teamState);
            updateTeamStateLines();
        }
    }

    @Override
    public void clearTeamStates() {
        teamStates.clear();
        updateTeamStateLines();
    }

    private void updateTeamStateLines() {
        for(int i = 0; i < 7; i++) {
            updateTeamStateLine(i);
        }
    }

    private void updateTeamStateLines(PlayerScoreboardImplementation scoreboard) {
        for(int i = 0; i < 7; i++) {
            updateTeamStateLine(i, scoreboard);
        }
    }


    private String getEmptyLine(int scoreboardIndex) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < scoreboardIndex + 1; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private void updateTeamStateLine(int teamStateLineIndex) {
        for(PlayerScoreboardImplementation scoreboard : scoreboards.values()) {
            updateTeamStateLine(teamStateLineIndex, scoreboard);
        }
    }

    private void updateTeamStateLine(int teamStateLineIndex, PlayerScoreboardImplementation scoreboard) {
        Pair<Integer, Integer> teamStateIndexPair = getTeamStateIndexPair(teamStateLineIndex);
        int firstIndex = teamStateIndexPair.getKey();
        int secondIndex = teamStateIndexPair.getValue();
        String line;
        if(firstIndex < teamStates.size()) {
            TeamState firstState = teamStates.get(firstIndex);
            line = representState(firstState);
            if(secondIndex < teamStates.size()) {
                TeamState secondState = teamStates.get(secondIndex);
                line += " ";
                line += representState(secondState);
            }
        } else {
            line = getEmptyLine(teamStateLineIndex + 7);
        }
        scoreboard.set(teamStateLineIndex + 7, line);
    }

    private String representState(TeamState state) {
        TeamColor teamColor = state.getTeamColor();
        int health = state.getMexusHealth();
        String stateName = Chat.leftPadIgnoreColor(
                Chat.colorize(teamColor.getChatColor() + "&l" + state.getTeamColor().toString()),
                6
        );
        String stateHealth = Chat.rightPadIgnoreColor(
                 Chat.colorize((health > 0 ? "&a" + health : (health == 0 ? (state.getTeamSize() > 0 ? "&c" + state.getTeamSize() : "&4&l✘") : "&cN/A"))),
                3
        );

        return stateName + " " + stateHealth;
    }

    private int getTeamStateLineIndex(int teamStateIndex) {
        return teamStateIndex / 2;
    }

    private Pair<Integer, Integer> getTeamStateIndexPair(int scoreboardIndex) {
        int firstIndex = scoreboardIndex * 2;
        int secondIndex = firstIndex + 1;
        return new Pair<>(firstIndex, secondIndex);
    }

}
