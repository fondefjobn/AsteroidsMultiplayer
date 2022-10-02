package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.model.GameServer;

/**
 * Command for spectating the game
 */
public class SpectateGameCommand extends AbstractMenuCommand {

    /**
     * Constructor
     * @param gs game server running the game
     */
    public SpectateGameCommand(GameServer gs) {
        super(gs);
    }

    /**
     * restart the game and sets spectate flag
     */
    @Override
    public void execute() {
        super.execute();
        gs.getGame().setSpectate(true);
        gs.startClient();
        gs.getGame().restart();
    }
}
