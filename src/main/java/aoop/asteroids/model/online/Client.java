package aoop.asteroids.model.online;

import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.game.ByteModel;

import java.io.IOException;
import java.net.*;

/**
 * This is a class of a client that connect to the server
 */
public class Client extends PacketHandler implements Runnable {

    public static final int JOIN_SIGNAL = 1;
    public static final int MAINTAIN_SIGNAL = 2;
    public static final int RECEIVED_SIGNAL = 3;
    public static final int GAME_SIGNAL = 4;
    public static final int SPECTATE_SIGNAL = 5;

    private DatagramSocket datagramSocket;
    private final Game game;
    private InetAddress IP;

    /**
     * Constructor
     * @param game of the client
     */
    public Client(Game game) {
        super();
        this.game = game;
        try {
            datagramSocket = new DatagramSocket();
            IP = InetAddress.getLocalHost();
        } catch (IOException e) {
            System.out.println("Connection problem, cannot make socket.");
        }
    }

    /**
     * initialises connection with the server
     * @return true if successful
     */
    private boolean initialiseConnection() {
        int response;
        ByteModel bytes;
        do {
            if (game.isSpectate()) {
                send(datagramSocket, SPECTATE_SIGNAL, 0, IP, Server.PORT_NUMBER);
            }
            if (game.isClient()) {
                send(datagramSocket, JOIN_SIGNAL, 0, IP, Server.PORT_NUMBER);
            }
            bytes = new ByteModel(receive(datagramSocket).getData());
            response = bytes.getInt();
            if (response < 0) return false;
        } while (response != RECEIVED_SIGNAL);
        if (!game.isSpectate()) game.getSpaceship().setID(bytes.getInt());
        return true;
    }

    /**
     * maintains connection by sending maintain signals to the server and
     * load the game based on the instructions of the server.
     */
    @Override
    public void run() {
        if (!initialiseConnection()) {
            System.out.println("Initialisation failed");
            return;
        }
        running = true;
        while (running) {
            send(datagramSocket, MAINTAIN_SIGNAL, game.getSpaceship().getInputValue(), IP, Server.PORT_NUMBER);
            ByteModel bytes = new ByteModel(receiveBytes(datagramSocket));
            if (bytes.getInt() == GAME_SIGNAL) {
                bytes.loadGame(game);
                game.notifyListeners(0L);
            }
        }
    }
}
