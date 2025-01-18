package lv.reseller.netherwars;

import lv.reseller.netherwars.decoration.DecoratedGame;
import lv.reseller.netherwars.decoration.DecoratedMember;
import lv.reseller.netherwars.decoration.DecoratedTeam;
import lv.reseller.netherwars.decoration.generator.Generator;
import lv.reseller.netherwars.decoration.shop.Shop;
import lv.reseller.netherwars.logic.State;
import lv.reseller.netherwars.logic.Team;
import lv.reseller.netherwars.logic.TeamColor;
import lv.reseller.netherwars.command.CommandDecor;
import lv.reseller.netherwars.command.CommandLogic;
import lv.reseller.netherwars.command.CommandTest;
import lv.reseller.netherwars.util.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class NetherWarsPlugin extends JavaPlugin implements Listener {

    public static NetherWarsPlugin instance;
    public File langFile;
    public JSONObject lang;
    public File shopFile;
    public YamlConfiguration shopConfig;

    public Shop shop;
    public DecoratedGame decoratedGame;
    public boolean invitingMode;
    public Set<String> invitedPlayers;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        langFile = new File(getDataFolder(), "lang.json");
        saveResource("lang.json", false);
        JSONParser parser = new JSONParser();
        FileReader langReader;
        try {
            langReader = new FileReader(langFile);
            lang = (JSONObject) parser.parse(langReader);
            langReader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        shopFile = new File(getDataFolder(), "shop.yml");
        saveResource("shop.yml", false);
        InputStreamReader shopReader = null;
        try {
            shopReader = new InputStreamReader(new FileInputStream(shopFile), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopReader);

        shop = new Shop(this);
        shop.loadShop(shopConfig);
        shop.buildViews();

        saveDefaultConfig();
        reloadConfig();

        decoratedGame = new DecoratedGame(this);

        loadGameData();

        invitedPlayers = new HashSet<>();
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("logic").setExecutor(new CommandLogic(decoratedGame));
        getCommand("decor").setExecutor(new CommandDecor(decoratedGame));
        getCommand("test").setExecutor(new CommandTest(this));
    }

    @Override
    public void onDisable() {
        if (decoratedGame.getState() != State.DISABLED)
            decoratedGame.disable();

        instance = null;
    }

    public void loadGameData() {
        World gameWorld = Bukkit.getWorld(DecoratedGame.WORLD_NAME);
        decoratedGame.setLobby(Locations.asPosition(gameWorld, getConfig().getString("lobby")));
        decoratedGame.setRespawn(Locations.asPosition(gameWorld, getConfig().getString("respawn")));
        decoratedGame.setTeamCapacity(getConfig().getInt("team-capacity"));
        decoratedGame.setMaxMexusHealth(getConfig().getInt("mexus-health"));
        //decoratedGame.setMembersToStarting(getConfig().getInt("members-to-starting"));
        decoratedGame.getTeams().clear();
        for(String key : getConfig().getConfigurationSection("teams").getKeys(false)) {
            TeamColor teamColor = TeamColor.valueOf(key);
            DecoratedTeam team = decoratedGame.addTeam(teamColor);
            team.setSpawn(Locations.asPosition(gameWorld, getConfig().getString("teams." + key + ".spawn")));
            team.getMexus().setBlock(Locations.asPosition(gameWorld, getConfig().getString("teams." + key + ".mexus")).getBlock());
            team.setTraderSpawn(Locations.asPosition(gameWorld, getConfig().getString("teams." + key + ".trader")));
        }
        decoratedGame.getGeneratorManager().removeAll();
        if(getConfig().isConfigurationSection("generators")) {
            for(String key : getConfig().getConfigurationSection("generators").getKeys(false)) {
                ConfigurationSection genSect = getConfig().getConfigurationSection("generators." + key);
                Location location = Locations.asPosition(gameWorld, genSect.getString("location"));
                ItemStack drop = genSect.getItemStack("drop");
                int period = genSect.getInt("period");
                Generator generator = decoratedGame.getGeneratorManager().newGenerator(key);
                generator.setLocation(location);
                generator.setPeriod(period);
                generator.setDrop(drop);
            }
        }
    }

    public void saveGameData() {
        getConfig().set("lobby", Locations.asStringPosition(decoratedGame.getLobby()));
        getConfig().set("respawn", Locations.asStringPosition(decoratedGame.getRespawn()));
        getConfig().set("team-capacity", decoratedGame.getTeamCapacity());
        getConfig().set("mexus-health", decoratedGame.getMaxMexusHealth());
        //getConfig().set("members-to-starting", decoratedGame.getMembersToStarting());
        for(Team team : decoratedGame.getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            getConfig().set("teams." + team.getColor() + ".spawn", Locations.asStringPosition(decoratedTeam.getSpawn()));
            getConfig().set("teams." + team.getColor() + ".mexus", Locations.asStringPosition(decoratedTeam.getMexus().getBlock().getLocation()));
            getConfig().set("teams." + team.getColor() + ".trader", Locations.asStringPosition(decoratedTeam.getTraderSpawn()));
        }
        for(Generator generator : decoratedGame.getGeneratorManager().getGenerators().values()) {
            getConfig().set("generators." + generator.getId() + ".location", Locations.asStringPosition(generator.getLocation()));
            getConfig().set("generators." + generator.getId() + ".drop", generator.getDrop());
            getConfig().set("generators." + generator.getId() + ".period", generator.getPeriod());
        }
    }

    public boolean invite(String playerName) {
        if(!invitingMode) return false;
        if(decoratedGame.getState().isLobby() && decoratedGame.getMembers().size() < decoratedGame.getGameCapacity()) {
            invitedPlayers.add(playerName);
            return true;
        } else {
            return false;
        }
    }

    @EventHandler
    public void onInvitedPlayerJoin(PlayerJoinEvent event) {
        if(!invitingMode) return;
        Player player = event.getPlayer();
        if(invitedPlayers.contains(player.getName())) {
            decoratedGame.joinMember(player.getName());
            invitedPlayers.remove(player.getName());
        } else {
            player.kickPlayer("You are not invited to the minigame");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName()) {
            case "loadgamedata": {
                reloadConfig();
                loadGameData();
                sender.sendMessage("Gamedata has loaded");
                break;
            }
            case "savegamedata": {
                saveGameData();
                saveConfig();
                sender.sendMessage("Gamedata has saved");
                break;
            }
            case "join": {
                Player player = (Player) sender;
                decoratedGame.joinMember(player.getName());
                break;
            }
            case "quit": {
                Player player = (Player) sender;
                DecoratedMember member = decoratedGame.getMember(player.getName());
                member.quit();
                break;
            }
            case "invitemode": {
                if(args.length == 0) {
                    sender.sendMessage("Invite mode: " + invitingMode);
                } else {
                    invitingMode = Boolean.parseBoolean(args[0]);
                }
                break;
            }
            case "invite": {
                invite(args[0]);
                break;
            }
            case "invitelist": {
                sender.sendMessage("Invite list:");
                invitedPlayers.forEach(sender::sendMessage);
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

}
