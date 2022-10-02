package aoop.asteroids.model.game;

/**
 * This enumeration defines the different possible sizes of asteroids.
 */
public enum AsteroidSize {

	SMALL(10),
	MEDIUM(20),
	LARGE(40);

	/**
	 * The radius that each different asteroid size is.
	 */
	private final double radius;

	/**
	 * Constructor that requires that any new enumeration values provide a valid radius.
	 *
	 * @param radius The radius of the asteroid.
	 */
	AsteroidSize(double radius) {
		this.radius = radius;
	}

	/**
	 * @return The radius of this asteroid size.
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * @return The size of asteroids that are produced when this one is destroyed. May return null if this asteroid is
	 * too small to produce successors.
	 */
	public AsteroidSize getSuccessorSize() {
		if (equals(LARGE)) {
			return MEDIUM;
		} else if (equals(MEDIUM)) {
			return SMALL;
		} else {
			return null;
		}
	}
}
