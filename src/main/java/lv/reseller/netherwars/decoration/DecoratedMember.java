package lv.reseller.netherwars.decoration;

import lv.reseller.netherwars.logic.Game;
import lv.reseller.netherwars.logic.Member;
import lv.reseller.netherwars.util.Chat;
import lv.reseller.netherwars.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DecoratedMember extends Member {

    final Player player;
    int respawnTime;

    protected DecoratedMember(Game game, String name) {
        super(game, name);
        this.player = Bukkit.getPlayer(name);
    }

    @Override
    public DecoratedGame getGame() {
        return (DecoratedGame) super.getGame();
    }

    @Override
    public DecoratedTeam getTeam() {
        return (DecoratedTeam) super.getTeam();
    }

    private void decoratedRespawn() {
        for(ItemStack item : player.getInventory()) {
            if(item == null) continue;
            player.getWorld().dropItem(player.getLocation(), item);
        }
        Players.clear(player);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(getGame().getRespawn());
        respawnTime = 6;
    }

    @Override
    protected void onDeath(Member killer) {
        super.onDeath(killer);
        decoratedRespawn();
        getGame().getGameScoreboard().getScoreboard(getName()).setDeaths(getStat().getDeaths());
        if(killer != null) {
            getGame().sendMessage(Chat.colorize(getTeam().getColor().getChatColor() + getName() + " &fhas died by " + killer.getTeam().getColor().getChatColor() + killer));
            getGame().getGameScoreboard().getScoreboard(killer.getName()).setKills(killer.getStat().getKills());
        } else {
            getGame().sendMessage(Chat.colorize(getTeam().getColor().getChatColor() + getName() + " &fhas died"));
        }

    }

    @Override
    protected void onDestroy(Member killer) {
        super.onDestroy(killer);
        this.player.setGameMode(GameMode.SPECTATOR);
        getGame().getGameScoreboard().putTeamState(getTeam().getColor(), getTeam().getMexus().getHealth(), DecoratedMexus.MAX_MEXUS_HEALTH, getTeam().getAliveSize());
        getGame().getGameScoreboard().getScoreboard(getName()).setDeaths(getStat().getDeaths());
        if(killer == null) {
            getGame().sendMessage(Chat.colorize(getTeam().getColor().getChatColor() + getName() + " &fwas &4destroyed"));
        } else {
            DecoratedMember decoratedKiller = (DecoratedMember) killer;
            DecoratedTeam killerTeam = decoratedKiller.getTeam();
            getGame().sendMessage(Chat.colorize(getTeam().getColor().getChatColor() + getName() + " &fwas &4destroyed &fby " + killerTeam.getColor().getChatColor() + killer.getName()));
            decoratedKiller.getPlayer().playSound(decoratedKiller.getPlayer().getLocation(), Sound.CHICKEN_HURT, 1, 1);
            getGame().getGameScoreboard().getScoreboard(killer.getName()).setFinalKills(decoratedKiller.getStat().getFinalKills());
        }
    }

    @Override
    protected void onRespawn() {
        player.teleport(getTeam().getSpawn());
        player.setHealth(player.getMaxHealth());
        player.setGameMode(GameMode.SURVIVAL);
    }

    public Player getPlayer() {
        return player;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = Math.max(respawnTime, 0);
    }
}
