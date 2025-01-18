package lv.reseller.netherwars.decoration;

import lv.reseller.netherwars.logic.*;
import lv.reseller.netherwars.util.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.github.paperspigot.Title;

public class DecoratedTeam extends Team {

    final Title.Builder titleBuilder;
    Location spawn;
    Location traderSpawn;
    Villager trader;

    DecoratedTeam(Game game, TeamColor teamColor) {
        super(game, teamColor);
        this.titleBuilder = Title.builder();
    }

    @Override
    public DecoratedGame getGame() {
        return (DecoratedGame) super.getGame();
    }

    @Override
    public DecoratedMexus getMexus() {
        return (DecoratedMexus) super.getMexus();
    }

    @Override
    protected Mexus newMexus() {
        return new DecoratedMexus(this);
    }

    @Override
    protected void onInitialize() {
        getGame().getGameScoreboard().putTeamState(getColor(), getMexus().getHealth(), getGame().getMaxMexusHealth(), getAliveSize());

        trader = traderSpawn.getWorld().spawn(traderSpawn, Villager.class);
        trader.setCustomName(Chat.colorize("&e&lShop"));
        trader.setCustomNameVisible(true);

        getMexus().buildHologram();
        getMexus().spawnMexus();
    }

    @Override
    protected void onDeinitialize() {
        trader.remove();

        getMexus().hologram.destroy();
        getMexus().block.setType(Material.AIR);
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getTraderSpawn() {
        return traderSpawn;
    }

    public void setTraderSpawn(Location location) {
        traderSpawn = location;
        if(trader != null)
            trader.teleport(traderSpawn);
    }

    public Villager getTrader() {
        return trader;
    }

    public void sendMessage(String message) {
        for(Member member : getMembers()) {
            DecoratedMember decoratedMember = (DecoratedMember) member;
            decoratedMember.getPlayer().sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        sendTitle(title, subtitle, 15, 40, 15);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for(Member member : getMembers()) {
            DecoratedMember decoratedMember = (DecoratedMember) member;
            Title titleObject = this.titleBuilder
                    .title(title)
                    .subtitle(subtitle)
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
                    .build();
            decoratedMember.getPlayer().sendTitle(titleObject);
        }
    }
}
