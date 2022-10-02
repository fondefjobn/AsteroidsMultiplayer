package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.control.menu.MenuItemCommand;
import aoop.asteroids.model.GameServer;

/**
 * This is a abstract class for menu commands
 */
public abstract class AbstractMenuCommand implements MenuItemCommand {

    protected final GameServer gs;

    /**
     * Constructor
     * @param gs game server
     */
    public AbstractMenuCommand(GameServer gs) {
        this.gs = gs;
    }

    /**
     * resets the game server to it's initial state
     */
    public void execute() {
        gs.reset();
    }
}
