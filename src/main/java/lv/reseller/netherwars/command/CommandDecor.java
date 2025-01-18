package lv.reseller.netherwars.command;

import lv.reseller.netherwars.decoration.DecoratedGame;
import lv.reseller.netherwars.decoration.DecoratedTeam;
import lv.reseller.netherwars.decoration.generator.Generator;
import lv.reseller.netherwars.logic.exceptions.GameException;
import lv.reseller.netherwars.logic.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandDecor implements CommandExecutor {

    private final DecoratedGame decoratedGame;

    public CommandDecor(DecoratedGame decoratedGame) {
        this.decoratedGame = decoratedGame;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        try {
            switch(args[0]) {
                case "setlobby": {
                    decoratedGame.setLobby(player.getLocation());
                    player.sendMessage("Done");
                    break;
                }
                case "setrespawn": {
                    decoratedGame.setRespawn(player.getLocation());
                    player.sendMessage("Done");
                    break;
                }
                case "setteamspawn": {
                    DecoratedTeam team = decoratedGame.getTeam(TeamColor.valueOf(args[1]));
                    team.setSpawn(player.getLocation());
                    player.sendMessage("Done");
                    break;
                }
                case "setteammexus": {
                    DecoratedTeam team = decoratedGame.getTeam(TeamColor.valueOf(args[1]));
                    team.getMexus().setBlock(player.getLocation().getBlock());
                    player.sendMessage("Done");
                    break;
                }
                case "settraderspawn": {
                    DecoratedTeam team = decoratedGame.getTeam(TeamColor.valueOf(args[1]));
                    team.setTraderSpawn(player.getLocation());
                    player.sendMessage("Done");
                    break;
                }
                case "addgenerator": {
                    String id = args[1];
                    Location loc = player.getLocation();
                    ItemStack itemStack = player.getItemInHand();
                    int period = Integer.parseInt(args[2]);
                    Generator generator = decoratedGame.getGeneratorManager().newGenerator(id);
                    generator.setLocation(loc);
                    generator.setPeriod(period);
                    generator.setDrop(itemStack);
                    player.sendMessage("Done");
                    break;
                }
                case "removegenerator": {
                    decoratedGame.getGeneratorManager().removeGenerator(args[1]);
                    break;
                }
                default: {
                    break;
                }
            }
        } catch(GameException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }



}
