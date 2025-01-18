package lv.reseller.netherwars.logic;

import lv.reseller.netherwars.logic.exceptions.ExpectedStateException;
import lv.reseller.netherwars.logic.exceptions.GameException;

import java.util.*;

public abstract class Game {

    public static final int PLAYER_COUNT_TO_START = 2;
    private final Random random = new Random();
    private final Printer printer;

    State state;
    final Map<TeamColor, Team> teams;
    int teamCapacity;
    int maxMexusHealth;
    final Map<String, Member> members;
    Team winner;

    public Game() {
        state = State.DISABLED;

        teams = new HashMap<>();
        teamCapacity = 1;
        maxMexusHealth = 5;

        members = new HashMap<>();
        winner = null;

        printer = newPrinter();
    }

    protected abstract Printer newPrinter();

    public Printer getPrinter() {
        return printer;
    }

    public State getState() {
        return state;
    }

    void setState(State state) {
        State oldState = this.state;
        this.state = state;
        onStateChanged(oldState, state);
    }

    public void expectState(ExpectedStateException exception) {
        exception.expect(this.state);
    }

    protected abstract void onStateChanged(State oldState, State newState);

    protected Team checkTeam(TeamColor teamColor) {
        Team team = teams.get(teamColor);
        if(team == null)
            throw new GameException("Team color " + teamColor + " does not exist");
        return team;
    }

    protected void checkNoTeam(TeamColor teamColor) {
        if(teams.containsKey(teamColor))
            throw new GameException("Team color " + teamColor + " already exists");
    }

    public Map<TeamColor, Team> getTeams() {
        return teams;
    }

    protected abstract Team newTeam(TeamColor teamColor);

    public Team addTeam(TeamColor teamColor) {
        expectState(ExpectedStateException.DISABLED);
        checkNoTeam(teamColor);
        Team newTeam = newTeam(teamColor);
        teams.put(teamColor, newTeam);
        return newTeam;
    }

    public void removeTeam(TeamColor color) {
        expectState(ExpectedStateException.DISABLED);
        checkTeam(color);
        teams.remove(color);
    }

    public int getTeamCapacity() {
        return teamCapacity;
    }

    public void setTeamCapacity(int teamCapacity) {
        expectState(ExpectedStateException.DISABLED);
        this.teamCapacity = teamCapacity;
    }

    public int getGameCapacity() {
        return this.teams.size() * teamCapacity;
    }

    public int getMaxMexusHealth() {
        return maxMexusHealth;
    }

    public void setMaxMexusHealth(int maxMexusHealth) {
        expectState(ExpectedStateException.DISABLED);
        this.maxMexusHealth = maxMexusHealth;
    }

    public void enable() {
        expectState(ExpectedStateException.DISABLED);
        if(this.teams.size() < 2)
            throw new GameException("Teams must have at least 2 teams");
        if(teamCapacity < 1)
            throw new GameException("Team capacity must be at least 1");
        if(maxMexusHealth < 1)
            throw new GameException("Max mexus health must be at least 1");
        setState(State.WAITING);
        onEnable();
    }

    protected abstract void onEnable();

    public Map<String, Member> getMembers() {
        return members;
    }

    protected abstract Member newMember(String name);

    public Member getMember(String name) {
        return checkMember(name);
    }

    protected Member checkMember(String name) {
        Member member = this.members.get(name);
        if(member == null)
            throw new GameException("Member " + name + " not found");
        return member;
    }

    protected void checkNoMember(String name) {
        if(this.members.containsKey(name))
            throw new GameException("Member " + name + " already exists");
    }

    protected void checkFreeSpot() {
        if(this.members.size() == getGameCapacity())
            throw new GameException("Max members size exceeded");
    }

    public void joinMember(String name) {
        expectState(ExpectedStateException.LOBBY);
        checkNoMember(name);
        checkFreeSpot();
        Member newMember = newMember(name);
        this.members.put(name, newMember);
        onJoinMember(newMember);
    }

    public void leaveMember(Member member) {
        expectState(ExpectedStateException.ENABLED);
        this.members.remove(member.getName());
        if(member.hasTeam())
            member.getTeam().leaveMember(member);
        onLeaveMember(member);
    }

    protected void onLeaveMember(Member member) {
        if(state == State.START_READY) {
            if(members.size() < Game.PLAYER_COUNT_TO_START)
                setState(State.WAITING);
            onStartUnready();
        }
        if(state == State.ACTIVE) {
            List<Team> aliveTeams = getAliveTeams();
            if(aliveTeams.size() == 1) {
                end(aliveTeams.get(0));
            }
        }
    }

    protected void onJoinMember(Member member) {
        if(this.members.size() == PLAYER_COUNT_TO_START) {
            setState(State.START_READY);
            onStartReady();
        }
    }

    protected abstract void onStartReady();

    protected abstract void onStartUnready();

    public void start() {
        expectState(ExpectedStateException.START_READY);
        packMembersToTeams();
        for(Member member : members.values()) {
            member.alive = true;
            member.stat = new MemberStat();
        }
        setState(State.ACTIVE);
        onStart();
    }

    private void packMembersToTeams() {
        List<Team> notFullTeams = new ArrayList<>(teams.values());
        for(Member member : members.values()) {
            findTeamForMember(notFullTeams, member, member.preferredTeam);
        }
    }

    private void findTeamForMember(List<Team> notFullTeams, Member member, Team prefferedTeam) {
        if(prefferedTeam == null)
            prefferedTeam = notFullTeams.get(random.nextInt(notFullTeams.size()));
        if(!prefferedTeam.initialized)
            prefferedTeam.initialize();
        if(prefferedTeam.members.size() == teamCapacity) {
            notFullTeams.remove(prefferedTeam);
            findTeamForMember(notFullTeams, member, notFullTeams.get(random.nextInt(notFullTeams.size())));
            return;
        }
        prefferedTeam.joinMember(member);
    }

    protected abstract void onStart();

    public List<Team> getAliveTeams() {
        List<Team> aliveTeams = new ArrayList<>();
        for(Team team : this.teams.values()) {
            if(!team.isInitialized()) continue;
            if(team.isAlive())
                aliveTeams.add(team);
        }
        return aliveTeams;
    }

    public void end(Team winner) {
        expectState(ExpectedStateException.ACTIVE);
        setState(State.ENDING);
        this.winner = winner;
        onEnd();
    }

    protected abstract void onEnd();

    public Team getWinner() {
        return winner;
    }

    private void clearState() {
        for(Team team : this.teams.values()) {
            if(team.isInitialized())
                team.deinitialize();
        }
        this.members.clear();
        this.winner = null;
    }

    public void reset() {
        expectState(ExpectedStateException.ENABLED);
        onReset();
        clearState();
        setState(State.WAITING);
    }

    protected abstract void onReset();

    public void disable() {
        expectState(ExpectedStateException.ENABLED);
        onDisable();
        clearState();
        setState(State.DISABLED);
    }

    protected abstract void onDisable();


}