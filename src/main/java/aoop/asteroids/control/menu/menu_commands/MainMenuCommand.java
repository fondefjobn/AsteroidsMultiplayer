package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.model.GameServer;

/**
 * Command for main menu and new normal game
 */
public class MainMenuCommand extends AbstractMenuCommand {

    private final boolean asteroidsOnly;

    /**
     * Constructor
     * @param gs game server running the game
     * @param asteroidsOnly flag - true for main menu, false for normal game
     */
    public MainMenuCommand(GameServer gs, boolean asteroidsOnly) {
        super(gs);
        this.asteroidsOnly = asteroidsOnly;
    }

    /**
     * restart the game and sets asteroidsOnly flag
     */
    @Override
    public void execute() {
        super.execute();
        gs.getGame().setAsteroidsOnly(asteroidsOnly);
        gs.getGame().restart();
    }
}
