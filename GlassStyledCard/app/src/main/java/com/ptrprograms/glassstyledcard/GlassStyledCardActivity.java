package com.ptrprograms.glassstyledcard;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.glass.app.Card;

/**
 * Created by paulruiz on 8/27/14.
 */
public class GlassStyledCardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Card card = new Card(this);
        card.setText("5 Basic Human Emotions");
        card.setFootnote("how are you feeling?");
        card.addImage(R.drawable.ic_launcher);
        card.addImage(R.drawable.ic_launcher);
        card.addImage(R.drawable.ic_launcher);
        card.addImage(R.drawable.ic_launcher);
        card.addImage(R.drawable.ic_launcher);
        card.setImageLayout(Card.ImageLayout.LEFT);
        setContentView(card.getView());
    }
}
