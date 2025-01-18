package lv.reseller.netherwars.decoration;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lv.reseller.netherwars.decoration.generator.Generator;
import lv.reseller.netherwars.decoration.generator.GeneratorManager;
import lv.reseller.netherwars.decoration.scoreboard.GameScoreboard;
import lv.reseller.netherwars.decoration.scoreboard.GameScoreboardImplementation;
import lv.reseller.netherwars.decoration.scoreboard.LobbyScoreboard;
import lv.reseller.netherwars.decoration.scoreboard.PlayerScoreboard;
import lv.reseller.netherwars.decoration.task.EndingTask;
import lv.reseller.netherwars.decoration.task.LobbyTask;
import lv.reseller.netherwars.decoration.task.RespawnerTask;
import lv.reseller.netherwars.decoration.ui.TeamPreffer;
import lv.reseller.netherwars.logic.*;
import lv.reseller.netherwars.logic.exceptions.ExpectedStateException;
import lv.reseller.netherwars.logic.exceptions.GameException;
import lv.reseller.netherwars.util.Chat;
import lv.reseller.netherwars.util.Items;
import lv.reseller.netherwars.util.Players;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.github.paperspigot.Title;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.logging.Level;

public class DecoratedGame extends Game {

    public static final String WORLD_NAME = "arena";

    private final Plugin plugin;

    private final LobbyScoreboard lobbyScoreboard;
    private final GameScoreboard gameScoreboard;

    private Location lobby;
    private Location respawn;

    private final GeneratorManager generatorManager;

    private final LobbyTask lobbyTask;
    private final RespawnerTask respawnerTask;
    private final EndingTask endingTask;

    private final Title.Builder titleBuilder;

    private final ItemStack teamChooser;
    private final TeamPreffer teamPreffer;
    private final SlimePropertyMap slimePropertyMap;
    private final GameListener gameListener;

    public JedisPool jedisPool;
    public Jedis jedis;

    public DecoratedGame(Plugin plugin) {
        this.plugin = plugin;

        this.lobbyScoreboard = new LobbyScoreboard();
        this.gameScoreboard = new GameScoreboardImplementation(plugin);

        this.generatorManager = new GeneratorManager(this);

        this.lobbyTask = new LobbyTask(this, 10);
        this.respawnerTask = new RespawnerTask(this);
        this.endingTask = new EndingTask(this, 5);
        this.titleBuilder = Title.builder();

        ItemStack teamChooser = new ItemStack(Material.PAPER);
        NBTTagCompound tag = new NBTTagCompound();
        tag.set("team_chooser", new NBTTagByte((byte)0));
        teamChooser = Items.setTag(teamChooser, tag);
        ItemMeta meta = teamChooser.getItemMeta();
        meta.setDisplayName("Preffer team");
        teamChooser.setItemMeta(meta);
        this.teamChooser = teamChooser;
        this.teamPreffer = new TeamPreffer(this);

        SlimePropertyMap map = new SlimePropertyMap();
        map.setInt(SlimeProperties.SPAWN_X, 0);
        map.setInt(SlimeProperties.SPAWN_Y, 30);
        map.setInt(SlimeProperties.SPAWN_Z, 0);
        map.setString(SlimeProperties.DIFFICULTY, "peaceful");
        map.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
        map.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        map.setBoolean(SlimeProperties.PVP, true);
        map.setString(SlimeProperties.ENVIRONMENT, "normal");
        map.setString(SlimeProperties.WORLD_TYPE, "default");
        this.slimePropertyMap = map;

        this.gameListener = new GameListener(this);
        this.jedisPool = new JedisPool();
        this.jedis = jedisPool.getResource();
    }

    public GameListener getGameListener() {
        return gameListener;
    }

    @Override
    protected Printer newPrinter() {
        return new DecoratedPrinter(this);
    }

    public GameScoreboard getGameScoreboard() {
        return gameScoreboard;
    }

    public LobbyScoreboard getLobbyScoreboard() {
        return lobbyScoreboard;
    }

    public TeamPreffer getTeamPreffer() {
        return teamPreffer;
    }

    public DecoratedMember identificate(Entity entity) {
        if(!(entity instanceof Player)) return null;
        Player player = (Player) entity;
        return (DecoratedMember) getMembers().get(player.getName());
    }

    @Override
    protected Team newTeam(TeamColor teamColor) {
        return new DecoratedTeam(this, teamColor);
    }

    @Override
    protected Member newMember(String name) {
        return new DecoratedMember(this, name);
    }

    public DecoratedTeam getTeam(TeamColor teamColor) {
        return checkTeam(teamColor);
    }

    @Override
    public DecoratedMember getMember(String name) {
        return (DecoratedMember) super.getMember(name);
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    private void checkOnline(String name) {
        if(Bukkit.getPlayer(name) == null)
            throw new GameException("Player " + name + " must be online");
    }

    @Override
    public DecoratedTeam addTeam(TeamColor teamColor) {
        return (DecoratedTeam) super.addTeam(teamColor);
    }

    @Override
    protected void onStateChanged(State oldState, State newState) {
        updateJedisStatus();
    }

    private void updateJedisStatus() {
        String state = "netherwars" + ';' +
                getMembers().size() + ';' +
                getGameCapacity() + ';' +
                getState().toString();
        jedis.publish("gamestate/netherwars", state);
    }

    @Override
    protected DecoratedTeam checkTeam(TeamColor teamColor) {
        return (DecoratedTeam) super.checkTeam(teamColor);
    }

    public void sendMessage(String message) {
        for(Member member : getMembers().values()) {
            DecoratedMember decoratedMember = (DecoratedMember) member;
            decoratedMember.getPlayer().sendMessage(message);
        }
    }

    public void sendTitle(String title, String subtitle) {
        sendTitle(title, subtitle, 15, 40, 15);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Title titleObject = titleBuilder.title(title).subtitle(subtitle).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut).build();
        for(Member member : getMembers().values()) {
            DecoratedMember decoratedMember = (DecoratedMember) member;
            decoratedMember.getPlayer().sendTitle(titleObject);
        }
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        expectState(ExpectedStateException.DISABLED);
        this.lobby = lobby;
    }

