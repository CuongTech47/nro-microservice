package com.ngocrong.network;

import org.apache.log4j.Logger;

import java.io.*;

public class Message {

    private byte command;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    public DataInputStream dis;

    private static final Logger logger = Logger.getLogger(Message.class);
    public Message(int command) {
        this((byte) command);
    }

    public Message(byte command) {
        this.command = command;
        os = new ByteArrayOutputStream();
        dos = new DataOutputStream(os);
    }

    public Message(byte command, byte[] data) {

        this.command = command;
        is = new ByteArrayInputStream(data);
        dis = new DataInputStream(is);
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(int cmd) {
        setCommand((byte) cmd);
    }

    public void setCommand(byte cmd) {
        command = cmd;
    }

    public byte[] getData() {
        return os.toByteArray();
    }

    public DataInputStream reader() {
        return dis;

    }

    public DataOutputStream writer() {
        return dos;
    }

    public void cleanup() {
        try {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
        } catch (IOException e) {
        }
    }

}
