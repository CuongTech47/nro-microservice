package com.ngocrong.network;

import com.ngocrong.config.AppConfig;
import com.ngocrong.consts.Cmd;
import com.ngocrong.server.DragonBall;
import com.ngocrong.server.Server;
import com.ngocrong.user.Char;
import com.ngocrong.user.User;
import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Session implements ISession{
    private static final Logger logger = Logger.getLogger(Session.class);
    private static final Lock lock = new ReentrantLock();

    private byte[] key;
    public Socket socket;
    public DataInputStream dis;
    public DataOutputStream dos;
    public int id;
    public IMessageHandler messageHandler;

    @Getter
    private IService service;
    protected boolean isConnected, isLogin;
    private byte curR, curW;
    private final Sender sender;
    private Thread collectorThread;
    protected Thread sendThread;
    protected String version;
    protected byte zoomLevel;
    protected int width;
    protected int height;
    protected int device; // 0-PC, 1- APK, 2-IOS
    public User user;
    public Char _char;
    private boolean isSetClientInfo;
    public boolean isEnter = false;
    public String deviceInfo;

    public String ip;

    public Session(Socket socket, String ip, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        this.ip = ip;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        setHandler(new MessageHandler(this));
        messageHandler.onConnectOK();
        setService(new Service(this));
        sendThread = new Thread(sender = new Sender());
        collectorThread = new Thread(new MessageCollector());
        collectorThread.start();
        Server.ips.put(ip, Server.ips.getOrDefault(ip, 0) + 1);
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {

    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void setService(IService service) {

    }

    @Override
    public void close() {

    }

    @Override
    public void disconnect() {

    }


    private class Sender implements Runnable {

        private final ArrayList<Message> sendingMessage;

        public Sender() {
            sendingMessage = new ArrayList<>();
        }

        public void addMessage(Message message) {
            sendingMessage.add(message);
        }

        @Override
        public void run() {
            try {
                while (isConnected()) {
                    while (sendingMessage.size() > 0) {
                        Message m = sendingMessage.get(0);
                        doSendMessage(m);
                        sendingMessage.remove(0);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    protected synchronized void doSendMessage(Message m) throws IOException {
        if (m == null) {
            return;
        }
        byte[] data = m.getData();
        byte b = m.getCommand();
        if (isConnected) {
            dos.writeByte(writeKey(b));
        } else {
            dos.writeByte(b);
        }
        if (data != null) {
            int size = data.length;
            if (isConnected) {
                if (isSpecialMessage(b)) {
                    byte e = (byte) ((size & 255) - 128);
                    byte f = (byte) ((size >> 8) - 128);
                    byte g = (byte) ((size >> 16) - 128);
                    dos.writeByte(writeKey(e));
                    dos.writeByte(writeKey(f));
                    dos.writeByte(writeKey(g));
                } else {
                    int num2 = writeKey((byte) (size >> 8));
                    dos.writeByte((byte) num2);
                    int num3 = writeKey((byte) (size & 255));
                    dos.writeByte((byte) num3);
                }
            } else {
                dos.writeByte(size & 256);
                dos.writeByte(size & 255);
            }
            if (isConnected) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = writeKey(data[i]);
                }
            }
            dos.write(data);
        }
        dos.flush();
        m.cleanup();
    }

    private byte writeKey(byte b) {
        byte b2 = curW;
        curW = (byte) (b2 + 1);
        byte result = (byte) ((key[(int) b2] & 255) ^ ((int) b & 255));
        if (curW >= key.length) {
            curW = (byte) (curW % key.length);
        }

        return result;
    }

    private static boolean isSpecialMessage(int command) {
        return command == Cmd.BACKGROUND_TEMPLATE || command == Cmd.GET_EFFDATA || command == Cmd.REQUEST_NPCTEMPLATE
                || command == Cmd.REQUEST_ICON || command == Cmd.GET_IMAGE_SOURCE || command == Cmd.UPDATE_DATA
                || command == Cmd.GET_IMG_BY_NAME;
    }

    public void generateKey(int size) {
        this.key = new byte[size];

        // Sử dụng SecureRandom để tạo các byte ngẫu nhiên mã hóa
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(this.key); // Điền các byte ngẫu nhiên vào mảng `key`

        logger.debug("Generated secure key: " + Arrays.toString(this.key));
    }


    class MessageCollector implements Runnable {

        @Override
        public void run() {
            while (!socket.isClosed() && dis != null) {
                try {
                    Message message = readMessage();
                    try {
                        if (message != null) {
                            if (message.getCommand() == Cmd.GET_SESSION_ID) {
                                generateKey(10);
                                sendKey();
                            } else {
                                messageHandler.onMessage(message);
                            }
                        } else {
                            break;
                        }
                    } finally {
                        message.cleanup();
                    }
                } catch (Exception e) {
                    break;
                }
            }
            close();
        }

        private Message readMessage() throws IOException {
            // read message command
            byte cmd = dis.readByte();
            if (isConnected) {
                cmd = readKey(cmd);
            }
            // read size of data
            int size;
            if (isConnected) {
                byte b1 = dis.readByte();
                byte b2 = dis.readByte();
                size = (readKey(b1) & 0xff) << 8 | readKey(b2) & 0xff;
            } else {
                size = dis.readUnsignedShort();
            }
            byte data[] = new byte[size];
            int len = 0;
            int byteRead = 0;
            while (len != -1 && byteRead < size) {
                len = dis.read(data, byteRead, size - byteRead);
                if (len > 0) {
                    byteRead += len;
                }
            }
            if (isConnected) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = readKey(data[i]);
                }
            }
            Message msg = new Message(cmd, data);

            return msg;

        }
    }

    private void sendKey() throws IOException {
        if (isConnected) {
            return;
        }
        Server server = DragonBall.getInstance().getServer();
        AppConfig config = server.getConfig();
        Message ms = new Message(Cmd.GET_SESSION_ID);
        DataOutputStream ds = ms.writer();
        ds.writeByte(key.length);
        ds.writeByte(key[0]);
        for (int i = 1; i < key.length; i++) {
            ds.writeByte(key[i] ^ key[i - 1]);
        }
        ds.writeUTF(config.getHost());
        ds.writeInt(config.getPort());
        ds.writeBoolean(config.isRedirect());
        ds.flush();
        doSendMessage(ms);
        ms.cleanup();
        isConnected = true;
        sendThread.start();
        messageHandler.setService(service);
    }

    private byte readKey(byte b) {
        byte b2 = curR;
        curR = (byte) (b2 + 1);
        byte result = (byte) ((key[(int) b2] & 255) ^ ((int) b & 255));
        if (curR >= key.length) {
            curR = (byte) (curR % key.length);
        }

        return result;
    }
}
