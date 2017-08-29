package edu.gatech.seclass.sdpcryptogram;

import java.util.Hashtable;

/**
 * Created by Rajan on 7/8/2017.
 */

public class Trial {
    public int TrialID;
    public String CryptoID;
    public boolean Solved;
    public boolean Submitted;
    public Hashtable<Character,Character> Assignments;
    public String Answer;
    //public String SubmittedSolution;
    public String Assignee;
    public String Assigned;

    // Default Constructor
    public Trial(int TrialID, String CryptoID)
    {
        this.TrialID = TrialID;
        this.CryptoID = CryptoID;
    }
    public Trial(int TrialID, String CryptoID, boolean Submmited, boolean Solved){
        this.TrialID=TrialID;
        this.CryptoID=CryptoID;
        //this.Answer=Answer;
        this.Solved=Solved;
        this.Submitted=Submmited;
    }

    public Trial(int TrialID, String CryptoID,
                 boolean Submmited, boolean Solved,String Answer,String Assignee,String Assigned){
        this.TrialID=TrialID;
        this.CryptoID=CryptoID;
        this.Solved=Solved;
        this.Submitted=Submmited;
        this.Answer=Answer;
        this.Assignee=Assignee;
        this.Assigned=Assigned;

    }




}
