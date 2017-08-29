package edu.gatech.seclass.sdpcryptogram;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;



public class Ratings extends AppCompatActivity {

    GridLayout glo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);


        glo= (GridLayout) findViewById(R.id.ratingLayout);

        TextView tvName = new TextView(this);
        tvName.setText("NAME");

        glo.addView(tvName);

        TextView tvSolved = new TextView(this);
        tvSolved.setText("  Solved  ");

        glo.addView(tvSolved);

        TextView tvStarted = new TextView(this);
        tvStarted.setText("  Started  ");

        glo.addView(tvStarted);

        TextView tvFailed = new TextView(this);
        tvFailed.setText("  Failed  ");

        glo.addView(tvFailed);



        Player player=Player.getInstance();
        showRating(player.viewRating(this));

    }

    public void showRating(List<Rating> ratingList){
        Collections.sort(ratingList);
        int k=0;
        for(Rating r : ratingList){
            TextView tvName = new TextView(this);
            tvName.setText(r.name);
            glo.addView(tvName);

            TextView tvSolved = new TextView(this);
            tvSolved.setText("  "+r.numSolved);

            glo.addView(tvSolved);

            TextView tvStarted = new TextView(this);
            tvStarted.setText("  "+r.numStarted);

            glo.addView(tvStarted);

            TextView tvFailed = new TextView(this);
            tvFailed.setText("  "+r.numFailed);

            glo.addView(tvFailed);

            k++;
        }

    }




}
