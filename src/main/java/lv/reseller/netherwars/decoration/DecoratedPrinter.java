package lv.reseller.netherwars.decoration;

import lv.reseller.netherwars.decoration.generator.Generator;
import lv.reseller.netherwars.logic.Game;
import lv.reseller.netherwars.logic.Printer;
import lv.reseller.netherwars.logic.Team;
import lv.reseller.netherwars.util.Locations;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.PrintStream;
import java.util.Map;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class DecoratedPrinter extends Printer {

    protected DecoratedPrinter(Game game) {
        super(game);
    }

    @Override
    public DecoratedGame getGame() {
        return (DecoratedGame) super.getGame();
    }

    @Override
    public void printState(PrintStream out) {
        super.printState(out);
        out.print("Lobby: " + (getGame().getLobby() == null ? "null" : Locations.asString(getGame().getLobby())));
        out.print("Generators");
        for(Map.Entry<String, Generator> entry : getGame().getGeneratorManager().getGenerators().entrySet()) {
            out.print("  " + entry.getKey());
            out.print("    " + Locations.asString(entry.getValue().getLocation()));
            out.print("    " + entry.getValue().getDrop().getType());
            out.print("    " + entry.getValue().getPeriod());
        }
    }

    @Override
    protected void printTeam(PrintStream out, Team team) {
        super.printTeam(out, team);
        DecoratedTeam teamDecorated = (DecoratedTeam) team;
        Location spawn = teamDecorated.spawn;
        Location traderSpawn = teamDecorated.traderSpawn;
        Block block = teamDecorated.getMexus().block;
        out.print("    Spawn: " + (spawn == null ? "null" : Locations.asString(spawn)));
        out.print("    MexusBlock: " + (block == null ? "null" : Locations.asString(block.getLocation())));
        out.print("    TraderSpawn: " + (traderSpawn == null ? "null" : Locations.asString(traderSpawn)));
    }
}
