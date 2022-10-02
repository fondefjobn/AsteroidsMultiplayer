package aoop.asteroids.control;

import aoop.asteroids.model.game.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A runnable object which, when started in a thread, runs the main game loop and periodically updates the game's model
 * as time goes on. This class can be thought of as the 'Game Engine', because it is solely responsible for all changes
 * to the game model as a result of user input, and this class also defines the very important game loop itself.
 */
public class GameUpdater implements Runnable {

    /**
     * The refresh rate of the display, in frames per second. Increasing this number makes the game look smoother, up to
     * a certain point where it's no longer noticeable.
     */
    private static final int DISPLAY_FPS = 30;

    /**
     * The rate at which the game ticks (how often physics updates are applied), in frames per second. Increasing this
     * number speeds up everything in the game. Ships react faster to input, bullets fly faster, etc.
     */
    private static final int PHYSICS_FPS = 30;

    /**
     * The number of milliseconds in a game tick.
     */
    public static final double MILLISECONDS_PER_TICK = 1000.0 / PHYSICS_FPS;

    /**
     * The default maximum number of asteroids that may be present in the game when starting.
     */
    private static final int ASTEROIDS_LIMIT_DEFAULT = 7;

    /**
     * Set this to true to allow asteroids to collide with each other, potentially causing chain reactions of asteroid
     * collisions.
     */
    private static final boolean KESSLER_SYNDROME = true;

    /**
     * The game that this updater works for.
     */
    private final Game game;

    /**
     * Counts the number of times the game has updated.
     */
    private int updateCounter;

    /**
     * The limit to the number of asteroids that may be present. If the current number of asteroids exceeds this amount,
     * no new asteroids will spawn.
     */
    private int asteroidsLimit;

    /**
     * Constructs a new game updater with the given game.
     * @param game The game that this updater will update when it's running.
     */
    public GameUpdater(Game game) {
        this.game = game;
        updateCounter = 0;
        asteroidsLimit = ASTEROIDS_LIMIT_DEFAULT;
    }

    /**
     * The main game loop.
     * Starts the game updater thread. This will run until the quit() method is called on this updater's game object.
     */
    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        long timeSinceLastTick = 0L;
        long timeSinceLastDisplayFrame = 0L;

        final double millisecondsPerDisplayFrame = 1000.0 / DISPLAY_FPS;

