package edu.gatech.seclass.sdpcryptogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.InputFilter;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;


public class CryptoMain extends AppCompatActivity {
    private GridLayout glo;
    private String currCryptoID;
    private Cryptogram currCryptogram;
    private String playOrReplay;
    private boolean isValidDecipher=false;
    private String trialSol;
    private Button submitBtn;
    private Button decipherBtn;
    private String puzzle;
    private boolean isReplay;
    private Button rechooseBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = this.getWindow();
        w.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_main);

        playOrReplay=getIntent().getStringExtra("playOrReplay");
        glo=(GridLayout) findViewById(R.id.decipher_grid);
        submitBtn=(Button)findViewById(R.id.submitSolutionBtn);
        submitBtn.setEnabled(false);

        Player player=Player.getInstance();
        if (playOrReplay.equals("firstPlay")){
            isReplay=false;
            currCryptoID=getIntent().getStringExtra("CryptogramID");
            currCryptogram=player.selectCrypto(this,currCryptoID);

            ContentValues newTrial = new ContentValues();
            String dbPath = this.getDatabasePath("CryptogramGame_DB.db").toString();
            SQLiteDatabase cryptoDB = this.openOrCreateDatabase(
                    dbPath, Context.MODE_PRIVATE, null);
            newTrial.put("CrypotgramID",currCryptoID);
            newTrial.put("UserName",player.username);
            newTrial.put("IsSubmitted","0");
            newTrial.put("IsSolved","0");
            long retID = cryptoDB.insert("Trail_T", null, newTrial);
            Trial newT= new Trial((int)retID,currCryptoID,false,false);

            //player.currentTrialID=player.currentTrial.TrialID;

            if(player.allTrials.containsKey(currCryptoID)){
                player.allTrials.get(currCryptoID).add(newT);
            }else{
                ArrayList<Trial> newTrialList= new ArrayList<Trial>();
                newTrialList.add(newT);
                player.allTrials.put(currCryptoID,newTrialList);
                player.numStarted+=1;
                player.savePlayerInfo(this);
            }
            player.currentTrial=newT;
            player.currentTrialID=newT.TrialID;


            cryptoDB.close();


        }else{
            isReplay=true;
            currCryptoID=player.currentTrial.CryptoID;
            currCryptogram=player.selectCrypto(this,currCryptoID);
            //Toast.makeText(this,playOrReplay,Toast.LENGTH_LONG).show();
        }


        if (currCryptogram!=null) {
            TextView titleTV=(TextView) findViewById(R.id.cryptoMainTitle);
            titleTV.setText("Cryptogram ID: "+currCryptoID);
            puzzle=currCryptogram.encodePhrase;
            TextView showPuzzle=(TextView) findViewById(R.id.puzzleTV);
            showPuzzle.setText(puzzle);
            setDecipherGrid();
            decipher();
            submitSolution();
        }

        rechooseBtn=(Button) findViewById(R.id.rechooseBtn);
        rechooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player player=Player.getInstance();
                player.reChoose(CryptoMain.this);
            }
        });

    }

    public void setDecipherGrid(){
        String assignees="";
        String assigneds="";
        int gridNCol=13;
        int gridNRow=4;
        glo.setColumnCount(gridNCol);
        glo.setRowCount(gridNRow);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);
        Player player=Player.getInstance();
        if (isReplay&&player.currentTrial.Answer!=null){
            TextView showSolTV=(TextView) findViewById(R.id.solutionTV);
            showSolTV.setText(player.currentTrial.Answer);
            assignees=player.currentTrial.Assignee;
            assigneds=player.currentTrial.Assigned;
        }
        boolean validAssigns=true;
        if (isReplay)
            validAssigns=(assignees.length()==26)&&(assigneds.length()==26);
        for (int r=0;r<gridNRow;r++){
            for (int c=0;c<gridNCol;c++){
                EditText et=new EditText(CryptoMain.this);
                GridLayout.LayoutParams glp=new GridLayout.LayoutParams();
                glp.rowSpec=GridLayout.spec(r);
                glp.columnSpec=GridLayout.spec(c);
                et.setFilters(filterArray);
                if(r==1){
                    et.setEnabled(false);
                    et.setText((char)('A'+c)+"");
                } else if(r==3){
                    et.setEnabled(false);
                    et.setText((char)('A'+c+gridNCol)+"");
                }
                if (isReplay&&validAssigns){
                    if (r==0){
                        char assignee=assignees.charAt(c);
                        char assigned=assigneds.charAt(c);
                        if (assignee!=assigned)
                            et.setText(assigneds.charAt(c)+"");
                    }
                    if (r==2){
                        char assignee=assignees.charAt(13+c);
                        char assigned=assigneds.charAt(13+c);
                        if (assignee!=assigned)
                            et.setText(assigneds.charAt(13+c)+"");
                    }

                }

                glo.addView(et,glp);


            }
        }

    }

    public void decipher(){

        decipherBtn=(Button) findViewById(R.id.decodeBtn);
        decipherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView showSolTV=(TextView) findViewById(R.id.solutionTV);
                trialSol="";

                Hashtable<Character,Character> replaceTable=new Hashtable<Character,Character>();
                Hashtable<Character,Character> replaceTableRev=new Hashtable<Character,Character>();
                String assignees="";
                String assigneds="";
                isValidDecipher=true;
                outloop:
                for (int k=0;k<3;k+=2) {
                    for (int i = 0; i < 13; i++) {
                        EditText decipherET = (EditText) glo.getChildAt(13*k+i);
                        String decipher = decipherET.getText().toString();
                        boolean validDecipher = decipher.matches(".*[a-zA-Z]+.*");
                        if (validDecipher) {
                            char assignee = (char) (13*k/2+i + 'A');
                            char assigned = decipher.toUpperCase().charAt(0);
                            assignees+=assignee;
                            assigneds+=assigned;
                            replaceTable.put(assignee,assigned);
                            if (!replaceTableRev.containsKey(assigned))
                                replaceTableRev.put(assigned,assignee);
                            else {
                                isValidDecipher=false;
                                //Toast.makeText(CryptoMain.this,"Deciphers are not unique!!!",Toast.LENGTH_LONG).show();
                                //break outloop;
                            }
                        } else {

                            char assignee = (char) (13*k/2+i + 'A');
                            assignees+=assignee;
                            if (decipher==null ||decipher.length()==0){
                                char assigned = assignee;
                                assigneds+=assigned;
                            }else {
                                isValidDecipher=false;
                                char assigned = decipher.charAt(0);
                                assigneds+=assigned;
                            }
                        }
                    }
                }
                if (isValidDecipher){
                    submitBtn.setEnabled(true);
                    if (replaceTable==null||replaceTable.size()==0){
                        showSolTV.setText(puzzle);
                        Player player=Player.getInstance();
                        trialSol=puzzle;
                        player.assign(CryptoMain.this,puzzle,assignees,assigneds);
                    } else {
                        for(int i=0; i<puzzle.length();i++){
                            char curr=puzzle.charAt(i);
                            char currCAP=(curr+"").toUpperCase().charAt(0);
                            if (replaceTable.containsKey(currCAP)){

                                if (curr-'A'>26)
                                    trialSol+=(char)(replaceTable.get(currCAP)+32);
                                else
                                    trialSol+=replaceTable.get(currCAP);
                            }else {
                                trialSol+=curr;
                            }

                        }
                        showSolTV.setText(trialSol);
                        Player player=Player.getInstance();
                        player.assign(CryptoMain.this,trialSol,assignees,assigneds);
                        //Toast.makeText(CryptoMain.this,player.currentTrial.Answer,Toast.LENGTH_LONG).show();
                    }




                }else {
                    Toast.makeText(CryptoMain.this,"Deciphers are not valid!!!",Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    public void submitSolution(){


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isValidDecipher&&trialSol!=null && currCryptogram!=null) {
                    Player player = Player.getInstance();
                    player.submit(CryptoMain.this,trialSol,currCryptogram.solution);
                    submitBtn.setEnabled(false);
                    decipherBtn.setEnabled(false);
                }
                else {
                    Toast.makeText(CryptoMain.this,"Please make valid assignments before submit!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}