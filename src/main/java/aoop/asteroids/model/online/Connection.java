package aoop.asteroids.model.online;

import aoop.asteroids.model.game.Game;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This is a class of connection of a client to the server
 */
public class Connection extends PacketHandler {

    public static final int MAX_NO_RESPONSE_TIME = 30;

    private final DatagramPacket dp;
    private final DatagramSocket ds;
    private final Game game;
    private int lastTick;
    private final int shipID;

    /**
     * Constructor
     * @param game game played on the server
     * @param ds socket of the connection
     * @param dp packet received
     * @param shipID id of the ship of the client
     */
    public Connection(Game game, DatagramSocket ds, DatagramPacket dp, int shipID) {
        super();
        this.game = game;
        this.dp = dp;
        this.ds = ds;
        this.shipID = shipID;
        lastTick = game.getLastLocalTick();
        sendShipId();
    }

    /**
     * sends the ID of the ship to the client
     */
    public void sendShipId() {
        send(ds, Client.RECEIVED_SIGNAL, shipID, dp.getAddress(), dp.getPort());
    }

    /**
     * sends game to the client if the client received it's ship id, otherwise tries to send ship ID
     */
    public void sendGame() {
        if (running && MAX_NO_RESPONSE_TIME > game.getLastLocalTick() - lastTick) {
            send(ds, Client.GAME_SIGNAL, game, dp.getAddress(), dp.getPort());
        }
        if (!running && MAX_NO_RESPONSE_TIME > game.getLastLocalTick() - lastTick) {
            sendShipId();
        }
    }

    /**
     * @return last tick of the game
     */
    public int getLastTick() {
        return lastTick;
    }

    /**
     * setter for last tick of the game
     * @param lastTick sets last tick
     */
    public void setLastTick(int lastTick) {
        this.lastTick = lastTick;
    }

    /**
     * @return IP of the client
     */
    public InetAddress getInetAddres() {
        return dp.getAddress();
    }
}
