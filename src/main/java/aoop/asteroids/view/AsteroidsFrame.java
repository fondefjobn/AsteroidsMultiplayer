package aoop.asteroids.view;

import aoop.asteroids.control.*;
import aoop.asteroids.control.menu.MenuCommandHandler;
import aoop.asteroids.control.menu.MenuItem;
import aoop.asteroids.control.menu.MenuItemAction;
import aoop.asteroids.control.menu.MenuMouseController;
import aoop.asteroids.model.GameServer;

import javax.swing.*;
import java.awt.*;

/**
 * The main window that's used for displaying the game.
 */
public class AsteroidsFrame extends JFrame {

	/**
	 * The title which appears in the upper border of the window.
	 */
	private static final String WINDOW_TITLE = "Asteroids";

	/**
	 * The size that the window should be.
	 */
	public static final Dimension WINDOW_SIZE = new Dimension(800, 800);

	/** The game model. */
	private final GameServer gs;

	/**
	 * Constructs the game's main window.
	 * @param gs gameserver
	 */
	public AsteroidsFrame (GameServer gs) {
		this.gs = gs;
		initSwingUI();
	}

	/**
	 * A helper method to do the tedious task of initializing the Swing UI components.
	 */
	private void initSwingUI() {
		// Basic frame properties.
		setTitle(WINDOW_TITLE);
		setSize(WINDOW_SIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AsteroidsPanel panel = new AsteroidsPanel(gs);
		// Add a key listener that can control the game's spaceship.
		addKeyListener(new PlayerKeyListener(gs.getGame().getSpaceship()));
		// Add a menu bar with some simple actions.
		addMenu(panel.getCommandHandler());
		new MenuMouseController(panel);
		// Add the custom panel that the game will be drawn to.
		add(panel);
		setVisible(true);
	}

	/**
	 * This method creates menu bar
	 * @param handler command handler of the panel
	 */
	private void addMenu(MenuCommandHandler handler) {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		menuBar.add(menu);
		for (MenuItem menuItem: MenuItem.getAllItems()) {
			MenuItemAction action = new MenuItemAction(handler);
			action.putValue(Action.NAME, menuItem.getTitle());
			menu.add(action);
		}
		setJMenuBar(menuBar);
	}

}
