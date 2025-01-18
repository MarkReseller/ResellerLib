package lv.reseller.netherwars.decoration;

import lv.reseller.lib.hologram.LineText;
import lv.reseller.netherwars.logic.Member;
import lv.reseller.netherwars.logic.Mexus;
import lv.reseller.netherwars.logic.Team;
import lv.reseller.netherwars.util.Chat;
import lv.reseller.lib.hologram.Hologram;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

public class DecoratedMexus extends Mexus {

    Hologram hologram;
    Block block;

    protected DecoratedMexus(Team team) {
        super(team);
    }

    @Override
    public DecoratedTeam getTeam() {
        return (DecoratedTeam) super.getTeam();
    }

    public DecoratedGame getGame() {
        return getTeam().getGame();
    }

    public Hologram getHologram() {
        return hologram;
    }

    public void buildHologram() {
        Location hologramLocation = block.getLocation().add(0.5, 0, 0.5);
        hologram = new Hologram(hologramLocation, 0.3);
        hologram.add(new LineText(Chat.colorize(getTeam().getColor().getChatColor() + "" + getTeam().getColor() + " mexus")));
        hologram.add(new LineText(Chat.colorize("Health: &a" + getTeam().getMexus().getHealth())));
    }

    public void spawnMexus() {
        block.setType(Material.ENDER_STONE);
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
        if(hologram != null) {
            Location hologramLocation = block.getLocation().add(0.5, 0, 0.5);
            hologram.setOrigin(hologramLocation);
        }
    }

    @Override
    protected void onDamage(int damage, Member damager) {
        this.block.getWorld().spigot().playEffect(block.getLocation(), Effect.LARGE_SMOKE, 0, 0, 3, 3, 3, 0.5f, 5, 10);
        getTeam().sendTitle( "", Chat.colorize("&cYour mexus is getting damage!"));
        ((LineText)this.hologram.get(1)).setText(Chat.colorize("Health: &a" + getHealth()));
        getTeam().getGame().getGameScoreboard().putTeamState(getTeam().getColor(), getHealth(), getTeam().getGame().getMaxMexusHealth(), getTeam().getAliveSize());
        if(damager != null) {
            getGame().getGameScoreboard().getScoreboard(damager.getName()).setMexusHits(damager.getStat().getMexusHitCount());
        }
    }

    @Override
    protected void onDestroy(int damage, Member damager) {
        this.block.setType(Material.AIR);
        this.hologram.destroy();
        this.block.getWorld().spigot().playEffect(block.getLocation(), Effect.LAVA_POP, 0, 0, 0.5f, 0.5f, 0.5f, 0.2f, 5, 5);
        this.block.getWorld().playSound(block.getLocation(), Sound.ENDERDRAGON_DEATH, 1, 10);
        getGame().getGameScoreboard().putTeamState(getTeam().getColor(), 0, getGame().getMaxMexusHealth(), getTeam().getAliveSize());
        if(damager != null) {
            getGame().getGameScoreboard().getScoreboard(damager.getName()).setMexusDestroyed(damager.getStat().getMexusesDestroyed());
            getGame().sendMessage(Chat.colorize("&fThe mexus of " + getTeam().getColor().getChatColor() + getTeam().getColor() + " &fwas destroyed by " + damager.getTeam().getColor().getChatColor() + damager.getName()));
        } else {
            getGame().sendMessage(Chat.colorize("&fThe mexus of " + getTeam().getColor().getChatColor() + getTeam().getColor() + " &fwas destroyed"));
        }
    }
}
