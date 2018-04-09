package com.ezhex1991.ezfloatingwindow;

public interface IFloatingWindowListener {
    void onConnected(int x, int y);

    void onClick();

    void onMove(int x, int y);

    void onDisconnected();
}
