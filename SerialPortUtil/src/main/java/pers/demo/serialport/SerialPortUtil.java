package pers.demo.serialport;

import com.sun.istack.internal.NotNull;
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
    // 用户操作
    private Operation operation;
    // 串行端口
    private SerialPort serialPort;
    // 端口 ID
    private CommPortIdentifier portIdentifier;
    // 端口列表
    private List<String> portList = new ArrayList<String>();
    // 数据输出流
    private OutputStream outputStream;
    // 数据输入流
    private InputStream inputStream;
    // 发送的数据量
    private int sendCount = 0;

    private String portName = "COM1";
    private int baudRate = 115200;
    private int dataBits = SerialPort.DATABITS_8;
    private int stopBits = SerialPort.STOPBITS_1;
    private int parityBits = SerialPort.PARITY_NONE;
    private int flowControl = SerialPort.FLOWCONTROL_NONE;

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public void setParityBits(int parityBits) {
        this.parityBits = parityBits;
    }

    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
    }


    /**
     * 无参构造函数
     */
    public SerialPortUtil() {

    }

    /**
     * 构造的同时添加串口接收到数据后的用户自定义操作
     *
     * @param operation
     */
    public SerialPortUtil(Operation operation) {
        this.operation = operation;
    }

    /**
     * 构造的同时完成所有参数的绑定
     * @param operation
     * @param portName
     * @param baudRate
     * @param dataBits
     * @param stopBits
     * @param parityBits
     * @param flowControl
     */
    public SerialPortUtil(Operation operation, @NotNull String portName, int baudRate, int dataBits, int stopBits, int parityBits, int flowControl){
        this.operation = operation;
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parityBits = parityBits;
        this.flowControl = flowControl;
    }

    /**
     * 绑定连接串口的参数
     *
     * @param portName
     * @param baudRate
     * @param dataBits
     * @param stopBits
     * @param parityBits
     */
    public void bindOptions(@NotNull String portName, int baudRate, int dataBits, int stopBits, int parityBits, int flowControl) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parityBits = parityBits;
        this.flowControl = flowControl;
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
     * @return
     */
    public boolean openSerialPort() {
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
