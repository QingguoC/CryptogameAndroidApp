package edu.gatech.seclass.sdpcryptogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.gatech.seclass.utilities.ExternalWebService;



public class Administrator extends User{
    public static Administrator admin=null;
    private Administrator(){};
    public static Administrator getInstance(){
        if(admin==null){
            admin=new Administrator();
        }
        return admin;
    }
    public boolean isAdmin(){
        return true;
    }

    public static void addNewPlayer(){

    }
    //This method return a list of Cryptograms retrieved from server.
    public static void requestMoreCrypto(Context context){
        List<Cryptogram> cryptos= new ArrayList<Cryptogram>();
        ExternalWebService ews = ExternalWebService.getInstance();
        if (ews.syncCryptogramService()!=null) {
            for (String[] strings : ews.syncCryptogramService()) {
                Cryptogram crypto = new Cryptogram(strings[0], strings[1], strings[2]);
                cryptos.add(crypto);
            }
        }
        SQLiteDatabase cryptoDB= context.openOrCreateDatabase( "CryptogramGame_DB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

        long sf=0;
        long tempsf=-1;
        if (cryptos!=null) {
            for (Cryptogram cr : cryptos) {
                ContentValues crypto = new ContentValues();
                crypto.put("CryptogramID", cr.id);
                crypto.put("Phrase", cr.solution);
                crypto.put("EncodedPhrase", cr.encodePhrase);

                tempsf = cryptoDB.insert("Cryptogram_T", null, crypto);
                if (tempsf >= 0) {
                    sf++;
                }
            }
        }
        if (sf<=0){
            Toast.makeText(context,"There is no more new game on the server!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context,sf+" new cryptograms were just added into your phone.",Toast.LENGTH_LONG).show();
        }
        cryptoDB.close();
    }

    // This method let admin to add new local Player
    public static void addNewPlayer(Context context, String username, String firstname, String lastname){
        try{
            SQLiteDatabase cryptoDB= context.openOrCreateDatabase( "CryptogramGame_DB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            ContentValues newPlayer = new ContentValues();
            newPlayer.put("UserName",username);
            newPlayer.put("FirstName",firstname);
            newPlayer.put("LastName",lastname);
            newPlayer.put("IsAdmin","0");
            newPlayer.put("NumberStarted","0");
            newPlayer.put("NumberSolved","0");
            newPlayer.put("NumberFailed","0");

            long sf = cryptoDB.insert("User_T", null, newPlayer);
            if (sf<0){
                Toast.makeText(context,"Failed to add new player!!!\n Player already exists!!!",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context,username+"'s account has been created successfully.",Toast.LENGTH_LONG).show();
            }
            cryptoDB.close();

        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static String addNewCryptogram(Context context,String puzzle, String solution){
        String message="Service is failed. The new cryptogram is not valid.";
        String cryptoID="";
        try {
            ExternalWebService ews = ExternalWebService.getInstance();
            cryptoID = ews.addCryptogramService(puzzle, solution);

            SQLiteDatabase cryptoDB= context.openOrCreateDatabase( "CryptogramGame_DB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            ContentValues crypto = new ContentValues();
            crypto.put("CryptogramID",cryptoID);
            crypto.put("Phrase", solution);
            crypto.put("EncodedPhrase", puzzle);

            cryptoDB.insert("Cryptogram_T", null, crypto);
            cryptoDB.close();
            message="The new cryptogram successfully got an ID: "+cryptoID+", and saved locally.";

        } catch (Exception e){
            System.out.println(e.toString());
        }

        return message;

    }

}
