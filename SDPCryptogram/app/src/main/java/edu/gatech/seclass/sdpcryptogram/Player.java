package edu.gatech.seclass.sdpcryptogram;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import edu.gatech.seclass.utilities.ExternalWebService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;



public class Player extends User{
    public static Trial currentTrial = null;

    public static int numSolved;
    public static int numStarted;
    public static int numFailed;
    public static int currentTrialID;
    public static Hashtable<String,List> allTrials;
    public static Hashtable<String,int[]> cryptoStats;
    public static List<Rating> ratingList;


    private static Player player=null;
    private Player (){ }

    public static Player getInstance(){
        if (player==null){
            player=new Player();
        }
        return player;
    }
    public static void reset(){
        if (player!=null){
            player=null;
        }
    }

    public Cryptogram selectCrypto(Context context, String cryptoID){
        Cryptogram chosenCrypto=null;
        String crypto = "";

        try{
            String dbPath = context.getDatabasePath("CryptogramGame_DB.db").toString();
            SQLiteDatabase cryptoDB = context.openOrCreateDatabase(
                    dbPath, Context.MODE_PRIVATE, null);

            String query = "SELECT * FROM Cryptogram_T WHERE CryptogramID='" + cryptoID+"'";

            Cursor crs = cryptoDB.rawQuery(query, null);
            if (crs != null) {
                crs.moveToFirst();

                chosenCrypto = new Cryptogram(crs.getString(0), crs.getString(2),crs.getString(1));

            }

            cryptoDB.close();

        }
        catch (Exception e) {
            System.out.println(e);
        }
        //System.out.println(crypto);
        return chosenCrypto;

    }

    //This function return a list of Ratings from server.
    public static List<Rating> viewRating(Context context){
        ExternalWebService ews=ExternalWebService.getInstance();
        if (ews!=null){
            ews.updateRatingService(Player.username,Player.firstName,Player.lastName,
                    Player.numSolved,Player.numStarted,Player.numFailed);

            List<ExternalWebService.PlayerRating> ratings=ews.syncRatingService();
            ratingList=new ArrayList<Rating>();
            for(ExternalWebService.PlayerRating pr : ratings){
                String tempName=pr.getFirstname()+" "+pr.getLastname();
                int tempNumSolved=pr.getSolved();
                int tempNumStarted=pr.getStarted();
                int tempNumFailed=pr.getIncorrect();
                Rating tempRating= new Rating(tempName,tempNumSolved,tempNumStarted,tempNumFailed);
                ratingList.add(tempRating);
            }
        } else
            Toast.makeText(context,"ExternalWebService is not available, please try later.",Toast.LENGTH_LONG).show();
        return ratingList;
    }

    // This function returns all openTrials across all cryptograms by a current Player
    // I modified it to return all openTrials for one Cryptogram.
    public List OpenTrials(String cryptoID)
    {
        List<Trial> allOpenTrials = new ArrayList<Trial>();
        /*
        if (allTrials != null) {
            Set<String> keys = allTrials.keySet();
            for (String key : keys) {
                List trials =  allTrials.get(key);
                Trial t = null;
                for(Iterator it = trials.iterator(); it.hasNext();) {
                    t = (Trial) it.next();
                    if (t.Submitted == false && t.CryptoID==cryptoID)
                        allOpenTrials.add(t);
                }
            }
        }*/
        if (allTrials !=null && allTrials.size()>0){
            List<Trial> allTrialsOnOne=allTrials.get(cryptoID);
            if (allTrialsOnOne!=null){
                for(Trial trial : allTrialsOnOne ){
                    if (!trial.Submitted){
                        allOpenTrials.add(trial);
                    }
                }
            }
        }
        return null;
    }

    public boolean getCryptogramSolutionStatus(String cryptoID)
    {
        boolean bVal = false;
        //List<Trial> cryptoTrials=this.OpenTrials(cryptoID);
        Player player=Player.getInstance();
        List<Trial> cryptoTrials=player.allTrials.get(cryptoID);
        if (cryptoTrials!=null){
            for(Trial t : cryptoTrials){

                if (t.Solved){
                    return true;
                }
            }
        }
        /*
        if (allTrials != null && allTrials.size()!=0) {
            List trials =  allTrials.get(cryptoID);
            Trial t = null;
            for(Iterator it = trials.iterator(); it.hasNext();) {
                t = (Trial) it.next();
                if (t.Solved == true)
                    bVal = true;
            }
        }*/
        return bVal;
    }

    public int getCryptogramIncorrectTrials(String cryptoID)
    {
        int bVal = 0;
        Player player=Player.getInstance();
        List<Trial> cryptoTrials=player.allTrials.get(cryptoID);
        //List<Trial> cryptoTrials=this.OpenTrials(cryptoID);
        if (cryptoTrials!=null){
            for(Trial t : cryptoTrials){

                if (t.Submitted&& !t.Solved){
                    bVal+=1;
                }
            }
        }
        /*
        if (allTrials != null && allTrials.size()!=0) {
            List trials =  allTrials.get(cryptoID);
            Trial t = null;
            for(Iterator it = trials.iterator(); it.hasNext();) {
                t = (Trial) it.next();
                if (t.Submitted == true && t.Solved == false)
                    bVal += 1;
            }
        }*/
        return bVal;
    }

