package edu.gatech.seclass.sdpcryptogram;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class PlayerMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        String UserName;
        Player player=Player.getInstance();
        UserName = getIntent().getStringExtra("UserName");
        if (UserName!=null)
            player.username = UserName;
        else if (player.username!=null)
            UserName=player.username;
        TextView userInfo = (TextView) findViewById(R.id.txtvUserName);
        userInfo.setText("Welcome: " + UserName);
        try {
            SQLiteDatabase cryptoDB = SQLiteDatabase.openDatabase("/data/user/0/edu.gatech.seclass.sdpcryptogram/databases/CryptogramGame_DB.db", null, SQLiteDatabase.OPEN_READONLY);
            String query = "SELECT * FROM User_T Where UserName = '" + UserName + "'";
            Cursor crs = cryptoDB.rawQuery(query, null);

            if (crs.getCount() > 0) {
                crs.moveToFirst();
                player.firstName = crs.getString(1);
                player.lastName = crs.getString(2);
                player.numStarted = crs.getInt(4);
                player.numSolved = crs.getInt(5);
                player.numFailed = crs.getInt(6);
                //player.currentTrialID = crs.getInt(7);
            } else {
                TextView errMsg = (TextView) findViewById(R.id.errMsg);
                errMsg.setText("DB Application Error - Please Contact Your Admin");
            }

            player.fetchStateFromDB(this);
        } catch (SQLException e) {
            TextView dbError = (TextView) findViewById(R.id.errMsg);
            dbError.setText("Application Error - Please Contact Your Admin");
        }

        Button play= (Button) findViewById(R.id.play);
        Button viewRating = (Button) findViewById(R.id.viewRating);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itt = new Intent(PlayerMain.this,ChooseCrypto.class);
                startActivity(itt);

            }
        });

        viewRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itt = new Intent(PlayerMain.this,Ratings.class);
                startActivity(itt);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
