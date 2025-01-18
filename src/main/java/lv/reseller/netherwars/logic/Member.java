package lv.reseller.netherwars.logic;

import lv.reseller.netherwars.decoration.DecoratedTeam;
import lv.reseller.netherwars.logic.exceptions.ExpectedStateException;
import lv.reseller.netherwars.logic.exceptions.GameException;

import java.util.List;

public abstract class Member {

    final Game game;
    final String name;
    Team preferredTeam;

    Team team;
    boolean respawning;
    boolean alive;
    MemberStat stat;

    protected Member(Game game, String name) {
        this.game = game;
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public Team getPreferredTeam() {
        return preferredTeam;
    }

    public void prefferTeam(Team team) {
        game.expectState(ExpectedStateException.LOBBY);
        this.preferredTeam = team;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isRespawning() {
        return respawning;
    }

    public boolean isAlive() {
        return alive;
    }

    public MemberStat getStat() {
        return stat;
    }

    protected void checkMemberDestroyed() {
        if(alive)
            throw new GameException("Member must be destroyed");
    }

    protected void checkRespawning() {
        if(!respawning)
            throw new GameException("Member must be respawning");
    }

    protected void checkNoTeam(Team team) {
        if(this.team.equals(team))
            throw new GameException("Member " + name + " must not be in " + team.color);
    }

    public void checkAlive() {
        if(!alive)
            throw new GameException("Member must be alive");
    }

    protected void checkNotRespawning() {
        if(respawning)
            throw new GameException("Member must not be respawning");
    }

    public void checkHasTeam() {
        if(!hasTeam())
            throw new GameException("Member must have team");
    }

    public void checkHasNoTeam() {
        if(hasTeam())
            throw new GameException("Member must not have team");
    }

    public boolean hasTeam() {
        return team != null;
    }

    public void death() {
        death(null);
    }

    public void death(Member killer) {
        game.expectState(ExpectedStateException.ACTIVE);
        checkAlive();
        checkNotRespawning();
        if(killer != null) {
            killer.checkAlive();
            killer.checkNotRespawning();
            killer.checkNoTeam(this.team);
        }
        if(!this.team.mexus.isDestroyed()) {
            respawning = true;
            onDeath(killer);
        } else {
            alive = false;
            onDestroy(killer);
        }
    }

    protected void onDeath(Member killer) {
        stat.deaths++;
        if(killer != null) {
            killer.stat.kills++;
        }
    }

    protected void onDestroy(Member killer) {
        stat.deaths++;
        if(killer != null) {
            killer.stat.finalKills++;
        }
        List<Team> aliveTeams = this.game.getAliveTeams();
        if(aliveTeams.size() == 1) {
            this.game.end(aliveTeams.get(0));
        }
    }

    public void respawn() {
        checkRespawning();
        respawning = false;
        onRespawn();
    }

    protected abstract void onRespawn();

    public void quit() {
        game.leaveMember(this);
    }

}
