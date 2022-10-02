package aoop.asteroids.model.game;

import aoop.asteroids.control.GameUpdater;
import aoop.asteroids.game_observer.ObservableGame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class is the main model for the Asteroids game. It contains all game objects, and has methods to start and stop
 * the game.
 *
 * This is strictly a model class, containing only the state of the game. Updates to the game are done in
 * {@link GameUpdater}, which runs in its own thread, and manages the main game loop and physics updates.
 */
public class Game extends ObservableGame {

	public static final String PLAYER_NAME = "Player";

	/**
	 * The list of all bullets currently active in the game.
	 */
	private Collection<Bullet> bullets;

	/**
	 * The list of all asteroids in the game.
	 */
	private Collection<Asteroid> asteroids;

	/**
	 * The list of all spaceships. First one is the player, next are other players
	 */
	private ArrayList<Spaceship> spaceships;

	/**
	 * Indicates whether or not the game is running. Setting this to false causes the game to exit its loop and quit.
	 */
	private volatile boolean running;

	/**
	 * Indicates if the game should be played wil only asteroids (used for menu)
	 */
	private boolean asteroidsOnly;

	/**
	 * Indicates if the game should behave as a spectating one
	 */
	private boolean spectate;

	/**
	 * Indicates if the game should behave as a host
	 */
	private boolean host;

	/**
	 * Indicates if the game should behave as a client
	 */
	private boolean client;

	/**
	 * last tick of game updater
	 */
	private int lastLocalTick;

	/**
	 * if client, last received tick from the server
	 */
	private int lastReceivedTick;

	/**
	 * The game updater thread, which is responsible for updating the game's state as time goes on.
	 */
	private Thread gameUpdaterThread;

	/**
	 * Constructs a new game, with a new spaceship and all other model data in its default starting state.
	 */
	public Game() {
		initializeGameData();
	}

	/**
	 * Initializes all of the model objects used by the game. Can also be used to reset the game's state back to a
	 * default starting state before beginning a new game.
	 */
	public void initializeGameData() {
		Spaceship spaceship;
		//saving spaceship not to lose player key listener
		if (spaceships != null && spaceships.size() > 0) {
			spaceship = spaceships.get(0);
			spaceship.reset();
		}
		else {
			spaceship = new Spaceship();
		}
		resetLists();
		spaceships.add(spaceship);
	}

	/**
	 * resets flags and counters
	 */
	public void reset() {
		asteroidsOnly = false;
		spectate = false;
		running = false;
		host = false;
		client = false;
		lastLocalTick = 0;
		lastReceivedTick = 0;
	}

	/**
	 * resets collections for game objects
	 */
	public void resetLists() {
		bullets = new ArrayList<>();
		asteroids = new ArrayList<>();
		spaceships = new ArrayList<>();
	}

	/**
	 * adds another spaceship to the game
	 * @return id of the spaceship
	 */
	public int addSpaceShip() {
		Point.Double freeLocation = findFreeLocation();
		if (freeLocation == null) return - 1;
		Spaceship spaceship = new Spaceship(freeLocation);
		spaceships.add(spaceship);
		return spaceship.getID();
	}

	/**
	 * Using this game's current model, spools up a new game updater thread to begin a game loop and start processing
	 * user input and physics updates. Only if the game isn't currently running, that is.
	 */
	public void start() {
		if (!running) {
			running = true;
			gameUpdaterThread = new Thread(new GameUpdater(this));
			gameUpdaterThread.start();
		}
	}

	/**
	 * Tries to quit the game, if it is running.
	 */
	public void quit() {
		if (running) {
			running = false;
			gameUpdaterThread.interrupt();
			gameUpdaterThread = null; // Throw away the game updater thread and let the GC remove it.
		}
	}

	/**
	 * restarts the game
	 */
	public void restart() {
		quit();
		initializeGameData();
		start();
	}

	/**
	 * finds free location for the spaceship
	 * @return free location as Point2D.Double
	 */
	private Point2D.Double findFreeLocation() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		Point.Double newLocation;
		double distanceX, distanceY;
		int i = 100;
		ArrayList<GameObject> objects = new ArrayList<>();
		objects.addAll(asteroids);
		objects.addAll(spaceships);
		while (i > 0) { // Iterate until a point is found that is far enough away from the player.
			newLocation = new Point.Double(rng.nextDouble(0.0, 800.0), rng.nextDouble(0.0, 800.0));
			boolean success = true;
			for (GameObject obj: objects) {
				distanceX = newLocation.x - obj.getLocation().x;
				distanceY = newLocation.y - obj.getLocation().y;
				// Pythagorean theorem for distance between two points.
				if (distanceX * distanceX + distanceY * distanceY < 50 * 50) {
					success = false;
					break;
				}
			}
			if (success) return newLocation;
			i--;
		}
		return null;
	}

	/**
	 * @return The game's spaceship.
	 */
	public Spaceship getSpaceship() {
		if (spaceships.size() > 0) return spaceships.get(0);
		return null;
	}

	/**
	 * @return The collection of asteroids in the game.
	 */
	public Collection<Asteroid> getAsteroids() {
		return asteroids;
	}

	/**
	 * @return The collection of bullets in the game.
	 */
	public Collection<Bullet> getBullets () {
		return bullets;
	}

	/**
	 * @return spaceships
	 */
	public ArrayList<Spaceship> getSpaceships() {
		return spaceships;
	}

	/**
	 * @return asteroidsOnly
	 */
	public boolean isAsteroidsOnly() {
		return asteroidsOnly;
	}

	/**
	 * @return spectate
	 */
	public boolean isSpectate() {
		return spectate;
	}

	/**
	 * @return host
	 */
	public boolean isHost() {
		return host;
	}

	/**
	 * @return client
	 */
	public boolean isClient() {
		return client;
	}

	/**
	 * setter for asteroidsOnly
	 * @param asteroidsOnly to be set
	 */
	public void setAsteroidsOnly(boolean asteroidsOnly) {
		this.asteroidsOnly = asteroidsOnly;
	}

	/**
	 * setter for spectate
	 * @param spectate to be set
	 */
	public void setSpectate(boolean spectate) {
		this.spectate = spectate;
	}

	/**
	 * setter for host
	 * @param host to be set
	 */
	public void setHost(boolean host) {
		this.host = host;
	}

	/**
	 * setter for client
	 * @param client to be set
	 */
	public void setClient(boolean client) {
		this.client = client;
	}

	/**
	 * @return Whether or not the game is running.
	 */
	public synchronized boolean isRunning() {
		return running;
	}

	/**
	 * @return True if the player's ship has been destroyed, or false otherwise.
	 */
	public boolean isGameOver() {
		if (getSpaceship() == null) return false;
		return getSpaceship().isDestroyed();
	}

	/**
	 * @return lastLocalTick
	 */
	public int getLastLocalTick() {
		return lastLocalTick;
	}

	/**
	 * setter for lastLocalTick
	 * @param lastLocalTick to be set
	 */
	public void setLastLocalTick(int lastLocalTick) {
		this.lastLocalTick = lastLocalTick;
	}

	/**
	 * @return lastReceivedTick
	 */
	public int getLastReceivedTick() {
		return lastReceivedTick;
	}

	/**
	 * setter for lastReceivedTick
	 * @param lastReceivedTick to be set
	 */
	public void setLastReceivedTick(int lastReceivedTick) {
		this.lastReceivedTick = lastReceivedTick;
	}
}
