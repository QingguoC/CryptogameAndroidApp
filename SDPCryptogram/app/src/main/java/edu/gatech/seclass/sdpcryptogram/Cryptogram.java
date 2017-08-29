package edu.gatech.seclass.sdpcryptogram;

/**
 * Created by GuoFang on 7/8/17.
 */

public class Cryptogram {
    String id;
    String encodePhrase;
    String solution;
    public Cryptogram(String id, String ecodePhrase, String solution){
        this.id=id;
        this.encodePhrase=ecodePhrase;
        this.solution=solution;

    }
}
