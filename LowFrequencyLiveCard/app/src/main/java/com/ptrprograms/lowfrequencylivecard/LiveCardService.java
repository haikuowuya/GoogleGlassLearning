package com.ptrprograms.lowfrequencylivecard;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Date;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class LiveCardService extends Service {

    private static final String LIVE_CARD_TAG = "LiveCardService";


    private RemoteViews mRemoteViews;

    private LiveCard mLiveCard;

    int percent_done = 0;

    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (++percent_done > 100) {
                mRemoteViews.setTextViewText(R.id.text, "DONE!");
                mRemoteViews.setViewVisibility(R.id.progress, View.INVISIBLE);
                mLiveCard.setViews(mRemoteViews);
                return;
            }
            updateLowFrequencyLiveCard();
            mHandler.postDelayed(mUpdateProgressRunnable, 100);
        } };

    public void updateProgress() {
        mRemoteViews.setViewVisibility(R.id.progress, View.VISIBLE);
        mHandler.postDelayed(mUpdateProgressRunnable, 4000);
    }

    public void updateLowFrequencyLiveCard() {
        mRemoteViews.setTextViewText(R.id.text, ""+percent_done + "%");
        mRemoteViews.setProgressBar(R.id.progress, 100, percent_done, false);
        if (mLiveCard != null) mLiveCard.setViews(mRemoteViews);

        updateProgress();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createCard() {
        mLiveCard = new LiveCard( this, "card" );
        mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.live_card);
        updateLowFrequencyLiveCard();
        addMenuToLiveCard();
    }

    public void addMenuToLiveCard() {
        Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
        mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
        mLiveCard.publish(PublishMode.REVEAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( mLiveCard == null )
            createCard();

        return START_STICKY;
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
