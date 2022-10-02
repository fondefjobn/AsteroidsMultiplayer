package aoop.asteroids.control.menu.menu_commands;

import aoop.asteroids.control.menu.MenuItemCommand;

/**
 * Exits the application
 */
public class QuitCommand implements MenuItemCommand {

    /**
     * Command exiting application
     */
    @Override
    public void execute() {
        System.exit(0);
    }
}
