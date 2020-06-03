package pers.demo.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * - 必须实现 MqttCallback 的接口并实现对应的相关接口方法。
 * - 每个客户及标识都需要一个回调实例。
 * - 必须在回调类中实现三个方法
 * -- public void connectionLost(Throwable throwable)
 * -- public void messageArrived(String s, MqttMessage mqttMessage)
 * -- public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken)
 * - 由 MqttClient.connect 方法激活此回调
 */

/**
 * @Description 发布消息的回调类
 * @Author DDQ
 * @Date 2020/6/3 15:48
 */
public class PushCallbackExample implements MqttCallback {
    /**
     * 在断开连接时调用，连接丢失后在此方法进行重连
     *
     * @param throwable
     */
    public void connectionLost(Throwable throwable) {
        System.out.println("连接断开，可以做重连操作");
    }

    /**
     * Subscribe 后得到的消息执行此方法
     *
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        System.out.println("\n");
        System.out.println("> Topic : " + topic);
        System.out.println("> Qos : " + mqttMessage.getQos());
        System.out.println("> Message : " + new String(mqttMessage.getPayload()));
        System.out.println();
    }

    /**
     * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
     *
     * @param iMqttDeliveryToken
     */
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("> Delivery complete ----------> " + iMqttDeliveryToken.isComplete());
    }
}
