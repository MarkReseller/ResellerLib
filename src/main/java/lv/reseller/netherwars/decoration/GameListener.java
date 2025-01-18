package lv.reseller.netherwars.decoration;

import lv.reseller.netherwars.NetherWarsPlugin;
import lv.reseller.netherwars.logic.State;
import lv.reseller.netherwars.logic.Team;
import lv.reseller.netherwars.util.FireballUtil;
import lv.reseller.netherwars.util.Items;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class GameListener implements Listener {

    private final DecoratedGame game;

    GameListener(final DecoratedGame game) {
        this.game = game;
        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    private DecoratedMember identificateIntactMember(Entity entity) {
        if(game.getState() != State.ACTIVE) return null;
        DecoratedMember member = game.identificate(entity);
        if(member == null) return null;
        if(!member.isAlive()) return null;
        if(member.isRespawning()) return null;
        return member;
    }

    @EventHandler
    public void activeStateMemberDamage(EntityDamageEvent event) {
        if(event instanceof EntityDamageByEntityEvent) {
            intactMemberDamageByOther((EntityDamageByEntityEvent) event);
            return;
        }
        DecoratedMember member = identificateIntactMember(event.getEntity());
        if(member == null) return;
        Player player = (Player) event.getEntity();
        if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            member.death();
            return;
        }
        double damage = event.getDamage();
        if(player.getHealth() <= damage) {
            event.setCancelled(true);
            member.death();
        }
    }

    public void intactMemberDamageByOther(EntityDamageByEntityEvent event) {
        DecoratedMember member = identificateIntactMember(event.getEntity());
        if(member == null) return;
        DecoratedMember damager = identificateIntactMember(event.getDamager());
        if(damager == null) return;
        if(member.getTeam() != damager.getTeam()) {
            double damage = event.getDamage();
            if(member.getPlayer().getHealth() <= damage) {
                event.setCancelled(true);
                member.death(damager);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void memberBlockBreaking(BlockBreakEvent event) {
        DecoratedMember member = identificateIntactMember(event.getPlayer());
        if(member == null) return;
        Block block = event.getBlock();
        if(!block.hasMetadata("netherwars.breakable")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void memberMexusBreaking(BlockBreakEvent event) {
        DecoratedMember member = game.identificate(event.getPlayer());
        if(member == null) return;
        if(game.getState() != State.ACTIVE) return;
        DecoratedTeam memberTeam = member.getTeam();
        for(Team team : game.getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            if(team.getColor() == memberTeam.getColor()) continue;
            Block mexusBlock = decoratedTeam.getMexus().block;
            if(!mexusBlock.equals(event.getBlock())) continue;
            DecoratedMexus mexus = decoratedTeam.getMexus();
            if(mexus.isDestroyed()) continue;
            event.setCancelled(true);
            int damage = 1;
            mexus.damage(damage, member);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        DecoratedMember member = identificateIntactMember(event.getPlayer());
        if(member == null) return;
        if(event.getBlockPlaced().getType() == Material.TNT) {
            event.setCancelled(true);
            TNTPrimed tnt = (TNTPrimed) event.getBlockPlaced().getWorld().spawnEntity(event.getBlockPlaced().getLocation().add(0.5, 0.5, 0.5), EntityType.PRIMED_TNT);
            tnt.setFuseTicks(60);
            int amount = member.getPlayer().getItemInHand().getAmount();
            if(amount > 1)
                member.getPlayer().getItemInHand().setAmount(member.getPlayer().getItemInHand().getAmount() - 1);
            else
                member.getPlayer().setItemInHand(null);
            return;
        }
        event.getBlockPlaced().setMetadata("netherwars.breakable", new FixedMetadataValue(game.getPlugin(), true));
    }

    @EventHandler
    public void traderDamage(EntityDamageEvent event) {
        if(game.getState() != State.ACTIVE) return;
        for(Team team : game.getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            if(event.getEntity().equals(decoratedTeam.trader)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void traderInteract(PlayerInteractEntityEvent event) {
        DecoratedMember member = game.identificate(event.getPlayer());
        if(member == null) return;
        if(game.getState() != State.ACTIVE) return;
        for(Team team : game.getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            if(event.getRightClicked().equals(decoratedTeam.trader)) {
                event.setCancelled(true);
                NetherWarsPlugin.instance.shop.open(member.getPlayer());
            }
        }
    }

    @EventHandler
    public void fireballUse(PlayerInteractEvent event) {
//        DecoratedMember member = authorizeMember(event.getPlayer());
//        if(member == null) return;
//        if(getState() != State.ACTIVE) return;
        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.FIREBALL) return;
        event.setCancelled(true);
        int amount = item.getAmount();
        if(amount > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            item = null;
        }
        event.getPlayer().setItemInHand(item);
        FireballUtil.launchFireball(event.getPlayer());
    }

    @EventHandler
    public void fireballDamage(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof LargeFireball) {
            event.setDamage(1);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        DecoratedMember member = game.identificate(event.getPlayer());
        if(member == null) return;
        member.quit();
    }

    /**
     * Controlls explosions made by game member at ACTIVE state.
     * Mades blocks exploded, if block has metadata 'netherwars.breakable'
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if(game.getState() != State.ACTIVE) return;
        Entity cause = null;
        if(e.getEntity() instanceof LargeFireball) {
            LargeFireball largeFireball = (LargeFireball) e.getEntity();
            ProjectileSource shooter = largeFireball.getShooter();
            if(shooter instanceof Entity) {
                cause = (Entity) shooter;
            }
        }
        if(e.getEntity() instanceof TNTPrimed) {
            TNTPrimed tntPrimed = (TNTPrimed) e.getEntity();
            cause = tntPrimed.getSource();
        }
        if(cause == null) return;
        DecoratedMember member = game.identificate(cause);
        if(member == null) return;

        // Controll
        e.blockList().removeIf(block -> !block.hasMetadata("netherwars.breakable"));
    }

    @EventHandler
    public void onTeamChoserInteract(PlayerInteractEvent event) {
        DecoratedMember decoratedMember = game.identificate(event.getPlayer());
        if(decoratedMember == null) return;
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getPlayer().getItemInHand();
        if(item == null) return;
        if(item.getType() == Material.AIR) return;
        NBTTagCompound tagCompound = Items.getTag(item);
        if(tagCompound == null) return;
        if(!tagCompound.hasKey("team_chooser")) return;
        game.getTeamPreffer().open(decoratedMember.getPlayer());
    }



}
