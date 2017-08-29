package edu.gatech.seclass.sdpcryptogram;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.RadioGroup;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.Cursor;

import edu.gatech.seclass.utilities.ExternalWebService;


public class MainActivity extends AppCompatActivity {
    private RadioGroup rg;

    public static final String USER_TABLE = "User_T";
    public static final String CRYPTOGRAM_TABLE = "Cryptogram_T";
    public static final String TRAIL_TABLE = "Trail_T";
    public static final String ALLTRAILS_TABLE = "AllTrails_T";
    public static final String ASSIGNMENTS_TABLE = "Assignments_T";
    public static final String DATABASE_NAME = "CryptogramGame_DB.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            SQLiteDatabase cryptoDB = null;

            // Create database and associated tables
            cryptoDB = this.openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);

            // UNCOMMENT FIRST TIME RUNNING THE RECOMMENT AFTER
            // For debugging purposes uncomment this to drop a table
            // These drops are only temporary and are so the table changes take effect
/*
            cryptoDB.execSQL("DROP TABLE IF EXISTS " + CRYPTOGRAM_TABLE);
            cryptoDB.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            cryptoDB.execSQL("DROP TABLE IF EXISTS " + ALLTRAILS_TABLE);
            cryptoDB.execSQL("DROP TABLE IF EXISTS " + ASSIGNMENTS_TABLE);
            cryptoDB.execSQL("DROP TABLE IF EXISTS " + TRAIL_TABLE);
*/

            cryptoDB.execSQL("CREATE TABLE IF NOT EXISTS " + TRAIL_TABLE + "(TrailID INTEGER PRIMARY KEY, " + "CrypotgramID VARCHAR, " + "UserName VARCHAR, "+ "Answer VARCHAR," +"IsSubmitted INT,"+ "IsSolved INT,"+"Assignee VARCHAR,"+"Assigned VARCHAR);");
            cryptoDB.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + "(UserName VARCHAR PRIMARY KEY, " + "FirstName VARCHAR, " + "LastName VARCHAR," + "IsAdmin INT, " + "NumberStarted INT, " + "NumberSolved INT, " + "NumberFailed INT, " + "TrailID INT, " +  "FOREIGN KEY(TrailID) REFERENCES Trail_T(TrailID));");
            cryptoDB.execSQL("CREATE TABLE IF NOT EXISTS " + CRYPTOGRAM_TABLE + "(CryptogramID VARCHAR PRIMARY KEY, " + "Phrase VARCHAR, " + "EncodedPhrase VARCHAR);");
            //cryptoDB.execSQL("CREATE TABLE IF NOT EXISTS " + ALLTRAILS_TABLE + "(UserName VARCHAR, " + "CrypotgramID VARCHAR, " + "TrailID INT, " + "PRIMARY KEY (UserName, CrypotgramID, TrailID)" + ");");
            //cryptoDB.execSQL("CREATE TABLE IF NOT EXISTS " + ASSIGNMENTS_TABLE + "(TrailID INT, " + "Assignee VARCHAR, " + "Assigned VARCHAR, " + "FOREIGN KEY(TrailID) REFERENCES Trail_T(TrailID));");

            String adminUserName = "admin";
            String adminFirstName = "admin";
            String adminLastName = "user";
            String one = "1";
            String zero = "0";

            // Create first user - admin
            cryptoDB.execSQL("INSERT OR IGNORE INTO " + USER_TABLE + "(UserName,FirstName,LastName,IsAdmin,NumberStarted,NumberSolved,NumberFailed)" + "VALUES('" + adminUserName + "','" + adminFirstName + "','" + adminLastName + "'," + one + "," + zero + "," + zero + "," + zero + ");");

            /*
            // This user will only be used to test player log in
            String playerUserName = "fgarcia";
            String playerFirstName = "frankie";
            String playerLastName = "garcia";
            cryptoDB.execSQL("INSERT OR IGNORE INTO " + USER_TABLE + "(UserName,FirstName,LastName,IsAdmin,NumberStarted,NumberSolved,NumberFailed)" + "VALUES('" + playerUserName + "','" + playerFirstName + "','" + playerLastName + "'," + zero + "," + zero + "," + zero + "," + zero + ");");
            */

            cryptoDB.close();
        }
        catch (SQLException e)
        {
            TextView r = (TextView) findViewById(R.id.txtViewResult);
            r.setText("Database Error - Please Contact Your Admin");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void Login(View v){
        EditText userInput = (EditText)findViewById(R.id.editTxtUserName);
        final String userName = userInput.getText().toString();

        int isAdmin = 0;

        if (userName.equals("")) userInput.setError("Please Enter a Username");

        try{
            SQLiteDatabase cryptoDB = SQLiteDatabase.openDatabase("/data/user/0/edu.gatech.seclass.sdpcryptogram/databases/CryptogramGame_DB.db", null, SQLiteDatabase.OPEN_READONLY);
            String query = "SELECT * FROM User_T Where UserName = '" + userName + "'";
            Cursor crs = cryptoDB.rawQuery(query, null);

            if(crs.getCount() > 0) {
                crs.moveToFirst();

                isAdmin = crs.getInt(3);

                if (isAdmin == 0){
                    Intent intent = new Intent(MainActivity.this, PlayerMain.class);
                    intent.putExtra("UserName", userName);
                    startActivity(intent);
                }
                else if (isAdmin == 1){
                    Intent intent = new Intent(MainActivity.this, AdminMain.class);
                    intent.putExtra("UserName", userName);
                    startActivity(intent);
                }
                else {
                    TextView dbError = (TextView) findViewById(R.id.txtViewResult);
                    dbError.setText("Application Error - Please Contact Your Admin");
                }

                //empName = cursor.getString(cursor.getColumnIndex("EmployeeName"));
            }else {
                userInput.setError("Username does not exist.");
            }
            cryptoDB.close();
        }
        catch (SQLException e){
            TextView dbError = (TextView) findViewById(R.id.txtViewResult);
            dbError.setText("Application Error - Please Contact Your Admin");
        }

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


}
