package edu.gatech.seclass.sdpcryptogram;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChooseCrypto extends AppCompatActivity {

    public RadioGroup group;
    public ArrayList<String> cryptoIDs;
    public ArrayList<String> cryptoIsSolved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_crypto);
        Button playBtn = (Button) findViewById(R.id.startGame);
        Button viewChosenBtn = (Button) findViewById(R.id.viewChosen);
        group = (RadioGroup) findViewById(R.id.cryptoGroup);
        cryptoIDs=new ArrayList<String>();
        cryptoIsSolved=new ArrayList<String>();
        //Display lists of Cryptograms
        try {
            Player player=Player.getInstance();
            //player.fetchStateFromDB(this);
            String dbPath = getDatabasePath("CryptogramGame_DB.db").toString();

            SQLiteDatabase cryptoDB = SQLiteDatabase.openDatabase(
                    dbPath, null, SQLiteDatabase.OPEN_READONLY);
            String query = "SELECT * FROM " + MainActivity.CRYPTOGRAM_TABLE;
            Cursor crs = cryptoDB.rawQuery(query, null);

            if(crs.moveToFirst()){
                int k=1;
                do{
                    RadioButton rb = new RadioButton(this);

                    String cryptoID=crs.getString(0);
                    boolean isSolved=player.getCryptogramSolutionStatus(cryptoID);
                    int numFailed=player.getCryptogramIncorrectTrials(cryptoID);
                    rb.setText(k+". Cipher: ID "+cryptoID+"; isSovled: "+isSolved+"; numFailed: "+numFailed);
                    cryptoIDs.add(cryptoID);

                    if (isSolved)
                        cryptoIsSolved.add("completed");
                    else
                        cryptoIsSolved.add("in progress");
                    group.addView(rb);
                    k++;
                }while(crs.moveToNext());
            }



        }
        catch (SQLException e){
            Toast.makeText(this,"Choose Crypto Error - Please Contact Your Admin",Toast.LENGTH_LONG).show();
        }
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //group = (RadioGroup) findViewById(R.id.cryptoGroup);
                int selected_id= group.getCheckedRadioButtonId();

                if (selected_id<=0){
                    Toast.makeText(ChooseCrypto.this,"Please select one cryptogram.",Toast.LENGTH_LONG).show();
                }else {
                    RadioButton rb=(RadioButton) findViewById(selected_id);
                    String rbMessage=rb.getText().toString();
                    int chosenID=0;
                    for (int i=0;i<rbMessage.length();i++){
                        char curr=rbMessage.charAt(i);
                        if (!Character.isDigit(curr)){
                            break;
                        }
                        chosenID=chosenID*10+(curr-'0');
                    }
                    chosenID-=1;
                    Intent itt = new Intent(ChooseCrypto.this, CryptoMain.class);
                    itt.putExtra("CryptogramID", cryptoIDs.get(chosenID));
                    itt.putExtra("playOrReplay","firstPlay");
                    startActivity(itt);
                }
            }
        });
        viewChosenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                group = (RadioGroup) findViewById(R.id.cryptoGroup);
                int selected_id= group.getCheckedRadioButtonId();
                if (selected_id<=0){
                    Toast.makeText(ChooseCrypto.this,"Please select one cryptogram.",Toast.LENGTH_LONG).show();
                }else{
                    int chosenID=0;
                    RadioButton rb=(RadioButton) findViewById(selected_id);

                    String rbMessage=rb.getText().toString();
                    for (int i=0;i<rbMessage.length();i++){
                        char curr=rbMessage.charAt(i);
                        if (!Character.isDigit(curr)){
                            break;
                        }
                        chosenID=chosenID*10+(curr-'0');
                    }
                    chosenID-=1;

                    Intent intent = new Intent(ChooseCrypto.this, ViewChosenCrypto.class);
                    intent.putExtra("CryptogramID", cryptoIDs.get(chosenID));
                    intent.putExtra("isSolved",cryptoIsSolved.get(chosenID));
                    startActivity(intent);
                    //Toast.makeText(ChooseCrypto.this,chosenID,Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        finish();
        Player player =Player.getInstance();
        player.fetchStateFromDB(this);
        startActivity(getIntent());
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,PlayerMain.class);
        startActivity(intent);
    }
}
