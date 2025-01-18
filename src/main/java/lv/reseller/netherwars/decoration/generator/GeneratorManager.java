package lv.reseller.netherwars.decoration.generator;

import lv.reseller.netherwars.decoration.DecoratedGame;
import lv.reseller.netherwars.logic.exceptions.ExpectedStateException;

import java.util.HashMap;
import java.util.Map;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class GeneratorManager {

    private final DecoratedGame game;
    private final Map<String, Generator> generators;

    public GeneratorManager(DecoratedGame game) {
        this.game = game;
        this.generators = new HashMap<>();
    }

    public DecoratedGame getGame() {
        return game;
    }

    public Map<String, Generator> getGenerators() {
        return generators;
    }

    public void enableAll() {
        for(Generator generator : this.getGenerators().values()) {
            generator.enable();
        }
    }

    public void disableAll() {
        for(Generator generator : this.getGenerators().values()) {
            generator.disable();
        }
    }

    public Generator newGenerator(String id) {
        Generator generator = new Generator(this, id);
        Generator oldGenerator = this.generators.put(id, generator);
        if(oldGenerator != null)
            oldGenerator.disable();
        return generator;
    }

    public void removeGenerator(String id) {
        this.generators.remove(id).disable();
    }

    public void removeAll() {
        disableAll();
        generators.clear();
    }

}
