package edu.gatech.seclass.sdpcryptogram;

import android.content.ContentValues;
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

import java.util.List;

public class AdminMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        String UserName = getIntent().getStringExtra("UserName");

        TextView userInfo = (TextView) findViewById(R.id.txtvUserName);
        userInfo.setText("Welcome: " + UserName);

        Button addnewplayer = (Button) findViewById(R.id.addnewplayer);
        Button reqMoreCrypto = (Button) findViewById(R.id.requestMoreCrypto);
        Button addNewCrypto = (Button) findViewById(R.id.addnewcrypto);

        Administrator admin = Administrator.getInstance();
        admin.username = UserName;

        try{
            SQLiteDatabase cryptoDB = SQLiteDatabase.openDatabase("/data/user/0/edu.gatech.seclass.sdpcryptogram/databases/CryptogramGame_DB.db", null, SQLiteDatabase.OPEN_READONLY);
            String query = "SELECT * FROM User_T Where UserName = '" + UserName + "'";
            Cursor crs = cryptoDB.rawQuery(query, null);

            if(crs.getCount() > 0) {
                crs.moveToFirst();
                admin.firstName = crs.getString(1);
                admin.lastName = crs.getString(2);
            }
            else
            {
                TextView errMsg = (TextView) findViewById(R.id.errMsg);
                errMsg.setText("DB Application Error - Please Contact Your Admin");
            }
            cryptoDB.close();
        }
        catch (SQLException e){
            TextView dbError = (TextView) findViewById(R.id.errMsg);
            dbError.setText("Application Error - Please Contact Your Admin");
        }

        addnewplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itt=new Intent(AdminMain.this,AdminAddNewPlayer.class);
                startActivity(itt);
            }
        });

        addNewCrypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itt=new Intent(AdminMain.this,AddNewCryptogram.class);
                startActivity(itt);
            }
        });

        reqMoreCrypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Administrator admin=Administrator.getInstance();
                admin.requestMoreCrypto(AdminMain.this);
            }
        });


    }
}
