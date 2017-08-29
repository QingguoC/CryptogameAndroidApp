package edu.gatech.seclass.sdpcryptogram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewChosenCrypto extends AppCompatActivity {
    public RadioGroup groupTrials;
    public String cryptoID;
    //public List<Trial> allOpenTrials;
    public List<Trial> allTrialsOnOneCrypto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chosen_crypto);
        cryptoID = getIntent().getStringExtra("CryptogramID");
        String isSolved = getIntent().getStringExtra("isSolved");
        Player player=Player.getInstance();

        TextView chosenCryptoInfo = (TextView) findViewById(R.id.chosenCryptoTitle);
        if (player.allTrials!=null && player.allTrials.containsKey(cryptoID))
            chosenCryptoInfo.setText("Cryptogram (ID " + cryptoID+ ") is "+isSolved);
        else
            chosenCryptoInfo.setText("Cryptogram (ID " + cryptoID+ ") has no trial history.");

        GridLayout glo=(GridLayout) findViewById(R.id.viewTrialsGLO);

        groupTrials=new RadioGroup(this);
        Button replayBtn=(Button) findViewById(R.id.replayTrial);


        //allOpenTrials=player.OpenTrials(cryptoID);
        allTrialsOnOneCrypto=player.allTrials.get(cryptoID);
        if (allTrialsOnOneCrypto ==null||allTrialsOnOneCrypto.size()==0){
            replayBtn.setEnabled(false);
            Toast.makeText(this,"No Trial History on this Cryptogram.",Toast.LENGTH_LONG).show();
        }else{
            replayBtn.setEnabled(true);
            int k=1;
            for (Trial trial:allTrialsOnOneCrypto){

                RadioButton rbTrial=new RadioButton(this);
                if (trial.Submitted&&trial.Answer!=null&&trial.Answer.length()!=0){
                    rbTrial.setText(k+". Trial"+trial.TrialID+"-> "+trial.Answer);
                    rbTrial.setEnabled(false);
                }else{
                    rbTrial.setText(k+". Trial"+trial.TrialID+"-> "+"Not submitted");
                }
                /*
                if (trial.Solved){
                    rbTrial.setEnabled(false);
                }*/
                groupTrials.addView(rbTrial);
                k++;
            }
            glo.addView(groupTrials);
            replayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int selected_id=groupTrials.getCheckedRadioButtonId();


                    if (selected_id<=0){
                        Toast.makeText(ViewChosenCrypto.this,"Please select one unfinished trial.",Toast.LENGTH_LONG).show();
                    }else{

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
                        Player player=Player.getInstance();
                        player.currentTrial=allTrialsOnOneCrypto.get(chosenID);
                        player.currentTrialID=player.currentTrial.TrialID;

                        //Toast.makeText(ViewChosenCrypto.this,""+player.currentTrialID,Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ViewChosenCrypto.this, CryptoMain.class);
                        intent.putExtra("playOrReplay","replay");
                        startActivity(intent);

                    }
                }
            });
        }

    }
    @Override
    protected void onRestart(){
        super.onRestart();
        finish();
        Player player =Player.getInstance();
        player.fetchStateFromDB(this);
        startActivity(getIntent());
    }
}
