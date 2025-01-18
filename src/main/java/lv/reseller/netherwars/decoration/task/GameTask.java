package lv.reseller.netherwars.decoration.task;

import lv.reseller.netherwars.decoration.DecoratedGame;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Project MiniGameLobby
 *
 * @author Mark
 */
public abstract class GameTask {

    private final DecoratedGame game;
    private final int t1;
    private final int t2;

    private BukkitTask task;

    public GameTask(DecoratedGame game, int t1, int t2) {
        this.game = game;
        this.t1 = t1;
        this.t2 = t2;
    }

    public DecoratedGame getGame() {
        return game;
    }

    public int getT1() {
        return t1;
    }

    public int getT2() {
        return t2;
    }

    public BukkitTask getTask() {
        return task;
    }

    public boolean isStarted() {
        return task != null;
    }

    public void start() {
        if(isStarted()) return;
        task = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), this::tick, t1, t2);
    }

    public void stop() {
        if(!isStarted()) return;
        task.cancel();
        task = null;
    }

    public void restart() {
        if(!isStarted()) return;
        stop();
        start();
    }

    protected abstract void tick();

}