        while (game.isRunning() && !game.isGameOver()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;
            timeSinceLastTick += elapsedTime;
            timeSinceLastDisplayFrame += elapsedTime;

            if (timeSinceLastTick >= MILLISECONDS_PER_TICK) { // Check if enough time has passed to update the physics.
                updatePhysics(); // Perform one 'step' in the game.
                timeSinceLastTick = 0L;
            }
            if (timeSinceLastDisplayFrame >= millisecondsPerDisplayFrame) { // Check if enough time has passed to refresh the display.
                game.notifyListeners(timeSinceLastTick); // Tell the asteroids panel that it should refresh.
                timeSinceLastDisplayFrame = 0L;
            }

            previousTime = currentTime;
        }
    }

    /**
     * Called every game tick, to update all of the game's model objects.
     * First, each object's movement is updated by calling nextStep() on it.
     * Then, if the player is pressing the key to fire the ship's weapon, a new bullet should spawn.
     * Then, once all objects' positions are updated, we check for any collisions between them.
     * And finally, any objects which are destroyed by collisions are removed from the game.
     * Also, every 200 game ticks, if possible, a new random asteroid is added to the game.
     */
    private void updatePhysics() {
        Collection<Bullet> bullets = game.getBullets();
        Collection<Asteroid> asteroids = game.getAsteroids();
        Collection<Spaceship> spaceships = game.getSpaceships();
        asteroids.forEach(GameObject::nextStep);
        bullets.forEach(GameObject::nextStep);
        if (!game.isAsteroidsOnly() && !game.isSpectate()) {
            spaceships.forEach(ship -> {
                ship.nextStep();
                if (ship.canFireWeapon()) {
                    double direction = ship.getDirection();
                    bullets.add(new Bullet(
                            ship.getLocation().getX(),
                            ship.getLocation().getY(),
                            ship.getVelocity().x + Math.sin(direction) * 15,
                            ship.getVelocity().y - Math.cos(direction) * 15));
                    ship.setFired();
                }
            });
        }
        if (!game.isSpectate()) {
            checkCollisions();
            removeDestroyedObjects();
        }
        // Every 200 game ticks, try and spawn a new asteroid.
        if (updateCounter % 200 == 0 && asteroids.size() < asteroidsLimit && !game.isSpectate() && !game.isClient()) {
            addRandomAsteroid();
        }
        updateCounter++;
        game.setLastLocalTick(updateCounter);
    }

    /**
     * Adds a random asteroid at least 50 pixels away from the player's spaceship.
     */
    private void addRandomAsteroid() {
        Point.Double newAsteroidLocation = findAsteroidPoint();
        if (newAsteroidLocation == null) return;
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double randomChance = rng.nextDouble();
        Point.Double randomVelocity = new Point.Double(rng.nextDouble() * 6 - 3, rng.nextDouble() * 6 - 3);
        AsteroidSize randomSize;
        if (randomChance < 0.333) { // 33% chance of spawning a large asteroid.
            randomSize = AsteroidSize.LARGE;
        } else if (randomChance < 0.666) { // 33% chance of spawning a medium asteroid.
            randomSize = AsteroidSize.MEDIUM;
        } else { // And finally a 33% chance of spawning a small asteroid.
            randomSize = AsteroidSize.SMALL;
        }
        game.getAsteroids().add(new Asteroid(newAsteroidLocation, randomVelocity, randomSize));
    }

    /**
     * finds a point where asteroid can spawn
     * @return point where asteroid can spawn
     */
    private Point.Double findAsteroidPoint() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        Point.Double newAsteroidLocation;
        double distanceX, distanceY;
        int i = 10;
        boolean success;
        while (i > 0) { // Iterate until a point is found that is far enough away from the player.
            newAsteroidLocation = new Point.Double(rng.nextDouble(0.0, 800.0), rng.nextDouble(0.0, 800.0));
            success = true;
            for (Spaceship ship: game.getSpaceships()) {
                distanceX = newAsteroidLocation.x - ship.getLocation().x;
                distanceY = newAsteroidLocation.y - ship.getLocation().y;
                // Pythagorean theorem for distance between two points.
                if (distanceX * distanceX + distanceY * distanceY < 50 * 50) {
                    success = false;
                    break;
                }
            }
            if (success) return newAsteroidLocation;
            i--;
        }
        return null;
    }

    /**
     * Checks all objects for collisions and marks them as destroyed upon collision. All objects can collide with
     * objects of a different type, but not with objects of the same type. I.e. bullets cannot collide with bullets etc.
     */
    private void checkCollisions() {
        // First check collisions between bullets and other objects.
        game.getBullets().forEach(bullet -> {
            game.getAsteroids().forEach(asteroid -> { // Check collision with any of the asteroids.
                if (asteroid.collides(bullet)) {
                    asteroid.destroy();
                    bullet.destroy();
                }
            });
            if (!game.isAsteroidsOnly()) {
                game.getSpaceships().forEach(ship -> {
                    if (ship.collides(bullet)) { // Check collision with ships.
                        bullet.destroy();
                        ship.destroy();
                    }
                });
            }
        });
        // Next check for collisions between asteroids and the spaceship.
        game.getAsteroids().forEach(asteroid -> {
            if (!game.isAsteroidsOnly()) {
                game.getSpaceships().forEach(ship -> {
                    if (asteroid.collides(ship)) { // Check collision with ships.
                        asteroid.destroy();
                        ship.destroy();
                    }
                });
            }
            if (KESSLER_SYNDROME) { // Only check for asteroid - asteroid collisions if we allow kessler syndrome.
                game.getAsteroids().forEach(secondAsteroid -> {
                    if (!asteroid.equals(secondAsteroid) && asteroid.collides(secondAsteroid)) {
                        asteroid.destroy();
                        secondAsteroid.destroy();
                    }
                });
            }
        });
    }

    /**
     * Increment the player's score, and for every five score points, the asteroids limit is incremented.
     */
    private void increaseScore() {
        game.getSpaceship().increaseScore();
        if (game.getSpaceship().getScore() % 5 == 0) {
            asteroidsLimit++;
        }
    }

    /**
     * Removes all destroyed objects (those which have collided with another object).
     * When an asteroid is destroyed, it may spawn some smaller successor asteroids, and these are added to the game's
     * list of asteroids.
     */
    private void removeDestroyedObjects() {
        // Avoid reallocation and assume every asteroid spawns successors.
        Collection<Asteroid> newAsteroids = new ArrayList<>(game.getAsteroids().size() * 2);
        game.getAsteroids().forEach(asteroid -> {
            if (asteroid.isDestroyed()) {
                increaseScore();
                newAsteroids.addAll(asteroid.getSuccessors());
            }
        });
        game.getAsteroids().addAll(newAsteroids);
        // Remove all asteroids that are destroyed.
        game.getAsteroids().removeIf(GameObject::isDestroyed);
        // Remove any bullets that are destroyed.
        game.getBullets().removeIf(GameObject::isDestroyed);
    }
}
