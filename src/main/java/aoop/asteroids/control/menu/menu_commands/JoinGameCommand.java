package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.model.GameServer;

/**
 * Command for joining multiplayer game
 */
public class JoinGameCommand extends AbstractMenuCommand {

    /**
     * Constructor
     * @param gs game server running the game
     */
    public JoinGameCommand(GameServer gs) {
        super(gs);
    }

    @Override
    public void execute() {
        super.execute();
        gs.startClient();
        gs.getGame().setClient(true);
        gs.getGame().restart();
    }
}
