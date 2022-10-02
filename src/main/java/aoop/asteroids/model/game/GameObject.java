package aoop.asteroids.model.game;

import aoop.asteroids.view.AsteroidsFrame;

import java.awt.*;

/**
 * This class represents any object that is present in a game, such as a bullet, asteroid, or a player's ship. As an
 * abstract class, it provides some basic attributes that all objects in the game should have, like position and
 * velocity.
 */
public abstract class GameObject {

	/**
	 * An x and y value pair indicating the object's current location.
	 */
	private Point.Double location;

	/**
	 * An x and y value pair indicating the object's current velocity, in pixels per game tick.
	 */
	private final Point.Double velocity;

	/** Radius of the object. */
	private final double radius;

	/**
	 * A flag that is set when this object collides with another. This tells the game engine that this object should be
	 * removed from the game.
	 */
	protected boolean destroyed;

	/**
	 * The number of game ticks that must pass before this object is allowed to collide with other game objects. This
	 * can also be thought of as a grace period, or temporary immunity.
	 */
	private int stepsUntilCollisionPossible;

	/**
	 * Constructs a new game object with the specified location, velocity and radius.
	 * @param locationX The object's location on the x-axis.
	 * @param locationY The object's location on the y-axis.
	 * @param velocityX Velocity in X direction.
	 * @param velocityY Velocity in Y direction.
	 * @param radius Radius of the object.
	 */
	public GameObject(double locationX, double locationY, double velocityX, double velocityY, double radius) {
		location = new Point.Double(locationX, locationY);
		velocity = new Point.Double(velocityX, velocityY);
		this.radius = radius;
		stepsUntilCollisionPossible = getDefaultStepsUntilCollisionPossible();
	}

	/**
	 * A convenience constructor that accepts points instead of individual coordinates.
	 * @param location A point representing the x- and y-coordinates of the object's location.
	 * @param velocity A point representing the object's speed on both the x and y axes.
	 * @param radius The radius of the object.
	 */
	public GameObject(Point.Double location, Point.Double velocity, double radius) {
		this(location.getX(), location.getY(), velocity.getX(), velocity.getY(), radius);
	}

	/**
	 * Constructor for loading
	 * @param locationX location x of the object
	 * @param locationY location y of the object
	 * @param velocityX velocity x of the object
	 * @param velocityY velocity y of the object
	 * @param radius radius of the object
	 * @param steps until collision is possible
	 */
	public GameObject(double locationX, double locationY, double velocityX, double velocityY, double radius, int steps) {
		this(locationX, locationY, velocityX, velocityY, radius);
		stepsUntilCollisionPossible = steps;
	}

	/**
	 * Child classes should implement this method to define what happens to an object when the game advances by one game
	 * tick in the main loop. The amount of time that passes with each step should be the same, so that movement is
	 * uniform even when performance may suffer.
	 */
	public void nextStep() {
		location.x = (AsteroidsFrame.WINDOW_SIZE.width + location.x + velocity.x) % AsteroidsFrame.WINDOW_SIZE.width;
		location.y = (AsteroidsFrame.WINDOW_SIZE.height + location.y + velocity.y) % AsteroidsFrame.WINDOW_SIZE.height;
		if (stepsUntilCollisionPossible > 0) {
			stepsUntilCollisionPossible--;
		}
	}

	/**
	 * Flags this object as destroyed, so that the game may deal with it.
	 */
	public final void destroy() {
		destroyed = true;
	}
	
	/**
	 * @return radius of the object in amount of pixels.
	 */
	public double getRadius()
	{
		return radius;
	}

	/**
	 * @return The current location of this object.
	 */
	public Point.Double getLocation() {
		return location;
	}

	/**
	 * setter for location
	 * @param location to be set
	 */
	public void setLocation(Point.Double location) {
		this.location = location;
	}

	/**
	 * @return The current velocity of this object.
	 */
	public Point.Double getVelocity() {
		return velocity;
	}

	/**
	 * @return The speed of the object, as a scalar value combining the x- and y-velocities.
	 */
	public double getSpeed() {
		return getVelocity().distance(0, 0); // A cheap trick: distance() is doing Math.sqrt(px * px + py * py) internally.
	}

	public int getStepsUntilCollisionPossible() {
		return stepsUntilCollisionPossible;
	}

	/**
	 * @return true if the object is destroyed, false otherwise.
	 */
	public final boolean isDestroyed()
	{
		return destroyed;
	}

	/**
	 * Given some other game object, this method checks whether the current object and the given object collide with
	 * each other. It does this by measuring the distance between the objects and checking whether it is larger than the
	 * sum of the radii. Furthermore both objects should be allowed to collide.
	 * @param other The other object that it may collide with.
	 * @return True if object collides with given object, false otherwise.
	 */
	public boolean collides(GameObject other) {
		return getLocation().distance(other.getLocation()) < getRadius() + other.getRadius()
				&& canCollide() && other.canCollide();
	}

	/**
	 * @return Whether or not this object is immune from collisions.
	 */
	private boolean canCollide() {
		return stepsUntilCollisionPossible <= 0;
	}

	/**
	 * @return The number of steps, or game ticks, for which this object is immune from collisions.
	 */
	protected abstract int getDefaultStepsUntilCollisionPossible();
}
