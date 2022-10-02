package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.model.GameServer;

/**
 * Command for hosting the game
 */
public class HostGameCommand extends AbstractMenuCommand {

    /**
     * Constructor
     * @param gs game server running the game
     */
    public HostGameCommand(GameServer gs) {
        super(gs);
    }

    /**
     * Restarts the game and sets host flag
     */
    @Override
    public void execute() {
        super.execute();
        gs.startServer();
        gs.getGame().setHost(true);
        gs.getGame().restart();
    }
}
