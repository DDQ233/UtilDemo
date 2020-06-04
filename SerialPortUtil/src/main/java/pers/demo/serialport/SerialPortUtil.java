package pers.demo.serialport;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * @Description TO-DO
 * @Author DDQ
 * @Date 2020/6/1 18:09
 */
public class SerialPortUtil implements SerialPortEventListener {
    private Operation operation;
    private SerialPort serialPort;
    private CommPortIdentifier portIdentifier;
    private List<String> portList = new ArrayList<String>();
    private OutputStream outputStream;
    private InputStream inputStream;
    private int sendCount = 0;

    public SerialPortUtil(Operation operation) {
        this.operation = operation;
    }

    /**
     * 扫描本机所有 COM 端口
     *
     * @return
     */
    public boolean scanPorts() {
        Enumeration enumeration = CommPortIdentifier.getPortIdentifiers();
        while (enumeration.hasMoreElements()) {
            portIdentifier = (CommPortIdentifier) enumeration.nextElement();
            if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                String name = portIdentifier.getName();
                if (!portList.contains(name)) {
                    portList.add((name));
                }
            }
        }
        if (null == portList || portList.isEmpty()) {
            System.out.println("> x Failed to find usable serial port.");
            return false;
        }
        System.out.println("> √ Scan ports");
        return true;
    }

    /**
     * 打开串行端口
     *
     * @param portName   串口名称
     * @param baudRate   波特率
     * @param dataBits   数据位
     * @param stopBits   停止位
     * @param parityBits 奇偶检验位
     * @return
     */
    public boolean openSerialPort(String portName, int baudRate, int dataBits, int stopBits, int parityBits) {
        // 获取要打开的端口
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        } catch (NoSuchPortException e) {
            System.out.println("> x Failed to find " + portName + " serial port.");
            e.printStackTrace();
            return false;
        }
        // 打开端口
        try {
            serialPort = (SerialPort) portIdentifier.open("SerialPort", 2000);
        } catch (PortInUseException e) {
            System.out.println("> x " + portName + " is occupied.");
            e.printStackTrace();
            return false;
        }
        // 设置端口参数
        try {
            serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parityBits);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("> x Failed to set parameters of serial port.");
            e.printStackTrace();
            return false;
        }
        // 打开端口的 I/O 流管道
        try {
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("> x Failed to open I/O pipeline");
            e.printStackTrace();
            return false;
        }
        // 给端口添加监听器
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnBreakInterrupt(true);
        } catch (TooManyListenersException e) {
            System.out.println("> x Failed to add listener.");
            e.printStackTrace();
            return false;
        }
        // Enabled
        serialPort.notifyOnDataAvailable(true);
        return true;
    }

    /**
     * 发送数据
     *
     * @param message
     */
    public void sendData(String message) {
        if (serialPort != null) {
            try {
                sendCount++;
                outputStream.write(message.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                System.out.println("> x Failed to send message.");
                e.printStackTrace();
            }
        }
    }

    /**
     * 接收的字节数据转换成字符串数据
     *
     * @param bytes
     * @return
     */
    public String dataToString(byte[] bytes) {
        StringBuilder data = new StringBuilder();
        data.append(new String(bytes));
        return data.toString();
    }

    /**
     * 从串口读数据
     *
     * @return
     */
    public String readData() {
        byte[] bytes = null;
        if (inputStream != null) {
            try {
                inputStream = serialPort.getInputStream();
                // 获取 buffer 里的长度
                int buffLength = inputStream.available();
                while (buffLength != 0) {
                    // 初始化 byte 数组为 buffer 中的数据长度
                    bytes = new byte[buffLength];
                    inputStream.read(bytes);
                    buffLength = inputStream.available();
                }
            } catch (IOException e) {
                System.out.println("> x Failed to read data.");
                e.printStackTrace();
            }
        }
        return dataToString(bytes);
    }

    /**
     * 关闭串口工作流
     */
    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (serialPort != null) {
                serialPort.close();
            }
        } catch (IOException e) {
            System.out.println("> x Failed to close.");
            e.printStackTrace();
        }
    }

    /**
     * 获取已发送数据量
     *
     * @return
     */
    public int getSendCount() {
        return sendCount;
    }

    /**
     * 端口事件监听
     *
     * @param serialPortEvent
     */
    public void serialEvent(SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                // 自定义处理
                operation.operation();
        }
    }
}
