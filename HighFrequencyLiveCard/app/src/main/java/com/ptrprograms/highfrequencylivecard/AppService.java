package com.ptrprograms.highfrequencylivecard;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class AppService extends Service {

    private static final String LIVE_CARD_TAG = "AppService";
    private LiveCard mLiveCard;
    LiveCard2DRenderer mCallbackCanvas;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null)
            createHighFrequencyLiveCardForCanvasDrawing();
        return START_STICKY;
    }

    private void createHighFrequencyLiveCardForCanvasDrawing() {
        mLiveCard = new LiveCard( this, LIVE_CARD_TAG );
        mCallbackCanvas = new LiveCard2DRenderer();
        mLiveCard.setDirectRenderingEnabled( true ).getSurfaceHolder().addCallback( mCallbackCanvas );
        addMenuToLiveCard();
    }


    public void addMenuToLiveCard() {
        Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
        mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
        mLiveCard.publish(PublishMode.REVEAL);
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }
}
