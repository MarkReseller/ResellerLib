package lv.reseller.netherwars.decoration.ui;

import lv.reseller.netherwars.decoration.DecoratedGame;
import lv.reseller.netherwars.decoration.DecoratedMember;
import lv.reseller.netherwars.decoration.DecoratedTeam;
import lv.reseller.netherwars.logic.Team;
import lv.reseller.netherwars.logic.TeamColor;
import lv.reseller.netherwars.util.Items;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class TeamPreffer implements Listener {

    private final DecoratedGame game;
    private final Inventory teamList;

    public TeamPreffer(DecoratedGame game) {
        this.game = game;
        this.teamList = Bukkit.createInventory(null, 18, "Teams");
        Bukkit.getPluginManager().registerEvents(this, game.getPlugin());
    }

    public void build() {
        this.teamList.clear();
        int i = 0;
        for(Team team : this.game.getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            teamList.setItem(i++, buildItem(decoratedTeam));
        }
    }

    private ItemStack buildItem(DecoratedTeam team) {
        TeamColor teamColor = team.getColor();
        ItemStack teamItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, teamColor.getBlockData());
        NBTTagCompound tag = new NBTTagCompound();
        tag.set("teamColor", new NBTTagString(teamColor.toString()));
        teamItem = Items.setTag(teamItem, tag);
        ItemMeta meta = teamItem.getItemMeta();
        meta.setDisplayName(teamColor.getChatColor() + teamColor.toString());
        teamItem.setItemMeta(meta);
        return teamItem;
    }

    public void open(Player player) {
        player.openInventory(this.teamList);
    }

    @EventHandler
    public void onTeamListClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null) return;
        if(!event.getClickedInventory().equals(teamList)) return;
        event.setCancelled(true);
        DecoratedMember decoratedMember = this.game.identificate(event.getWhoClicked());
        if(decoratedMember == null) return;
        if(!game.getState().isLobby()) return;
        ItemStack item = event.getCurrentItem();
        if(item == null || item.getType() == Material.AIR) return;
        NBTTagCompound tagCompound = Items.getTag(item);
        if(tagCompound == null) return;
        if(!tagCompound.hasKey("teamColor")) return;
        TeamColor teamColor = TeamColor.valueOf(tagCompound.getString("teamColor"));
        DecoratedTeam decoratedTeam = (DecoratedTeam) game.getTeams().get(teamColor);
        if(decoratedTeam == null) return;
        decoratedMember.prefferTeam(decoratedTeam);
        decoratedMember.getPlayer().playSound(decoratedMember.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, 1);
    }

}
