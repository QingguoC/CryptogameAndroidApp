package edu.gatech.seclass.sdpcryptogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AddNewCryptogram extends AppCompatActivity {
    private GridLayout ciphers;
    private static String phrase=null;
    private static String encodePhrase=null;
    private static boolean validPhrase;
    private Button submitNewCryptoBtn;
    private Button addCipherBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_cryptogram);
        setCipherGrid();
        addCiphers();

        submitNewCryptoBtn=(Button) findViewById(R.id.submit_new_crypto);
        submitNewCryptoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validPhrase && phrase!=null &&encodePhrase!=null){
                    Administrator admin=Administrator.getInstance();
                    String message=admin.addNewCryptogram(AddNewCryptogram.this,encodePhrase,phrase);
                    Toast.makeText(AddNewCryptogram.this,message,Toast.LENGTH_LONG).show();
                    phrase=null;
                    encodePhrase=null;
                    validPhrase=false;
                    if (message.charAt(0)=='T'){
                        submitNewCryptoBtn.setEnabled(false);
                        addCipherBtn.setEnabled(false);
                    //finish();
                    //startActivity(getIntent());
                    }
                }else{
                    Toast.makeText(AddNewCryptogram.this,"Please provide valid Phrase.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void setCipherGrid(){
        ciphers=(GridLayout) findViewById(R.id.cipher_grid);
        int gridNCol=13;
        int gridNRow=4;
        ciphers.setColumnCount(gridNCol);
        ciphers.setRowCount(gridNRow);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);
        for (int r=0;r<gridNRow;r++){
            for (int c=0;c<gridNCol;c++){
                EditText et=new EditText(AddNewCryptogram.this);
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

                ciphers.addView(et,glp);


            }
        }

    }
    public void addCiphers(){
        ciphers=(GridLayout) findViewById(R.id.cipher_grid);
        addCipherBtn=(Button) findViewById(R.id.addCipher);
        addCipherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phraseET=(EditText) findViewById(R.id.phrase_new);
                phrase = phraseET.getText().toString();
                encodePhrase="";
                validPhrase = phrase.matches(".*[a-zA-Z]+.*");

                if (!validPhrase){
                    phraseET.setError("Provide at least one letter");

                }else {
                    phraseET.setError(null);

                    //Hashtable<Character,ArrayList<Integer>> replacePosition=new Hashtable<Character,ArrayList<Integer>>();
                    Hashtable<Character,Character> replaceTable=new Hashtable<Character,Character>();
                    for (int k=0;k<3;k+=2) {
                        for (int i = 0; i < 13; i++) {
                            EditText cipherET = (EditText) ciphers.getChildAt(13*k+i);
                            String cipher = cipherET.getText().toString();
                            boolean validcipher = cipher.matches(".*[a-zA-Z]+.*");
                            if (validcipher) {
                                char assignee = (char) (13*k/2+i + 'A');
                                char assigned = cipher.toUpperCase().charAt(0);
                                replaceTable.put(assignee,assigned);
                            }
                        }
                    }
                    for(int i=0; i<phrase.length();i++){
                        char curr=phrase.charAt(i);
                        char currCAP=(curr+"").toUpperCase().charAt(0);
                        if (replaceTable.containsKey(currCAP)){

                            if (curr-'A'>26)
                                encodePhrase+=(char)(replaceTable.get(currCAP)+32);
                            else
                                encodePhrase+=replaceTable.get(currCAP);
                        }else {
                            encodePhrase+=curr;
                        }

                    }

                    Toast.makeText(AddNewCryptogram.this,encodePhrase,Toast.LENGTH_LONG).show();
                    //Toast.makeText(AddNewCryptogram.this,encodePhrase,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
