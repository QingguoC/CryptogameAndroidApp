package edu.gatech.seclass.sdpcryptogram;

import android.support.annotation.NonNull;


class Rating implements Comparable {
    public String name;
    public int numSolved;
    public int numStarted;
    public int numFailed;
    public Rating (String name, int numSolved,int numStarted, int numFailed){
        this.name=name;
        this.numSolved=numSolved;
        this.numStarted=numStarted;
        this.numFailed=numFailed;
    }



    @Override
    public int compareTo(@NonNull Object o) {

        if (this.numSolved== ((Rating)o).numSolved){
            return 0;
        }else if (this.numSolved<((Rating)o).numSolved){
            return 1;
        } else {
            return -1;
        }
    }
}
