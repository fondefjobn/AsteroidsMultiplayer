package aoop.asteroids.model.game;

import java.nio.ByteBuffer;

/**
 * This class is responsible for changing model into array of bytes and vice versa
 */
public class ByteModel {

    private static final int SIZE_BOOLEAN = 1;

    private static final int SIZE_BYTE = 1;

    private static final int SIZE_INT = 4;

    private static final int SIZE_DOUBLE = 8;

    private static final int SIZE_GAME_OBJECT = 5 * SIZE_DOUBLE + SIZE_INT;

    private static final int SIZE_SPACESHIP = SIZE_GAME_OBJECT + SIZE_DOUBLE + SIZE_INT;

    private static final int SIZE_BULLET = SIZE_GAME_OBJECT + SIZE_INT;

    private static final int SIZE_ASTEROID = SIZE_GAME_OBJECT;

    private static final int DEFAULT_ARRAY_SIZE = 0;

    private byte[] bytes;
    private int read;
    private int write;

    /**
     * Constructor sets default values for all fields
     */
    public ByteModel() {
        bytes = new byte[DEFAULT_ARRAY_SIZE];
        read = 0;
        write = 0;
    }

    /**
     * This constructor sets default value for field and uses array of bytes as its basis
     * @param bytes array of bytes
     */
    public ByteModel(byte[] bytes) {
        this.bytes = bytes;
        read = 0;
        write = bytes.length;
    }

    /**
     * add value at the end of the byte array
     * @param value boolean
     */
    public void add(boolean value) {
        if (!possibleToWrite(SIZE_BOOLEAN)) increaseArraySize(SIZE_BOOLEAN);
        bytes[write] = value ? (byte) 1 : (byte) 0;
        incrementWrite(SIZE_BOOLEAN);
    }

    /**
     * add value at the end of the byte array
     * @param value byte
     */
    public void add(byte value) {
        if (!possibleToWrite(SIZE_BYTE)) increaseArraySize(SIZE_BYTE);
        bytes[write] = value;
        incrementWrite(SIZE_BYTE);
    }

    /**
     * add value at the end of the byte array
     * @param value int
     */
    public void add(int value) {
        if (!possibleToWrite(SIZE_INT)) increaseArraySize(SIZE_INT);
        ByteBuffer.wrap(bytes).putInt(write, value);
        incrementWrite(SIZE_INT);
    }

    /**
     * add value at the end of the byte array
     * @param value double
     */
    public void add(double value) {
        if (!possibleToWrite(SIZE_DOUBLE)) increaseArraySize(SIZE_DOUBLE);
        ByteBuffer.wrap(bytes).putDouble(write, value);
        incrementWrite(SIZE_DOUBLE);
    }

    /**
     * adds spaceship at the end of the byte array
     * @param spaceship to be added
     */
    public void add(Spaceship spaceship) {
        if (!possibleToWrite(SIZE_SPACESHIP)) increaseArraySize(SIZE_SPACESHIP);
        addGameObject(spaceship);
        add(spaceship.getID());
        add(spaceship.getDirection());
    }

    /**
     * adds bullet at the end of the byte array
     * @param bullet to be added
     */
    public void add(Bullet bullet) {
        if (!possibleToWrite(SIZE_BULLET)) increaseArraySize(SIZE_BULLET);
        addGameObject(bullet);
        add(bullet.getStepsLeft());
    }

    /**
     * adds asteroid at the end of the byte array
     * @param asteroid to be added
     */
    public void add(Asteroid asteroid) {
        if (!possibleToWrite(SIZE_ASTEROID)) increaseArraySize(SIZE_ASTEROID);
        addGameObject(asteroid);
    }

