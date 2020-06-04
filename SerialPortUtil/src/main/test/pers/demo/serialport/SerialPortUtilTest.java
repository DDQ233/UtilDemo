package pers.demo.serialport;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import javax.swing.*;

/**
 * @Description 测试类
 * @Author DDQ
 * @Date 2020/6/4 10:50
 */
public class SerialPortUtilTest extends JFrame implements Operation {
    private SerialPortUtil serialPortUtil = new SerialPortUtil();

    public SerialPortUtilTest() {
        serialPortUtil.bindOptions(this, "COM1", 9600, 8, 1, 0, 0);
        serialPortUtil.scanPorts();
        serialPortUtil.openSerialPort();
        serialPortUtil.sendData("Send Data Send Data\n");
        setSize(200, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void operation() {
        System.out.println(serialPortUtil.readData());
    }

    public static void main(String[] args) {
        SerialPortUtilTest serialPortUtilTest = new SerialPortUtilTest();
    }

}
