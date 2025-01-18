package lv.reseller.netherwars.logic;

import lv.reseller.netherwars.logic.exceptions.GameException;

import java.util.HashSet;
import java.util.Set;

public abstract class Team {

    final Game game;
    final TeamColor color;
    final Mexus mexus;

    boolean initialized;
    Set<Member> members;

    protected Team(Game game, TeamColor color) {
        this.game = game;
        this.color = color;
        this.mexus = newMexus();
    }

    public Game getGame() {
        return game;
    }

    public TeamColor getColor() {
        return color;
    }

    protected abstract Mexus newMexus();

    public Mexus getMexus() {
        return mexus;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize() {
        checkNotInitialized();
        initialized = true;
        members = new HashSet<>();
        mexus.health = Mexus.MAX_MEXUS_HEALTH;
        onInitialize();
    }

    protected abstract void onInitialize();

    public void deinitialize() {
        checkInitialized();
        onDeinitialize();
        initialized = false;
        for(Member member : members) {
            leaveMember(member);
        }
        members = null;
    }

    protected abstract void onDeinitialize();

    public Set<Member> getMembers() {
        return members;
    }

    public void checkInitialized() {
        if(!initialized) {
            throw new GameException("Team is not initialized");
        }
    }

    public void checkNotInitialized() {
        if(initialized) {
            throw new GameException("Team is already initialized");
        }
    }

    public void checkCapacity() {
        if(members.size() == game.getTeamCapacity())
            throw new GameException("Team is full");
    }

    public void checkInTeam(Member member) {
        if(!members.contains(member))
            throw new GameException("You are not in the team");
    }

    public void checkNotInTeam(Member member) {
        if(members.contains(member))
            throw new GameException("You are already in the team");
    }

    public void joinMember(Member member) {
        checkInitialized();
        checkNotInTeam(member);
        checkCapacity();
        this.members.add(member);
        member.team = this;
    }

    public void leaveMember(Member member) {
        checkInitialized();
        checkInTeam(member);
        this.members.remove(member);
        member.team = null;
    }

    public int getAliveSize() {
        checkInitialized();
        int aliveSize = 0;
        for(Member member : members) {
            if(member.isAlive())
                aliveSize++;
        }
        return aliveSize;
    }

    public boolean isAlive() {
        return getAliveSize() != 0;
    }


}
