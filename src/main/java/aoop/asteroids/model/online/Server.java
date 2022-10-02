package aoop.asteroids.model.online;

import aoop.asteroids.game_observer.GameUpdateListener;
import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.game.ByteModel;
import aoop.asteroids.model.game.Spaceship;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is a class for host server of the game
 */
public class Server extends PacketHandler implements GameUpdateListener, Runnable {

    public static final int PORT_NUMBER = 55554;

    private final Game game;
    private final ArrayList<Connection> connections;

    /**
     * Constructor - sets default values of the fields
     * @param game being host
     */
    public Server(Game game) {
        super();
        this.game = game;
        game.addListener(this);
        connections = new ArrayList<>();
    }

    /**
     * server listens to connections and behaves accordignly
     */
    @Override
    public void run() {
        try (DatagramSocket ds = new DatagramSocket(PORT_NUMBER)) {
            int shipID = -1;
            running = true;
            while (running) {
                DatagramPacket dp = receive(ds);
                ByteModel bytes = new ByteModel(dp.getData());
                int outcome = bytes.getInt();
                if (outcome == Client.JOIN_SIGNAL) {
                    if (findConnection(dp.getAddress()) == null) {
                        shipID = game.addSpaceShip();
                        connections.add(new Connection(game, ds, dp, shipID));
                    }
                    send(ds, Client.RECEIVED_SIGNAL, shipID, dp.getAddress(), dp.getPort());
                }
                if (outcome == Client.MAINTAIN_SIGNAL) {
                    Connection c = findConnection(dp.getAddress());
                    moveSpaceship(bytes);
                    if (c != null) {
                        c.setLastTick(game.getLastLocalTick());
                        c.setRunning(true);
                    }
                }
                if (outcome == Client.SPECTATE_SIGNAL) {
                    if (findConnection(dp.getAddress()) == null) {
                        connections.add(new Connection(game, ds, dp, -1));
                    }
                    send(ds, Client.RECEIVED_SIGNAL, 0, dp.getAddress(), dp.getPort());
                }
            }
        } catch (IOException e) {
            System.out.println("Connection problem");
        }
    }

    /**
     * moves the spaceship of the client
     * @param bytes to be processed
     */
    private void moveSpaceship(ByteModel bytes) {
        if (game.getSpaceships().size() > 1) {
            Spaceship s = game.getSpaceships().get(1);
            s.setIsFiring(bytes.getBoolean());
            s.setAccelerateKeyPressed(bytes.getBoolean());
            s.setTurnRightKeyPressed(bytes.getBoolean());
            s.setTurnLeftKeyPressed(bytes.getBoolean());
        }
    }

    /**
     * looks for connection is in connections list on IP
     * @param IP to be looked for
     * @return connection if found
     */
    private Connection findConnection(InetAddress IP) {
        for (Connection c: connections) {
            if (c.getInetAddres().equals(IP)) return c;
        }
        return null;
    }

    /**
     * After each update of the game sends the relevant game state to all active connections
     * @param timeSinceLastTick The number of milliseconds that have passed since the last game tick occurred. This is
     *                          used so that things like a display may continue showing an animated model while no
     */
    @Override
    public void onGameUpdated(long timeSinceLastTick) {
        Iterator<Connection> iter = connections.iterator();
        while(iter.hasNext()) {
            Connection c = iter.next();
            c.sendGame();
            //removes inactive connections from the list
            if (game.getLastLocalTick() - c.getLastTick() > Connection.MAX_NO_RESPONSE_TIME) {
                iter.remove();
            }
        }
    }
}
