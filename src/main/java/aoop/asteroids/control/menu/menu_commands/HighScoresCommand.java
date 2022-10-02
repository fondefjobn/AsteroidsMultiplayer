package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.model.GameServer;

/**
 * High score command, opens high scores table
 */
public class HighScoresCommand extends AbstractMenuCommand {

    /**
     * Constructor
     * @param gs game server running the game
     */
    public HighScoresCommand(GameServer gs) {
        super(gs);
    }

    /**
     * restarts the game and sets high score flag
     */
    @Override
    public void execute() {
        boolean mainMenu = gs.getGame().isAsteroidsOnly();
        super.execute();
        gs.getGame().setAsteroidsOnly(true);
        gs.setHighScores(true);
        //no game restart if it's in main menu
        if (!mainMenu) {
            gs.getGame().restart();
        }
    }
}
