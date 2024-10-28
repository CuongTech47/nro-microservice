package com.ngocrong.network;

import com.ngocrong.user.Char;

public interface IMessageHandler {
    public void setService(IService service);

    public void onMessage(Message message);

    public void setChar(Char _char);

    public void onConnectionFail();

    public void onDisconnected();

    public void onConnectOK();

    public void close();
}
