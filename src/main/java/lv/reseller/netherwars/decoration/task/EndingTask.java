package lv.reseller.netherwars.decoration.task;

import lv.reseller.netherwars.decoration.DecoratedGame;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class EndingTask extends CountdownTask {

    public EndingTask(DecoratedGame game, int time) {
        super(game, 20, 20, time);
    }

    @Override
    protected void countdownTick() {

    }

    @Override
    protected void timeOut() {
        getGame().reset();
    }
}