    public Location getRespawn() {
        return respawn;
    }

    public void setRespawn(Location respawn) {
        expectState(ExpectedStateException.DISABLED);
        this.respawn = respawn;
    }

    @Override
    public void enable() {
        if(lobby == null)
            throw new GameException("Lobby location is null");
        if(respawn == null)
            throw new GameException("Respawn location is null");
        for(Team team : getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            if(decoratedTeam.getSpawn() == null)
                throw new GameException("Spawn for team " + team.getColor() + " is null");
            if(decoratedTeam.getMexus().block == null)
                throw new GameException("Mexus block for team " + team.getColor() + " is null");
            if(decoratedTeam.traderSpawn == null)
                throw new GameException("Trader spawn for team " + team.getColor()  + " is null");
        }
        super.enable();
    }

    @Override
    protected void onEnable() {
        this.teamPreffer.build();
    }

    @Override
    public void joinMember(String name) {
        checkOnline(name);
        super.joinMember(name);
    }

    @Override
    protected void onJoinMember(Member member) {
        super.onJoinMember(member);
        DecoratedMember decoratedMember = (DecoratedMember) member;
        Player player = decoratedMember.getPlayer();
        player.teleport(lobby);
        player.setGameMode(GameMode.SURVIVAL);
        Players.clear(player);
        player.getInventory().addItem(teamChooser);
        lobbyScoreboard.show(player);
        sendMessage(Chat.colorize("&e" + player.getName() + " &fhas joined"));
        lobbyScoreboard.updatePlayerCount(getMembers().size(), getGameCapacity());
        updateJedisStatus();
    }

    @Override
    protected void onStartReady() {
        sendMessage(Chat.colorize("&eThe game is starting"));
        lobbyTask.start();
    }

    @Override
    protected void onStartUnready() {
        lobbyTask.stop();
        sendMessage(Chat.colorize("&cThe game is not ready to start"));
    }

    @Override
    protected void onLeaveMember(Member member) {
        super.onLeaveMember(member);
        DecoratedMember decoratedMember = (DecoratedMember) member;
        Player player = decoratedMember.getPlayer();
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        sendMessage(Chat.colorize("&e" + member.getName() + " &fhas left"));
        updateJedisStatus();
    }

    @Override
    protected void onStart() {
        for(Member member : getMembers().values()) {
            DecoratedMember decoratedMember = (DecoratedMember) member;
            Player player = decoratedMember.getPlayer();
            Players.clear(player);
            player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD), new ItemStack(Material.IRON_PICKAXE));
            player.teleport(decoratedMember.getTeam().spawn);

            GameScoreboard gameScoreboard = getGameScoreboard();;
            gameScoreboard.addScoreboard(decoratedMember.getName());
            PlayerScoreboard playerScoreboard = gameScoreboard.getScoreboard(decoratedMember.getName());
            playerScoreboard.setTeamColor(decoratedMember.getTeam().getColor());
            playerScoreboard.show(player);
        }
        generatorManager.enableAll();
        respawnerTask.start();
    }

    @Override
    protected void onEnd() {
        sendTitle(Chat.colorize("&eThe game is ending"), "The winner is " + getWinner().getColor().getChatColor() + getWinner().getColor());
        this.endingTask.start();
    }

    @Override
    protected void onReset() {
        generatorManager.disableAll();
        lobbyTask.stop();
        respawnerTask.stop();
        resetArena();
        getGameScoreboard().clearTeamStates();
    }

    @Override
    protected void onDisable() {
        generatorManager.disableAll();
        lobbyTask.stop();
        respawnerTask.stop();
        resetArena();
        getGameScoreboard().clearTeamStates();
    }

    private void resetArena() {
        SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        SlimeLoader slimeLoader = slimePlugin.getLoader("file");
        World world = Bukkit.getWorld(WORLD_NAME);

        //Kicking all players from world and unloading world
        for(Player player1 : world.getPlayers()) {
            player1.kickPlayer("reseting");
        }
        Bukkit.unloadWorld(world, false);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            SlimeWorld slimeWorld;
            try {
                slimeWorld = slimePlugin.loadWorld(slimeLoader, WORLD_NAME, true, this.slimePropertyMap);
            } catch (UnknownWorldException | CorruptedWorldException | IOException | NewerFormatException |
                     WorldInUseException e) {
                plugin.getLogger().log(Level.SEVERE, "Error loading world", e);
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                slimePlugin.generateWorld(slimeWorld);
                fixLocations();
            });
        });
    }

    private void fixLocations() {
        World world = Bukkit.getWorld(WORLD_NAME);
        for(Team team : getTeams().values()) {
            DecoratedTeam decoratedTeam = (DecoratedTeam) team;
            decoratedTeam.traderSpawn.setWorld(world);
            Location blockLocation = decoratedTeam.getMexus().getBlock().getLocation();
            blockLocation.setWorld(world);
            decoratedTeam.getMexus().setBlock(blockLocation.getBlock());
            decoratedTeam.getSpawn().setWorld(world);
        }
        this.lobby.setWorld(world);
        this.respawn.setWorld(world);
        for(Generator generator : getGeneratorManager().getGenerators().values()) {
            generator.getLocation().setWorld(world);
        }
    }


}
