package aoop.asteroids.control.menu;

import aoop.asteroids.util.Pair;
import aoop.asteroids.view.AsteroidsPanel;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * This is a class for a mouse contorller tha controlls menu
 */
public class MenuMouseController extends MouseInputAdapter {

    private final AsteroidsPanel asteroidsPanel;

    /**
     * Constructor
     * @param asteroidsPanel panel where this mouse controller operates
     */
    public MenuMouseController(AsteroidsPanel asteroidsPanel) {
        this.asteroidsPanel = asteroidsPanel;
        asteroidsPanel.addMouseListener(this);
    }

    /**
     * if clicked on a menu item, perform corresponding action
     * @param e mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        for (Pair<Shape, MenuItem> pair: asteroidsPanel.getMenuItems()) {
            if (asteroidsPanel.getGame().isAsteroidsOnly() && pair.getFirst().contains(e.getPoint())) {
                new MenuItemAction(asteroidsPanel.getCommandHandler()).actionPerformed(
                        new ActionEvent(asteroidsPanel, ActionEvent.ACTION_PERFORMED, pair.getSecond().toString()));
            }
        }
    }
}
