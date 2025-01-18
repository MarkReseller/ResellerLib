package lv.reseller.netherwars.logic;

public enum State {

    DISABLED(false, false, false),
    WAITING(true, true, false),
    START_READY(true, true, false),
    ACTIVE(true, false, true),
    ENDING(true, false, true);

    private final boolean enabled;
    private final boolean lobby;
    private final boolean started;

    State(boolean enabled, boolean lobby, boolean started) {
        this.enabled = enabled;
        this.lobby = lobby;
        this.started = started;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLobby() {
        return lobby;
    }

    public boolean isStarted() {
        return started;
    }
}
