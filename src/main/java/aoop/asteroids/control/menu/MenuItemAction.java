package aoop.asteroids.control.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * This class executes commands when a menu action is called
 */
public class MenuItemAction extends AbstractAction {

    private final MenuCommandHandler menuCommandHandler;

    /**
     * Constructor
     * @param menuCommandHandler menu command handler
     */
    public MenuItemAction(MenuCommandHandler menuCommandHandler) {
        this.menuCommandHandler = menuCommandHandler;
    }

    /**
     * perform corresponding command when the action is called
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItemCommand command = menuCommandHandler.getCommand(e.getActionCommand());
        if (command != null) {
            command.execute();
            return;
        }
        command = menuCommandHandler.getCommand(MenuItem.findMenuItem(e.getActionCommand()));
        if (command != null) command.execute();
    }
}
