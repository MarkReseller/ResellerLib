package lv.reseller.netherwars.logic.exceptions;

import lv.reseller.netherwars.logic.State;

import java.util.Arrays;

/**
 * Project NetherWars
 *
 * @author Mark
 */
public class ExpectedStateException extends GameException {

    public static final ExpectedStateException DISABLED = new ExpectedStateException(new State[] {State.DISABLED});
    public static final ExpectedStateException ENABLED = new ExpectedStateException(new State[] {State.WAITING, State.START_READY, State.ACTIVE, State.ENDING});
    public static final ExpectedStateException LOBBY = new ExpectedStateException(new State[] {State.WAITING, State.START_READY});
    public static final ExpectedStateException START_READY = new ExpectedStateException(new State[] {State.START_READY});
    public static final ExpectedStateException ACTIVE = new ExpectedStateException(new State[] {State.ACTIVE});

    private final State[] expected;

    private ExpectedStateException(State[] expected) {
        super(Arrays.toString(expected) + " excepted");
        this.expected = expected;
    }

    public State[] getExpected() {
    return expected;
  }

    public void expect(State state) {
        for(State s : expected) {
            if(s == state) {
                return;
            }
        }
        throw this;
    }
}
