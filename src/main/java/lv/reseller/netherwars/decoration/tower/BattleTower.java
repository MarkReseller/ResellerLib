package lv.reseller.netherwars.decoration.tower;

import lv.reseller.netherwars.util.Chat;
import lv.reseller.netherwars.util.VectorMath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class BattleTower implements Listener {

    public static final int HEALTH_BAR_SIZE = 20;

    public final Plugin plugin;

    public Location location;
    public int shootRate;
    public double range;
    public double health;
    public TargetSearch targetSearch;

    public boolean spawned;
    public ArmorStand armorStand;
    public boolean enabled;
    public BukkitTask shooterTask;
    public final Set<Projectile> projectiles;

    public BattleTower(Plugin plugin, Location location, int shootRate, double range, TargetSearch targetSearch) {
        this.plugin = plugin;
        this.location = location;
        this.shootRate = shootRate;
        this.range = range;
        this.health = 40;
        this.targetSearch = targetSearch;
        this.projectiles = new HashSet<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getShootRate() {
        return shootRate;
    }

    public void setShootRate(int shootRate) {
        this.shootRate = shootRate;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public TargetSearch getTargetSearch() {
        return targetSearch;
    }

    public void setTargetSearch(TargetSearch targetSearch) {
        this.targetSearch = targetSearch;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned(boolean spawned) {
        if(this.spawned == spawned) return;
        this.spawned = spawned;
        if(spawned) {
            this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setHelmet(new ItemStack(Material.DISPENSER));
            armorStand.setVisible(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(Chat.colorize(buildHealthBar()));
        } else {
            this.armorStand.remove();
            this.armorStand = null;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if(this.enabled == enabled) return;
        this.enabled = enabled;
        if(enabled) {
            shooterTask = Bukkit.getScheduler().runTaskTimer(plugin, this::searchTarget, 0, shootRate);
        } else {
            shooterTask.cancel();
        }
    }

    private String buildHealthBar() {
        StringBuilder healthBar = new StringBuilder();
        healthBar.append("&8&l[");
        int a = (int) (health / 40 * HEALTH_BAR_SIZE);
        for(int i = 0; i < HEALTH_BAR_SIZE; i++) {
            if(i < a) {
                healthBar.append("&a&l|");
            } else {
                healthBar.append("&7&l|");
            }
        }
        healthBar.append("&8&l]");
        return healthBar.toString();
    }

    private void searchTarget() {
        for(Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            if(entity.equals(armorStand)) continue;
            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if(!targetSearch.isTarget(livingEntity)) continue;
                Vector shootDirection = VectorMath.genVec(location, livingEntity.getLocation());
                Location standLocation = armorStand.getLocation();
                standLocation.setDirection(shootDirection);
                armorStand.teleport(standLocation);
                projectiles.add(armorStand.launchProjectile(Arrow.class, shootDirection));
                return;
            }
        }
    }

    protected void onTowerDamaged(EntityDamageEvent event) {}

    protected void onTowerDestroyed(EntityDamageEvent event) {}

    protected void onTowerInteracted(PlayerInteractAtEntityEvent event) {}

    protected void onProjectileHit(ProjectileHitEvent event) {}

    @EventHandler
    public void listenTowerDamage(EntityDamageEvent event) {
        if(!spawned) return;
        if(!event.getEntity().equals(armorStand)) return;
        health -= event.getDamage();
        if(health <= 0) {
            setEnabled(false);
            setSpawned(false);
            armorStand.getWorld().playSound(armorStand.getLocation(), Sound.EXPLODE, 1, 1);
            onTowerDestroyed(event);
        } else {
            onTowerDamaged(event);
        }
    }

    @EventHandler
    public void listenTowerInteract(PlayerInteractAtEntityEvent event) {
        if(!spawned) return;
        if(!event.getRightClicked().equals(armorStand)) return;
        onTowerInteracted(event);
    }

    @EventHandler
    public void listenProjectileHit(ProjectileHitEvent event) {
        if(!projectiles.contains(event.getEntity())) return;
        projectiles.remove(event.getEntity());
        Arrow r;
        onProjectileHit(event);
    }


}
