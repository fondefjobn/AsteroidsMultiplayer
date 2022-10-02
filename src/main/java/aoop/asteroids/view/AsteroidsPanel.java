package aoop.asteroids.view;

import aoop.asteroids.control.menu.MenuCommandHandler;
import aoop.asteroids.control.menu.MenuItem;
import aoop.asteroids.game_observer.GameUpdateListener;
import aoop.asteroids.model.GameServer;
import aoop.asteroids.model.game.Asteroid;
import aoop.asteroids.model.game.Bullet;
import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.game.Spaceship;
import aoop.asteroids.view.view_models.AsteroidViewModel;
import aoop.asteroids.view.view_models.BulletViewModel;
import aoop.asteroids.view.view_models.SpaceshipViewModel;
import aoop.asteroids.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The panel at the center of the game's window which is responsible for the custom drawing of game objects.
 */
public class AsteroidsPanel extends JPanel implements GameUpdateListener {

	/**
	 * The x- and y-coordinates of the score indicator.
	 */
	private static final Point SCORE_INDICATOR_POSITION = new Point(20, 20);

	/**
	 * ratio of width used for manu items
	 */
	private static final double WIDTH_RATIO = 0.3;

	/**
	 * menu font
	 */
	private static final Font MENU_FONT = new Font("Helvetica", Font.BOLD, 24);

	/**
	 * command handler for menu item actions
	 */
	private final MenuCommandHandler commandHandler;

	/**
	 * The game model that this panel will draw to the screen.
	 */
	private final Game game;

	/**
	 * Number of milliseconds since the last time the game's physics were updated. This is used to continue drawing all
	 * game objects as if they have kept moving, even in between game ticks.
	 */
	private long timeSinceLastTick = 0L;

	/**
	 * pairs of models and corresonding items for menu
	 */
	private Collection<Pair<Shape,MenuItem>> menuItems;

	/**
	 * Constructs a new game panel, based on the given model. Also starts listening to the game to check for updates, so
	 *  that it can repaint itself if necessary.
	 * @param gs geme server
	 */
	AsteroidsPanel(GameServer gs) {
		initialiseMenuItems();
		commandHandler = new MenuCommandHandler(gs);
		game = gs.getGame();
		game.addListener(this);
	}
	
	/**
	 * The method provided by JPanel for 'painting' this component. It is overridden here so that this panel can define
	 * some custom drawing. By default, a JPanel is just an empty rectangle.
	 * @param graphics The graphics object that exposes various drawing methods to use.
	 */
	@Override
	public synchronized void paintComponent(Graphics graphics) {
		/* The parent method is first called. Here's an excerpt from the documentation stating why we do this:
		"...if you do not invoke super's implementation you must honor the opaque property, that is if this component is
		opaque, you must completely fill in the background in an opaque color. If you do not honor the opaque property
		you will likely see visual artifacts." Just a little FYI.
		 */
		super.paintComponent(graphics);
		// The Graphics2D class offers some more advanced options when drawing, so before doing any drawing, this is obtained simply by casting.
		Graphics2D graphics2D = (Graphics2D) graphics;
		// Set some key-value options for the graphics object. In this case, this just sets antialiasing to true.
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Since the game takes place in space, it is efficient to just lazily make the background black.
		setBackground(Color.BLACK);

		drawGameObjects(graphics2D);
		if (game.isAsteroidsOnly()) drawMainMenu(graphics2D);
		else drawShipInformation(graphics2D);
	}

	/**
	 * Draws the ship's score and energy.
	 * @param graphics2D The graphics object that provides the drawing methods.
	 */
	private void drawShipInformation(Graphics2D graphics2D) {
		graphics2D.setColor(Color.WHITE);
		graphics2D.drawString(
				String.valueOf(game.getSpaceship().getScore()),
				SCORE_INDICATOR_POSITION.x,
				SCORE_INDICATOR_POSITION.y
		);
		graphics2D.setColor(Color.GREEN);
		graphics2D.drawRect(SCORE_INDICATOR_POSITION.x, SCORE_INDICATOR_POSITION.y + 20, 100, 15);
		graphics2D.fillRect(SCORE_INDICATOR_POSITION.x, SCORE_INDICATOR_POSITION.y + 20, (int) game.getSpaceship().getEnergyPercentage(), 15);
	}

