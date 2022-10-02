package aoop.asteroids.control.menu;

import aoop.asteroids.model.GameServer;
import aoop.asteroids.control.menu.menu_commands.*;

import java.util.HashMap;

/**
 * This is a class of command handler for menu items
 */
public class MenuCommandHandler {

    private final HashMap<MenuItem, MenuItemCommand> menuCommandsMap;
    private final HashMap<String, MenuItem> stringMenuItemMap;

    /**
     * Constructor
     * @param gameServer game with online features
     */
    public MenuCommandHandler(GameServer gameServer) {
        menuCommandsMap = new HashMap<>();
        stringMenuItemMap = new HashMap<>();
        for (MenuItem item: MenuItem.getAllItems()) {
            stringMenuItemMap.putIfAbsent(item.toString(), item);
        }
        menuCommandsMap.putIfAbsent(MenuItem.NEW_GAME, new MainMenuCommand(gameServer, false));
        menuCommandsMap.putIfAbsent(MenuItem.JOIN, new JoinGameCommand(gameServer));
        menuCommandsMap.putIfAbsent(MenuItem.HOST, new HostGameCommand(gameServer));
        menuCommandsMap.putIfAbsent(MenuItem.SPECTATE, new SpectateGameCommand(gameServer));
        menuCommandsMap.putIfAbsent(MenuItem.HIGH_SCORES, new HighScoresCommand(gameServer));
        menuCommandsMap.putIfAbsent(MenuItem.MAIN_MENU, new MainMenuCommand(gameServer, true));
        menuCommandsMap.putIfAbsent(MenuItem.QUIT, new QuitCommand());
    }

    /**
     * @param command to be mapped
     * @return MenuItemCommand if it exists
     */
    public MenuItemCommand getCommand(MenuItem command) {
        return menuCommandsMap.get(command);
    }

    /**
     * @param command to be mapped
     * @return MenuItemCommand if it exists
     */
    public MenuItemCommand getCommand(String command) {
        MenuItem item = stringMenuItemMap.get(command);
        if (item != null) return menuCommandsMap.get(item);
        return null;
    }
}
