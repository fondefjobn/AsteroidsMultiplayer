package aoop.asteroids.model;

import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.online.Client;
import aoop.asteroids.model.online.Server;

/**
 * This class groups the game and it's online features
 */
public class GameServer {

    private boolean highScores;

    private final Game game;

    private Thread threadServer;

    private Thread threadClient;

    private Server server;

    private Client client;

    /**
     * Constructor
     */
    public GameServer() {
        game = new Game();
        server = new Server(game);
        client = new Client(game);
        highScores = false;
    }

    /**
     * resets game server to the default state
     */
    public void reset() {
        highScores = false;
        quitServer();
        quitClient();
        game.reset();
    }

    /**
     * start server
     */
    public void startServer() {
        server = new Server(game);
        threadServer = new Thread(server);
        threadServer.start();
    }

    /**
     * starts client
     */
    public void startClient() {
        client = new Client(game);
        threadClient = new Thread(client);
        threadClient.start();
    }

    /**
     * quits server
     */
    public void quitServer() {
        server.setRunning(false);
        threadServer = new Thread(new Server(game));
    }

    /**
     * quits client
     */
    public void quitClient() {
        client.setRunning(false);
        threadClient = new Thread(new Client(game));
    }

    /**
     * @return game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @return high-scores flag value
     */
    public boolean isHighScores() {
        return highScores;
    }

    /**
     * setter for high-scores flag
     * @param highScores value to be set
     */
    public void setHighScores(boolean highScores) {
        this.highScores = highScores;
    }
}