    // Call this function to populate the state of player from DB: TO CONSIDER (Centralization of DB fetch functionality across application. Brain not functioning now)
    public void fetchStateFromDB(Context context)
    {
        try{
            String dbPath = context.getDatabasePath("CryptogramGame_DB.db").toString();
            SQLiteDatabase cryptoDB = context.openOrCreateDatabase(
                    dbPath, Context.MODE_PRIVATE, null);
            Player player=Player.getInstance();
            String query1 = "SELECT * FROM Trail_T Where UserName = '" + player.username + "'";
            Cursor crs1 = cryptoDB.rawQuery(query1, null);
            player.allTrials=new Hashtable<String,List>();
            if(crs1.getCount() > 0) {
                crs1.moveToFirst();
                do{
                    int trialID=crs1.getInt(0);
                    String cryptoID=crs1.getString(1);
                    String answer=crs1.getString(3);
                    int isSubmitted=crs1.getInt(4);
                    int isSolved=crs1.getInt(5);
                    String assignees=crs1.getString(6);
                    String assigneds=crs1.getString(7);

                    Trial t= new Trial(trialID,cryptoID,isSubmitted==1,isSolved==1,answer,assignees,assigneds);
                    if (player.allTrials.containsKey(cryptoID)){
                        player.allTrials.get(cryptoID).add(t);
                    } else{
                        ArrayList<Trial> trialL=new ArrayList<Trial>();
                        trialL.add(t);
                        player.allTrials.put(cryptoID,trialL);
                    }

                } while (crs1.moveToNext());
            }
            //Toast.makeText(this,""+player.allTrials.size(),Toast.LENGTH_LONG).show();
            cryptoDB.close();

        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // This call is mostly redundant due to our implementation but including it to match with our class design
    public boolean isAdmin()
    {
        return false;
    }

    // This function can be called to persist the current trail the player is working on before he swiches on to a different trial
    public void saveTrial(Context context)
    {
        try{
            Player player=Player.getInstance();
            SQLiteDatabase cryptoDB= context.openOrCreateDatabase( "CryptogramGame_DB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            //String[] args = new String[]{"CrypotgramID", "UserName", "Answer", "isSolved", "isSubmitted"};
            ContentValues trial = new ContentValues();
            trial.put("CrypotgramID",player.currentTrial.CryptoID);
            trial.put("UserName",player.username);
            trial.put("Answer",player.currentTrial.Answer);
            trial.put("isSolved",player.currentTrial.Solved?1:0);
            trial.put("isSubmitted",player.currentTrial.Submitted?1:0);
            trial.put("Assignee",player.currentTrial.Assignee);
            trial.put("Assigned",player.currentTrial.Assigned);
            cryptoDB.update(MainActivity.TRAIL_TABLE,trial,"TrailID=?",new String[]{""+currentTrial.TrialID});
            cryptoDB.close();

        } catch (Exception e){
            System.out.println(e.toString());
        }
    }
    public void savePlayerInfo(Context context)
    {
        //UserName VARCHAR PRIMARY KEY, " + "FirstName VARCHAR, " + "LastName VARCHAR," + "IsAdmin INT, " + "NumberStarted INT, " + "NumberSolved INT, " + "NumberFailed INT, " + "TrailID INT, " +  "FOREIGN KEY(TrailID) REFERENCES Trail_T(TrailID)
        try{
            Player player=Player.getInstance();
            //SQLiteDatabase cryptoDB= context.openOrCreateDatabase( "CryptogramGame_DB.db", SQLiteDatabase.OPEN_READWRITE, null);
            SQLiteDatabase cryptoDB = SQLiteDatabase.openDatabase("/data/user/0/edu.gatech.seclass.sdpcryptogram/databases/CryptogramGame_DB.db", null, SQLiteDatabase.OPEN_READWRITE);
            ContentValues newInfo = new ContentValues();
            newInfo.put("NumberStarted",player.numStarted);
            newInfo.put("NumberSolved",player.numSolved);
            newInfo.put("NumberFailed",player.numFailed);

            //cryptoDB.update("User_T",newInfo,"UserName="+player.username,null);
            cryptoDB.update("User_T",newInfo,"UserName=?",new String[]{player.username});
            cryptoDB.close();



        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static boolean submit(Context context,String Answer, String Solution){
        try{
            Player player = Player.getInstance();
            if (Answer.equals(Solution)) {
                if (!player.getCryptogramSolutionStatus(player.currentTrial.CryptoID))
                    player.numSolved += 1;
                player.currentTrial.Solved = true;
                player.currentTrial.Submitted = true;


                Toast.makeText(context,"Congrats! You made it!",Toast.LENGTH_LONG).show();

            }else{
                player.currentTrial.Submitted=true;
                player.currentTrial.Solved=false;
                player.saveTrial(context);
                player.numFailed+=1;
                Toast.makeText(context,"Nice try. You are getting close. Start a new trial on this game later if you want.",Toast.LENGTH_LONG).show();
            }
            player.saveTrial(context);
            player.savePlayerInfo(context);

            return true;
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return false;
    }

    public static void assign(Context context,String Answer,String Assignee, String Assigned){

        Player player=Player.getInstance();
        if (player.currentTrial!=null){
            player.currentTrial.Answer=Answer;
            player.currentTrial.Assignee=Assignee;
            player.currentTrial.Assigned=Assigned;
            player.saveTrial(context);
        }
    }
    public static void reChoose(Context context){
        Intent itt=new Intent(context, ChooseCrypto.class);
        context.startActivity(itt);
    }
}
