package pers.demo.serialport;

/**
 * @Description TO-Do
 * @Author DDQ
 * @Date 2020/6/4 10:50
 */
public class SerialPortUtilTest implements Operation {
    private static SerialPortUtil serialPortUtil = new SerialPortUtil();

    public static void main(String[] args) {
        serialPortUtil.scanPorts();
        serialPortUtil.openSerialPort();
    }

    public void operation() {
        System.out.println(serialPortUtil.readData());
    }
}
