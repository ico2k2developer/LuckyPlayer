package it.developing.ico2k2.luckyplayer.services;

import android.os.Bundle;

import androidx.annotation.Nullable;

public interface OnServiceMessageListener
{
    void onMessageReceived(int what,@Nullable Bundle extras);
}
