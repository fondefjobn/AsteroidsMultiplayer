package aoop.asteroids.util;

/**
 * Class for Pair of 2 Objects
 * @param <E> type if first object
 * @param <F> type of second object
 */
public class Pair<E,F> {

    private final E first;
    private final F second;

    /**
     * Constructor
     * @param first element of the pair
     * @param second element of the pair
     */
    public Pair(E first, F second) {
        this.first = first;
        this.second = second;
    }

    /**
     * getter for the first element
     * @return first element
     */
    public E getFirst() {
        return first;
    }

    /**
     * getter for the second element
     * @return second element
     */
    public F getSecond() {
        return second;
    }
}
