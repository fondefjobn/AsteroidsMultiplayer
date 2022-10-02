package aoop.asteroids.control.menu;

/**
 * This is an command interface for menu items
 */
public interface MenuItemCommand {

    /**
     * Executed when MenuCommandHandler calls it
     */
    void execute();
}
