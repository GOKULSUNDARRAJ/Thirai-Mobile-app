package com.Saalai.Salaitvapp.App;

import android.app.Application;

import com.Saalai.Salaitvapp.PlayerManager;

public class MyApp extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();

        PlayerManager.stopNotification(); // 🔥 Stop notification

    }

}