    /**
     * adds Game at the end of the byte array
     * @param game to be added
     */
    public void add(Game game) {
        int as = game.getAsteroids().size();
        int bs = game.getBullets().size();
        int ss = game.getSpaceships().size();
        int totalSize = 3 * SIZE_BYTE + as * SIZE_ASTEROID + bs * SIZE_BULLET + ss * SIZE_SPACESHIP + SIZE_INT;
        if (!possibleToWrite(totalSize)) increaseArraySize(totalSize);
        add(game.getLastLocalTick());
        add((byte) game.getSpaceships().size());
        for (Spaceship ship: game.getSpaceships()) {
            add(ship);
        }
        add((byte) game.getAsteroids().size());
        for (Asteroid asteroid: game.getAsteroids()) {
            add(asteroid);
        }
        add((byte) game.getBullets().size());
        for (Bullet bullet: game.getBullets()) {
            add(bullet);
        }
    }

    /**
     * adds game object at the end of the byte array
     * @param object - game object to be added
     */
    public void addGameObject(GameObject object) {
        if (!possibleToWrite(SIZE_GAME_OBJECT)) increaseArraySize(SIZE_GAME_OBJECT);
        add(object.getLocation().x);
        add(object.getLocation().y);
        add(object.getVelocity().x);
        add(object.getVelocity().y);
        add(object.getRadius());
        add(object.getStepsUntilCollisionPossible());
    }

    /**
     * returns true if its possible to write given number of bytes
     * @param size of read
     */
    private boolean possibleToWrite(int size) {
        return bytes.length >= write + size;
    }

    /**
     * @return boolean at the current read pointer
     */
    public boolean getBoolean() {
        boolean b= bytes[read] == 1;
        incrementRead(SIZE_BOOLEAN);
        return b;
    }

    /**
     * @return byte at the current read pointer
     */
    public byte getByte() {
        byte b = bytes[read];
        incrementRead(SIZE_BYTE);
        return b;
    }

    /**
     * @return int at the current read pointer
     */
    public int getInt() {
        int x = ByteBuffer.wrap(bytes).getInt(read);
        incrementRead(SIZE_INT);
        return x;
    }

    /**
     * @return double at the current read pointer
     */
    public double getDouble() {
        double x = ByteBuffer.wrap(bytes).getDouble(read);
        incrementRead(SIZE_DOUBLE);
        return x;
    }

    /**
     * @return spaceship at the current read pointer
     */
    public Spaceship getSpaceship() {
        return new Spaceship(getDouble(), getDouble(), getDouble(), getDouble(), getDouble(), getInt(),
                getInt(), getDouble());
    }

    /**
     * @return asteroid at the current read pointer
     */
    public Asteroid getAsteroid() {
        return new Asteroid(getDouble(), getDouble(), getDouble(), getDouble(), getDouble(), getInt());
    }

    /**
     * @return bullet at the current read pointer
     */
    public Bullet getBullet() {
        return new Bullet(getDouble(), getDouble(), getDouble(), getDouble(), getDouble(), getInt(), getInt());
    }

    /**
     * loads game from byte array
     * @param game to be loaded into
     */
    public void loadGame(Game game) {
        int tickID = getInt();
        if (tickID < game.getLastReceivedTick()) return;
        else game.setLastReceivedTick(tickID);
        game.quit();
        Spaceship spaceship;
        if (game.getSpaceship() != null && game.getSpaceships().size() > 0) spaceship = game.getSpaceship();
        else spaceship = new Spaceship();
        game.resetLists();
        game.getSpaceships().add(spaceship);
        int length = getByte();
        for (int i = 0; i < length; i++) {
            Spaceship newShip = getSpaceship();
            if (newShip.getID() != spaceship.getID()) game.getSpaceships().add(newShip);
        }
        length = getByte();
        for (int i = 0; i < length; i++) {
            game.getAsteroids().add(getAsteroid());
        }
        length = getByte();
        for (int i = 0; i < length; i++) {
            game.getBullets().add(getBullet());
        }
    }

    /**
     * increases the array size
     * @param length to be increase by
     */
    private void increaseArraySize(int length) {
        byte[] newBytes = new byte[bytes.length + length];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        bytes = newBytes;
    }

    /**
     * increases read pointer
     * @param value of increase
     */
    private void incrementRead(int value) {
        read += value;
    }

    /**
     * increases write pointer
     * @param value of increase
     */
    private void incrementWrite(int value) {
        write += value;
    }

    /**
     * @return byte array
     */
    public byte[] getByteArray() {
        return bytes;
    }
}
