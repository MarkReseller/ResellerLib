package lv.reseller.netherwars.decoration.generator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Generator {

    private final GeneratorManager manager;
    private final String id;
    private Location location;
    private ItemStack drop;
    private int period;

    private boolean enabled;
    private BukkitTask task;

    Generator(GeneratorManager manager, String id) {
        this.manager = manager;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public void setDrop(ItemStack drop) {
        this.drop = drop;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void enable() {
        if(enabled) return;
        enabled = true;
        task = Bukkit.getScheduler().runTaskTimer(manager.getGame().getPlugin(), () -> location.getWorld().dropItem(location, drop), 0, period);
    }

    public void disable() {
        if(!enabled) return;
        enabled = false;
        task.cancel();
    }

}
