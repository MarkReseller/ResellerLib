package lv.reseller.netherwars.command;

import lv.reseller.netherwars.decoration.tower.BattleTower;
import lv.reseller.netherwars.NetherWarsPlugin;
import lv.reseller.netherwars.decoration.shop.Product;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CommandTest implements CommandExecutor {

    public NetherWarsPlugin plugin;

    BattleTower tower;

    public CommandTest(NetherWarsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        switch(args[0]) {
            case "buy": {
                Product product = plugin.shop.getProducts().get(args[1]);
                boolean result = product.buy(player);
                if(result) {
                    player.sendMessage("The product was bought!");
                } else {
                    player.sendMessage("Not enough items");
                }
                break;
            }
            case "products": {
                for(Map.Entry<String, Product> entry : plugin.shop.getProducts().entrySet()) {
                    player.sendMessage(entry.getKey());
                    Product product = entry.getValue();
                    player.sendMessage(" Items:");
                    for(ItemStack item : product.getItems()) {
                        player.sendMessage("  " + item.getType() + " " + item.getAmount());
                    }
                    player.sendMessage(" Price:");
                    for(ItemStack item : product.getPrice()) {
                        player.sendMessage("  " + item.getType() + " " + item.getAmount());
                    }
                }
                break;
            }
            case "reloadshop": {
                plugin.shopConfig = YamlConfiguration.loadConfiguration(plugin.shopFile);
                plugin.shop.loadShop(plugin.shopConfig);
                player.sendMessage("Reloaded!");
                break;
            }
            case "gui": {
                plugin.shop.open(player);
                break;
            }
            case "tower": {
                if(tower != null) {
                    tower.setEnabled(false);
                }
                tower = new BattleTower(plugin, player.getLocation(), Integer.parseInt(args[1]), 10, (livingEntity -> true));
                break;
            }
            case "enabled": {
                tower.setEnabled(Boolean.parseBoolean(args[1]));
                break;
            }
            case "spawned": {
                tower.setSpawned(Boolean.parseBoolean(args[1]));
            }
            case "resetarena": {
                sender.sendMessage("test");
                break;
            }
            case "worlds": {
                for(World world : Bukkit.getWorlds()) {
                    player.sendMessage(world.getName() + " " + (player.getWorld().equals(world) ? "ME" : ""));
                }
                break;
            }
        }
        return true;
    }



}
