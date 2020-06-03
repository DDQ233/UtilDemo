package pers.demo.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * @Description TO-DO
 * @Author DDQ
 * @Date 2020/6/3 16:58
 */
public class MqttUtilPublishTest {
    public static void main(String[] args) throws MqttException {
        String host = "mq.tongxinmao.com";
        int port = 18831;
        String clientId = "Client ID";
        String username = "TEST";
        String password = "TEST";
        String topic = "/public";
        int qos = 1;

        MqttUtil mqttUtil = new MqttUtil();
        mqttUtil.bindOptions(host, port, clientId, username, password);
        mqttUtil.connect();
        mqttUtil.setPushCallback(new PushCallback());
        mqttUtil.setPublishTopic(topic);
        mqttUtil.setPublishMessage("Message Message Message Message", qos, true);
        mqttUtil.setPublishMessage("Message \n Message \n Message \n Message", qos, true);

        mqttUtil.disconnect();
    }
}
