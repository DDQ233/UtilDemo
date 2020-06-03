package pers.demo.mqtt;

import com.sun.istack.internal.NotNull;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @Description MqttUtil
 * @Author DDQ
 * @Date 2020/6/1 17:17
 */
public class MqttUtil {
    // MQTT Client
    private MqttClient mqttClient;
    // 连接 MQTT 服务器需要的设置
    // private MqttConnectOptions mqttConnectOptions;
    // 主题
    private MqttTopic mqttTopic;
    // MQTT 消息
    private MqttMessage mqttMessage;
    // MQTT 消息发送
    private MqttDeliveryToken mqttDeliveryToken;
    // 连接 MQTT 服务器的 URL
    private String url;
    // MQTT 服务器地址
    private String host;
    // MQTT 服务器端口
    private int port;
    // 客户端标识 ID
    private String clientId;
    // 连接 MQTT 服务器的用户名
    private String username;
    // 连接 MQTT 服务器的密码
    private String password;
    // 会话是否保持
    private boolean isCleanSession = false;
    // 是否自动重连
    private boolean isAutomaticReconnect = true;
    // 超时时间
    private int connectionTimeout = 10;
    // 会话超时时间
    private int keepAliveIntervalTime = 20;

    /**
     * 无参构造函数
     */
    public MqttUtil() {

    }

    /**
     * 有参构造函数
     *
     * @param host
     * @param port
     * @param clientId
     * @param username
     * @param password
     */
    public MqttUtil(@NotNull String host, int port, @NotNull String clientId, @NotNull String username, @NotNull String password) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.url = "tcp://" + host + ":" + port;
    }

    /**
     * 绑定连接 MQTT 服务器的参数
     *
     * @param host
     * @param port
     * @param clientId
     * @param username
     * @param password
     */
    public void bindOptions(@NotNull String host, int port, @NotNull String clientId, @NotNull String username, @NotNull String password) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.url = "tcp://" + host + ":" + port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCleanSession(boolean cleanSession) {
        isCleanSession = cleanSession;
    }

    public void setAutomaticReconnect(boolean automaticReconnect) {
        isAutomaticReconnect = automaticReconnect;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setKeepAliveIntervalTime(int keepAliveIntervalTime) {
        this.keepAliveIntervalTime = keepAliveIntervalTime;
    }

    /**
     * 连接 MQTT 服务器
     *
     * @throws MqttException
     */
    public void connect() throws MqttException {
        if (checkOptions()) {
            if (mqttClient == null) {
                mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
                MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                // 设置参数
                mqttConnectOptions.setCleanSession(isCleanSession);
                mqttConnectOptions.setUserName(username);
                mqttConnectOptions.setPassword(password.toCharArray());
                mqttConnectOptions.setConnectionTimeout(connectionTimeout);
                mqttConnectOptions.setKeepAliveInterval(keepAliveIntervalTime);
                mqttConnectOptions.setAutomaticReconnect(isAutomaticReconnect);
                // 连接
                mqttClient.connect(mqttConnectOptions);
            }
        }
    }

    /**
     * 断开连接
     *
     * @throws MqttException
     */
    public void disconnect() throws MqttException {
        if (mqttClient != null) {
            mqttClient.disconnect();
            mqttClient.close();
        }
    }

    /**
     * 根据已绑定的参数重新连接
     *
     * @throws MqttException
     */
    public void reconnect() throws MqttException {
        disconnect();
        connect();
    }

    /**
     * 设置消息处理回调函数
     *
     * @param mqttCallback
     */
    public void setPushCallback(MqttCallback mqttCallback) {
        if (mqttClient != null) {
            mqttClient.setCallback(mqttCallback);
        }
    }

    /**
     * 订阅单个主题
     *
     * @param topic
     * @param qos
     * @throws MqttException
     */
    public void subscribeTopic(String topic, int qos) throws MqttException {
        mqttClient.subscribe(topic, qos);
    }

    /**
     * 订阅多个主题
     *
     * @param topic
     * @param qos
     * @throws MqttException
     */
    public void subscribeTopic(String[] topic, int[] qos) throws MqttException {
        mqttClient.subscribe(topic, qos);
    }

    /**
     * 设置发布主题
     *
     * @param topic
     */
    public void setReleaseTopic(String topic) {
        if (mqttClient != null) {
            mqttTopic = mqttClient.getTopic(topic);
        }
    }

    /**
     * 设置发布消息
     *
     * @param msg
     * @param qos
     * @param isRetained
     */
    public void setReleaseMessage(String msg, int qos, boolean isRetained) {
        if (mqttClient != null && mqttTopic != null) {
            mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setRetained(isRetained);
            mqttMessage.setPayload(msg.getBytes());
        }
    }

    /**
     * 发布消息
     *
     * @throws MqttException
     */
    public void publish() throws MqttException {
        if (mqttClient != null && mqttTopic != null) {
            mqttDeliveryToken = mqttTopic.publish(mqttMessage);
            mqttDeliveryToken.waitForCompletion();
            System.out.println("Message id published --- " + mqttDeliveryToken.isComplete());
        }
    }

    /**
     * 参数检查
     *
     * @return
     */
    public boolean checkOptions() {
        this.url = "tcp://" + host + ":" + port;
        return clientId != null && username != null && password != null;
    }
}