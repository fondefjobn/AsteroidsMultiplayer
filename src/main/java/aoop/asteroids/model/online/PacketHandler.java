package aoop.asteroids.model.online;

import aoop.asteroids.model.game.Game;
import aoop.asteroids.model.game.ByteModel;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * This is a class that is bases for other udp connection related classes
 */
public abstract class PacketHandler {

    public static final int MAX_SIZE = 1024;

    protected boolean running;

    /**
     * Constructor
     */
    public PacketHandler() {
        running = false;
    }

    /**
     * sends signal and game to the given ip and port
     * @param ds socket to send from
     * @param signal value of signal
     * @param game game to be send
     * @param ip to send to
     * @param port to send to
     */
    public void send(DatagramSocket ds, int signal, Game game, InetAddress ip, int port) {
        ByteModel bytes = new ByteModel();
        bytes.add(signal);
        bytes.add(game);
        send(ds, bytes.getByteArray(), ip, port);
    }

    /**
     * sends signal and value to the given ip and port
     * @param ds socket to send from
     * @param signal value of signal
     * @param value value to be send
     * @param ip to send to
     * @param port to send to
     */
    public void send(DatagramSocket ds, int signal, int value, InetAddress ip, int port) {
        ByteModel bytes = new ByteModel();
        bytes.add(signal);
        bytes.add(value);
        send(ds, bytes.getByteArray(), ip, port);
    }

    /**
     * sends byte array to the given ip and port
     * @param ds socket to send from
     * @param data byte array to be send
     * @param ip to send to
     * @param port to send to
     */
    private void send(DatagramSocket ds, byte[] data, InetAddress ip, int port) {
        try {
            ds.send(new DatagramPacket(data, data.length, ip, port));
        } catch (IOException e) {
            System.out.println("Couldn't send data");
        }
    }

    /**
     * receives packet and returns it
     * @param ds socket receiving the packet
     * @return packet if possible, null otherwise
     */
    public DatagramPacket receive(DatagramSocket ds) {
        byte[] data = new byte[MAX_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        try {
            ds.receive(receivePacket);
            return receivePacket;
        } catch (IOException e) {
            System.out.println("Couldn't receive packet");
        }
        return null;
    }

    /**
     * receives packet and returns its data
     * @param ds socket receiving the packet
     * @return data of the packet
     */
    public byte[] receiveBytes(DatagramSocket ds) {
        DatagramPacket dp = receive(ds);
        assert dp != null;
        return dp.getData();
    }

    /**
     * setter for running
     * @param running value to be set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return value of running
     */
    public boolean isRunning() {
        return running;
    }
}
