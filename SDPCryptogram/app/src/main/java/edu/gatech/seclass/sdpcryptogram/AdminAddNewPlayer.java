package edu.gatech.seclass.sdpcryptogram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminAddNewPlayer extends AppCompatActivity {
    private EditText fn,ln,un;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_player);
        Button addNPBtn = (Button) findViewById(R.id.addPlayerInfo);
        addNPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fn=(EditText) findViewById(R.id.firstname_new);
                ln=(EditText) findViewById(R.id.lastname_new);
                un=(EditText) findViewById(R.id.usrname_new);
                String usrname=un.getText().toString();
                String firstname=fn.getText().toString();
                String lastname = ln.getText().toString();
                boolean validUsrname=usrname.matches(".*[a-zA-Z]+.*");
                boolean validfname=firstname.matches(".*[a-zA-Z]+.*");
                boolean validlname=lastname.matches(".*[a-zA-Z]+.*");
                if (validUsrname && validfname && validlname){
                    Administrator admin = Administrator.getInstance();
                    admin.addNewPlayer(AdminAddNewPlayer.this, usrname, firstname, lastname);

                } else {
                    String message="Please provide valid information.\nAt least one letter in username, first name and last name";
                    Toast.makeText(AdminAddNewPlayer.this,message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
