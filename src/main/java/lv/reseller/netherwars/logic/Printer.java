package lv.reseller.netherwars.logic;

import java.io.PrintStream;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class Printer {

    private final Game game;

    protected Printer(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void printState(PrintStream out) {
        out.print("State: " + game.getState());
        out.print("Max mexus health: " + game.getMaxMexusHealth());
        out.print("Team capacity: " + game.getTeamCapacity());
        out.print("Members capacity: " + game.getGameCapacity());
        out.print("Teams:");
        for (Team team : game.getTeams().values()) {
            printTeam(out, team);
        }
        out.print("Members:");
        for (Member member : game.getMembers().values()) {
            printMember(out, member);
        }
        out.print("Winner: " + ((game.getWinner() == null) ? "null" : game.getWinner()));
    }

    protected void printTeam(PrintStream out, Team team) {
        TeamColor teamColor = team.color;
        out.print("  " + teamColor);
        if(team.initialized) {
            out.print("    IsAlive: " + team.isAlive());
            Mexus mexus = team.mexus;
            out.print("    Mexus: " + (mexus != null ? "" : "destroyed"));
            if(mexus != null) {
                out.print("      Health: " + mexus.health);
            }
        }
    }

    protected void printMember(PrintStream out, Member member) {
        out.print("  " + member.name);
        if(member.getGame().getState().isStarted()) {
            out.print("    Respawning: " + member.respawning);
            out.print("    Alive: " + member.alive);
            out.print("    Kills: " + member.stat.kills);
            out.print("    FinalKills: " + member.stat.finalKills);
            out.print("    Deaths: " + member.stat.deaths);
            out.print("    MexusHitCount: " + member.stat.mexusHitCount);
            out.print("    MexusedDestroyed: " + member.stat.mexusesDestroyed);
        }
    }

}
