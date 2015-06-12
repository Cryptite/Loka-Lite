package com.cryptite.lite.network;

import com.cryptite.lite.network.events.*;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class SocketHandler extends Thread {
    public String name;

    private Socket sock;

    private OutputStream out;

    private SocketConnected connected;
    private SocketDisconnected disconnected;
    private MessageReceived message;
    private SocketHandlerReady ready;

    private String hostName;

    private int id;

    public SocketHandler() {
        this.disconnected = new SocketDisconnected();
        this.message = new MessageReceived();
        this.connected = new SocketConnected();
        this.ready = new SocketHandlerReady();
    }

    public SocketHandler(Socket sock, int id) {

        this.sock = sock;
        this.id = id;

        this.connected = new SocketConnected();
        this.disconnected = new SocketDisconnected();
        this.message = new MessageReceived();
        this.ready = new SocketHandlerReady();

    }

    private void HandleConnection() {
        if (sock == null) {
            Disconnect();
            return;
        }

        try {
            this.hostName = sock.getInetAddress().getCanonicalHostName();
            out = sock.getOutputStream();

            if (out == null) {
                Disconnect();
                return;
            }

            ready.executeEvent(new SocketHandlerReadyEvent(this, this));
            connected.executeEvent(new SocketConnectedEvent(this, this, id));

            startReading();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SendMessage(String message) {
        if (sock.isConnected() && !sock.isClosed())
            writeToStream(message);
    }

    private void startReading() {
        if (!sock.isConnected() || sock.isClosed()) {
            Disconnect();
            return;
        }

        byte[] messageByte = new byte[1000];
        String messageString;

        try {
            DataInputStream inputStream = new DataInputStream(sock.getInputStream());

            messageByte[0] = inputStream.readByte();
            messageByte[1] = inputStream.readByte();
            ByteBuffer byteBuffer = ByteBuffer.wrap(messageByte, 0, 2);

            int bytesToRead = byteBuffer.getShort();
            if (bytesToRead > 0) {
                inputStream.readFully(messageByte, 0, bytesToRead);
                messageString = new String(messageByte, 0, bytesToRead);
                message.executeEvent(new MessageReceivedEvent(this, id, messageString));
            }

            startReading();
        } catch (SocketException e) {
            Disconnect();
        } catch (Exception ignored) {
        }
    }

    private void writeToStream(String message) {

        if (!sock.isConnected() || sock.isClosed() || out == null)
            return;

        byte[] sizeinfo = new byte[4];
        byte[] data = message.getBytes();

        ByteBuffer bb = ByteBuffer.allocate(sizeinfo.length);
        bb.putInt(message.getBytes().length);

        try {

            out.write(bb.array());
            out.write(data);
            out.flush();

        } catch (Exception ex) {
            Disconnect();
        }

    }

    public void Disconnect() {

        try {
            sock.shutdownInput();
            sock.shutdownOutput();

            sock.close();

            disconnected.executeEvent(new SocketDisconnectedEvent(this, id));

        } catch (Exception e) {
//            e.printStackTrace();
        }

    }

    public void setSocket(Socket sock) {
        this.sock = sock;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public SocketConnected getConnected() {
        return connected;
    }

    public SocketDisconnected getDisconnected() {
        return disconnected;
    }

    public MessageReceived getMessage() {
        return message;
    }

    public Socket getSocket() {
        return sock;
    }

    public SocketHandlerReady getReady() {
        return ready;
    }

    public void run() {

        if (this.sock == null)
            return;

        HandleConnection();

    }
}