	/**
	 * Draws all of the game's objects. Wraps each object in a view model, then uses that to draw the object.
	 * @param graphics2D The graphics object that provides the drawing methods.
	 */
	private synchronized void drawGameObjects(Graphics2D graphics2D) {
		/*
		 * Because the game engine is running concurrently in its own thread, we must obtain a lock for the game model
		 * while drawing to ensure that we don't encounter a concurrentModificationException, which would happen if we
		 * were in the middle of drawing while the game engine starts a new physics update.
		 */
		synchronized (game) {
			if (!game.isAsteroidsOnly()) {
				ArrayList<Spaceship> ships = new ArrayList<>(game.getSpaceships());
				if (game.isSpectate()) ships.remove(0);
				ships.forEach(ship -> new SpaceshipViewModel(ship).drawObject(graphics2D, timeSinceLastTick));
			}
			Collection<Asteroid> as = new ArrayList<>(game.getAsteroids());
			as.forEach(asteroid -> new AsteroidViewModel(asteroid).drawObject(graphics2D, timeSinceLastTick));
			Collection<Bullet> bs = new ArrayList<>(game.getBullets());
			bs.forEach(bullet -> new BulletViewModel(bullet).drawObject(graphics2D, timeSinceLastTick));
		}
	}

	/**
	 * Draws the menu
	 * @param g The graphics object that provides the drawing methods.
	 */
	private void drawMainMenu(Graphics2D g) {
		g.setStroke(new BasicStroke(6));
		g.setColor(Color.WHITE);
		for (Pair<Shape, MenuItem> pair: menuItems) {
			g.draw(pair.getFirst());
			drawString(g, pair.getFirst(), pair.getSecond());
		}
	}

	/**
	 * This method draws name of menu item inside the shape
	 * @param g The graphics object that provides the drawing methods.
	 * @param shape shape to be drawn
	 * @param menuItem menu item to be drawn
	 */
	private void drawString(Graphics2D g, Shape shape, MenuItem menuItem) {
		FontMetrics metrics = g.getFontMetrics(MENU_FONT);
		int x = shape.getBounds().x + (shape.getBounds().width - metrics.stringWidth(menuItem.getTitle())) / 2;
		int y = shape.getBounds().y + ((shape.getBounds().height- metrics.getHeight()) / 2) + metrics.getAscent();
		g.setFont(MENU_FONT);
		g.drawString(menuItem.getTitle(), x, y);
	}

	/**
	 * This method calculates positions in menu
	 */
	private void initialiseMenuItems() {
		menuItems = new ArrayList<>();
		int width = (int) (AsteroidsFrame.WINDOW_SIZE.width * WIDTH_RATIO);
		int height = AsteroidsFrame.WINDOW_SIZE.height / (2 * MenuItem.getNumberOfMenuItems() + 1);
		int widthStart = AsteroidsFrame.WINDOW_SIZE.width / 2 - (width / 2);
		int i = 0;
		for (MenuItem item: MenuItem.getMenuItems()) {
			menuItems.add(new Pair<>(new Ellipse2D.Double(widthStart,height + 2 * i * height, width, height), item));
			i++;
		}
	}

	/**
	 * Do something when the game has indicated that it is updated. For this panel, that means redrawing.
	 * @param timeSinceLastTick The number of milliseconds since the game's physics were updated. This is used to allow
	 *                          objects to continue to appear animated between each game tick.
	 *
	 * Note for your information: when repaint() is called, Swing does some internal stuff, and then paintComponent()
	 * is called.
	 */
	@Override
	public void onGameUpdated(long timeSinceLastTick) {
		this.timeSinceLastTick = timeSinceLastTick;
		repaint();
	}

	/**
	 * @return shape-menu item pairs collection
	 */
	public Collection<Pair<Shape, MenuItem>> getMenuItems() {
		return menuItems;
	}

	/**
	 * @return menu command handler
	 */
	public MenuCommandHandler getCommandHandler() {
		return commandHandler;
	}

	/**
	 * @return game
	 */
	public Game getGame() {
		return game;
	}
}
