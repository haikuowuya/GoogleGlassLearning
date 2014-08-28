package com.ptrprograms.glassstyledcard;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by paulruiz on 8/27/14.
 */
public class AppService extends Service {

    private void createGlassStyledCard() {
        Intent intent = new Intent(this, GlassStyledCardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        createGlassStyledCard();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
