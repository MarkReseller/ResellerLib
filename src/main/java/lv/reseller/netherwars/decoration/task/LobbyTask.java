package lv.reseller.netherwars.decoration.task;

import lv.reseller.netherwars.decoration.DecoratedGame;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class LobbyTask extends CountdownTask {

    public LobbyTask(DecoratedGame game, int time) {
        super(game, 20, 20, time);
    }

    @Override
    public void start() {
        super.start();
        getGame().getLobbyScoreboard().updateLobbyState(getTimeLeft());
    }

    @Override
    public void stop() {
        super.stop();
        getGame().getLobbyScoreboard().updateLobbyState(-1);
    }

    @Override
    protected void countdownTick() {
        getGame().getLobbyScoreboard().updateLobbyState(getTimeLeft());
    }

    @Override
    protected void timeOut() {
        getGame().start();
    }
}
