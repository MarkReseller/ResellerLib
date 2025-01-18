package lv.reseller.netherwars.command;

import lv.reseller.netherwars.logic.Member;
import lv.reseller.netherwars.logic.Team;
import lv.reseller.netherwars.logic.exceptions.GameException;
import lv.reseller.netherwars.logic.TeamColor;
import lv.reseller.netherwars.logic.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandLogic implements CommandExecutor {

    private final Game game;

    public CommandLogic(Game game) {
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            switch (args[0]) {
                case "state": {
                    game.getPrinter().printState(System.out);
                    break;
                }
                case "addteam": {
                    game.addTeam(TeamColor.valueOf(args[1]));
                    break;
                }
                case "removeteam": {
                    game.removeTeam(TeamColor.valueOf(args[1]));
                    break;
                }
                case "enable": {
                    game.enable();
                    break;
                }
                case "disable": {
                    game.disable();
                    break;
                }
                case "start": {
                    game.start();
                    break;
                }
                case "addmember": {
                    game.joinMember(args[1]);
                    break;
                }
                case "removemember": {
                    Member member  = game.getMember(args[1]);
                    member.quit();
                    break;
                }
                case "teamcapacity": {
                    game.setTeamCapacity(Integer.parseInt(args[1]));
                    break;
                }
                case "death": {
                    Member member  = game.getMember(args[1]);
                    if (args.length == 2)
                        member.death();
                    else {
                        Member killer = game.getMember(args[2]);
                        member.death(killer);
                    }
                    break;
                }
                case "hit": {
                    Team team = game.getTeams().get(TeamColor.valueOf(args[1]));
                    int damage = Integer.parseInt(args[2]);
                    Member member  = game.getMember(args[3]);
                    team.getMexus().damage(damage, member);
                    break;
                }
                case "end": {
                    Team team = game.getTeams().get(TeamColor.valueOf(args[1]));
                    game.end(team);
                    break;
                }
                case "respawn": {
                    Member member  = game.getMember(args[1]);
                    member.respawn();
                    break;
                }
            }
        } catch(GameException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }
}
