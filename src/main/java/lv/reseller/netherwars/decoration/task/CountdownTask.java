package lv.reseller.netherwars.decoration.task;

import lv.reseller.netherwars.decoration.DecoratedGame;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public abstract class CountdownTask extends GameTask {

    private int time;
    private int timeLeft;

    public CountdownTask(DecoratedGame game, int t1, int t2, int time) {
        super(game, t1, t2);
        this.time = time;
    }

    @Override
    public void start() {
        super.start();
        this.timeLeft = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        if(this.timeLeft > time)
            this.timeLeft = time;
    }

    @Override
    protected void tick() {
        if(timeLeft != 0) {
            timeLeft--;
            countdownTick();
        } else {
            stop();
            timeOut();
        }
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = Math.max(time, 0);
    }

    protected abstract void countdownTick();

    protected abstract void timeOut();

}
