package com.cryptite.lite;

import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class MQ implements ShutdownListener {
    private static final String HEARTBEAT_TOPIC = "heartbeat";
    private static final Duration FIVE_SECONDS = Duration.ofSeconds(5);
    private final LokaLite plugin;

    private static class Heartbeat {
        String server;
        public boolean playerList = false;
        boolean online = true;

        Heartbeat(String server) {
            this.server = server;
        }
    }

    private Gson gson = new Gson();
    private String host;
    private Connection connection;
    private Channel channel;
    private Heartbeat heartbeat;
    private Map<String, LocalDateTime> lastHeartbeats = new HashMap<>();
    private List<Runnable> subscriptions = new ArrayList<>();
    private boolean closed;

    public MQ(LokaLite plugin, String host) {
        this.plugin = plugin;
        this.host = host;
    }

    public void start() {
        plugin.scheduler.runTaskTimerAsynchronously(plugin, this::heartbeat, 100, 40);
    }

    private void heartbeat() {
        if (!isOffline()) {
            publish(HEARTBEAT_TOPIC, heartbeat);
        } else {
            connect();
        }
    }

    private synchronized void connect() {
        if (isOffline()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setRequestedHeartbeat(6);

            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                postConnectSetup();
                System.out.println("[Network] connected to " + host);
            } catch (IOException | TimeoutException e) {
                if (!(e instanceof ConnectException)) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized boolean isOffline() {
        return connection == null || !connection.isOpen() || !channel.isOpen();
    }

    private void postConnectSetup() throws IOException {
        connection.addShutdownListener(this);
        declareQueue(HEARTBEAT_TOPIC);
        heartbeat = new Heartbeat(plugin.serverName);
        subscribe(HEARTBEAT_TOPIC, Heartbeat.class, this::onHeartbeat);
        subscriptions.forEach(Runnable::run);
    }

    private synchronized void onHeartbeat(Heartbeat h) {
        if (!lastHeartbeats.containsKey(h.server)) {
            System.out.println("[Network] New heartbeat from: " + h.server);
        }
        if (h.playerList) plugin.status.updatePlayers();
        lastHeartbeats.put(h.server, LocalDateTime.now());
    }

    public boolean isOnline(String server) {
        LocalDateTime dt = lastHeartbeats.get(server);
        return dt != null && Duration.between(dt, LocalDateTime.now()).compareTo(FIVE_SECONDS) <= 0;
    }

    private String declareQueue(String topic) throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueDeclare(topic, false, false, false, null);
        channel.exchangeDeclare(topic, "fanout");
        channel.queueBind(queueName, topic, "");
        return queueName;
    }

    public <T> void publish(String topic, T message) {
        if (isOffline()) {
            System.out.println("[Network] Dropping send to topic " + topic + ", we're offline");
        } else {
            byte[] bytes = gson.toJson(message).getBytes();
            try {
                channel.basicPublish(topic, "", null, bytes);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                if (e.getMessage().contains("AlreadyClosedException")) {
                    close();
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T> void send(String destination, String topic, T message) {
        if (isOffline()) {
            System.out.println("[Network] Dropping send to topic " + topic + ", we're offline");
        } else {
            byte[] bytes = gson.toJson(message).getBytes();
            try {
                channel.basicPublish(topic, destination, null, bytes);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                if (e.getMessage().contains("AlreadyClosedException")) {
                    close();
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized <T> void subscribe(String topic, Class<T> type, java.util.function.Consumer<T> callback) {
        Runnable r = () -> {
            System.out.println("[Network] " + topic + " running");
            try {
                String queue = declareQueue(topic);

                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        T message = gson.fromJson(new String(body), type);
                        callback.accept(message);
                    }
                };

                channel.basicConsume(queue, true, consumer);
                System.out.println("[Network] " + topic + " now consuming.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        if (isOffline()) {
            subscriptions.add(r);
        } else {
            r.run();
        }
    }

    public void close() {
        try {
            heartbeat.online = false;
            publish(HEARTBEAT_TOPIC, heartbeat);
            closed = true;
            if (channel.isOpen()) channel.close();
            if (connection.isOpen()) connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println("[Network] Connection lost/closed");
    }

    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {
        channel = null;
        connection = null;

        if (!closed) {
            System.out.println("Connection closing due to " + cause.getMessage());
            connect();
        }
    }
}