package com.cryptite.lite;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.rabbitmq.client.*;

import java.io.IOException;
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
        heartbeat = new Heartbeat(plugin.serverName);
        connect();
    }

    public void start() {
        plugin.scheduler.runTaskTimerAsynchronously(plugin, this::heartbeat, 100, 40);
    }

    private void heartbeat() {
        try {
            publish(HEARTBEAT_TOPIC, heartbeat);
        } catch (Exception e) {
            LokaLite.log.info("[Network] Exception on heartbeat?: " + e.getClass().getTypeName() + ": " + e.getMessage());
        }
    }

    private synchronized void connect() {
        if (!isOffline()) return;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            connection.addShutdownListener(this);

            Map<String, Object> args = new HashMap<>();
            args.put("x-message-ttl", 0);
            declareQueue(HEARTBEAT_TOPIC, args);
            subscribe(HEARTBEAT_TOPIC, Heartbeat.class, args, this::onHeartbeat);

            LokaLite.log.info("[Network] connected to " + host);
            closed = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean isOffline() {
        return connection == null || closed;
    }

    private synchronized void onHeartbeat(Heartbeat h) {
        if (!lastHeartbeats.containsKey(h.server)) {
            System.out.println("[Network] New heartbeat from: " + h.server);
        }
        if (h.playerList) plugin.status.updatePlayers();
        lastHeartbeats.put(h.server, LocalDateTime.now());
    }

    private String declareQueue(String topic, Map<String, Object> args) throws IOException {
        channel.exchangeDeclare(topic, "fanout");
        String queueName = plugin.serverName + "-" + topic;

        channel.queueDeclare(queueName, false, true, true, args);
        channel.queueBind(queueName, topic, "");
        return queueName;
    }

    public <T> void publish(String topic, T message) {
        send("", topic, message);
    }

    public <T> void send(String destination, String topic, T message) {
        sendMessage(destination, topic, message);
    }

    private <T> void sendMessage(String destination, String topic, T message) {
        if (!connection.isOpen()) {
            LokaLite.log.info("[Network] Connection is closed?");
            return;
        }

        String json = gson.toJson(message);
        byte[] bytes = json.getBytes();
        try {
            channel.basicPublish(topic, destination, null, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized <T> void subscribe(String topic, Class<T> type, java.util.function.Consumer<T> callback) {
        subscribe(topic, type, null, callback);
    }

    private synchronized <T> void subscribe(String topic, Class<T> type, Map<String, Object> args, java.util.function.Consumer<T> callback) {
        try {
            String queue = declareQueue(topic, args);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) {
                    try {
                        T message = gson.fromJson(new String(body), type);
                        callback.accept(message);
                    } catch (JsonSyntaxException e) {
                        LokaLite.log.info("Bad Json: " + new String(body));
                        throw e;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            channel.basicConsume(queue, true, consumer);
            LokaLite.log.info("[Network] " + topic + " subscribed to queue: " + queue + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void close() {
        try {
            if (heartbeat != null) {
                heartbeat.online = false;
                publish(HEARTBEAT_TOPIC, heartbeat);
            }

            closed = true;

            if (channel != null && channel.isOpen()) channel.close();
            if (connection != null && connection.isOpen()) connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        LokaLite.log.info("[Network] Connection lost/closed");
    }

    @Override
    public void shutdownCompleted(ShutdownSignalException cause) {
        LokaLite.log.info("[Network] Connection closing: " + cause.getMessage());
        LokaLite.log.info("[Network] Connection closing reason: " + cause.getReason());
    }
}