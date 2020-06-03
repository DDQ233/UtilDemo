package pers.demo.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * @Description MqttUtil
 * @Author DDQ
 * @Date 2020/6/1 17:17
 */
public class MqttUtil {
    // MQTT Client
    private MqttClient mqttClient;
    // 连接 MQTT 服务器需要的设置
    private MqttConnectOptions mqttConnectOptions;
    // MQTT 服务器地址
    private String host;
    // MQTT 服务器端口
    private String port;
    // 客户端标识 ID
    private String clientId;
    // 连接 MQTT 服务器的用户名
    private String username;
    // 连接 MQTT 服务器的密码
    private String password;

    public MqttUtil() {

    }

    public MqttUtil(String host, String port, String clientId, String username, String password) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void bindOptions(String host, String port, String clientId, String username, String password) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public void connect() throws MqttException {

    }

    public boolean checkOptions() {
        if (host != null && port != null && clientId != null && username != null && password != null) {
            return true;
        } else {
            return false;
        }
    }

}