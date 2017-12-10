package com.gmail.aina.nary.sudoku;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Nary Aina on 01/06/2017.
 */

public class SudokuVisual {
    private View view;
    private Activity act;

    SudokuVisual(Activity a) {
        act = a;
    }

    public void printNum(View v,String lacase, String num){
        TextView tv;
        Log.v("E","Lacase : " + lacase);
        int resID = act.getResources().getIdentifier(lacase, "id", act.getPackageName());
        Log.v("E","Log int : " + Integer.toString(resID));
        tv = (TextView) act.findViewById(resID);
        tv.setText(num);

    }
}
