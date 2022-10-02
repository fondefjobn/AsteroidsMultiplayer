package aoop.asteroids.model.game;

/**
 * This is a class for score record
 */
public class Score {

    private int id;
    private String name;
    private int points;

    /**
     * Constructor
     * @param name name of the player
     * @param points achieved
     */
    public Score(String name, int points){
        this.name = name;
        this.points = points;
    }

    /**
     * Empty constructor
     */
    public Score() {
    }

    /**
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * setter for point
     * @param id to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * setter for name
     * @param name to be set
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * @return points
     */
    public int getPoints() {
        return points;
    }

    /**
     * setter for point
     * @param points to be set
     */
    public void setPoints(int points) {
        this.points = points;
    }
}
