package com.gmail.aina.nary.sudoku;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Stack;

import static com.gmail.aina.nary.sudoku.R.id.bdel;
import static com.gmail.aina.nary.sudoku.R.id.bnote;
import static com.gmail.aina.nary.sudoku.R.id.chrono;
import static com.gmail.aina.nary.sudoku.R.id.clavier2;
import static com.gmail.aina.nary.sudoku.R.id.clavier_main;
import static com.gmail.aina.nary.sudoku.R.id.layoutpause;
import static com.gmail.aina.nary.sudoku.R.id.layoutmain_sudoku;
import static com.gmail.aina.nary.sudoku.R.id.textdifficulty;

/**
 * Created by Nary Aina on 31/05/2017.
 * Note : essayer de gerer l'espace en mdoe note (1er char est decalé)
 */

public class Activity_Sudoku extends AppCompatActivity {

    private AdView mAdView;

    private int sizeS = 9;
    private int[][] sudoku;
    private int[][] sudoku_p_save;
    private int[][] sudoku_p;
    private boolean[][] sudoku_write = new boolean[sizeS][sizeS]; //droit d'ecrire
    private String[][] sudoku_note = new String[sizeS][sizeS];      //case note
    private String toPrint = " ";
    private SudokuVisual sv;
    private int difficulte;
    private int case_vide = 0;
    private TextView sudoku_save = null;
    private boolean mode_note = false;
    private boolean ending = false;
    private SudokuTab sd;

    //variable pour chrono
    private long startTime;
    private Chronometer chronosudo;
    private long timeWhenStopped = 0;
    private boolean enpause = false;

    private LinearLayout layout_sudoku;
    private LinearLayout layout_clavier;
    private LinearLayout layout_pause;
    private LinearLayout layout_clavier_sub2;
    private MenuItem menupause;
    private MenuItem menuplay;
    private MenuItem menurestart;
    private MenuItem menunewstart;
    private TextView txt_difficulty;

    private ImageButton ibnote;
    private ImageButton ibdelete;

    //private String pub_string = "ca-app-pub-3940256099942544~3347511713";
    //private String pub_string = "ca-app-pub-3940256099942544/6300978111"; // pub test 2 ?
    private String pub_string = "ca-app-pub-7882919351226127/7348738690"; // vrai pub
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String data = getIntent().getExtras().getString("Difficulty");
        difficulte = Integer.parseInt(data);
        setContentView(R.layout.activity_sudoku_vertical);
        sd = new SudokuTab();
        chronosudo = (Chronometer) findViewById(chrono);
        layout_sudoku = (LinearLayout) findViewById(layoutmain_sudoku);
        layout_clavier = (LinearLayout) findViewById(clavier_main);
        layout_clavier_sub2 = (LinearLayout) findViewById(clavier2);
        layout_pause = (LinearLayout) findViewById(layoutpause);
        txt_difficulty = (TextView) findViewById(textdifficulty);

        //pubs
        MobileAds.initialize(this, pub_string);
        mAdView = (AdView) findViewById(com.gmail.aina.nary.sudoku.R.id.adView);
        AdRequest adRequest = new AdRequest
                .Builder()
                .addTestDevice("D27EBAFFA1BB486E2C0D0C86E6FB1172")
                .addTestDevice("920735A35F946929BC1481832D02CB43")
                        .build();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("5D39C88A0B07910E").build();

        mAdView.loadAd(adRequest);
        delete_sudoku();
        creer_sudoku();
        afficher_sudoku();

        creer_pb_sudoku();
        chronosudo.setBase(SystemClock.elapsedRealtime());

        chronosudo.start();
        // pour version inferieur a lollipop
        ibnote = (ImageButton) findViewById(bnote);
        ibdelete = (ImageButton) findViewById(bdel);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ibnote.setBackgroundColor(getResources().getColor(R.color.colorGray));
            ibdelete.setBackgroundColor(getResources().getColor(R.color.colorGray));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.gmail.aina.nary.sudoku.R.menu.main_menu, menu);
        menupause = menu.findItem(com.gmail.aina.nary.sudoku.R.id.menu_pause);
        menuplay = menu.findItem(com.gmail.aina.nary.sudoku.R.id.menu_play);
        menunewstart = menu.findItem(com.gmail.aina.nary.sudoku.R.id.menu_next);
        menurestart = menu.findItem(com.gmail.aina.nary.sudoku.R.id.menu_reload);
        return true;
    }

    @SuppressWarnings("unused")
    public void caseSudoku(View view) {
        String tag = (String) view.getTag();

        if (sudoku_save != null) { //recolorier la case precedemment utilise en blanc
            color_case(get_coordX(sudoku_save),get_coordY(sudoku_save), com.gmail.aina.nary.sudoku.R.color.colorWhite);
        }

        //tester si on a le droit d'ecrire
        int x = Character.getNumericValue(tag.charAt(0));
        int y = Character.getNumericValue(tag.charAt(1));
        if (sudoku_write[x][y]) {

            color_case(x,y, com.gmail.aina.nary.sudoku.R.color.colorLightBlue2);
            sudoku_save = (TextView) view;
        }
    }

    public void sudoku_clavier_num(View view) {
        if (sudoku_save != null) {
            int x = get_coordX(sudoku_save);
            int y = get_coordY(sudoku_save);
            if (mode_note) {
                //mode prise de note
                toPrint = (String) view.getTag();
                if (sudoku_save != null) {
                    ajoute_note(toPrint, x, y);
                }
            }
            else {
                toPrint = (String) view.getTag();
                if (sudoku_save != null) {
                    ecrire_num(toPrint);
                    ajoute_num(toPrint,x,y);
                }
            }
        }
        if (case_vide == 0){
            check_end2();
        }
    }
    public void sudoku_clavier_delete(View view) {

        if (sudoku_save != null) {
            if (mode_note) {
                //mode prise de note
                remove_note(get_coordX(sudoku_save),get_coordY(sudoku_save));
            }
            else {
                toPrint = " ";
                ecrire_num(toPrint);
                int x = get_coordX(sudoku_save);
                int y = get_coordY(sudoku_save);
                enleve_num(x,y);
                remove_note_all(x,y);
            }
        }
    }

    public void sudoku_clavier_note(View view) {
    //SDK conflit
        mode_note = !mode_note;
            if (mode_note) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setBackgroundTintList((ContextCompat.getColorStateList(this, R.color.colorGrayDark)));
                }
                else {
                    view.setBackgroundColor(getResources().getColor(R.color.colorGrayDark));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setBackgroundTintList((ContextCompat.getColorStateList(this, R.color.colorGray)));
                }
                else {
                    view.setBackgroundColor(getResources().getColor(R.color.colorGray));
                }
            }
    }

    private String coord_to_id(int x, int y) {
        return "c" + x + y;
    }

    private int get_coordX(View view) {
        String t = (String) view.getTag();
        char x = t.charAt(0);
        return Character.getNumericValue(x);
    }

    private int get_coordY(View view) {
        String t = (String) view.getTag();
        char y = t.charAt(1);
        return Character.getNumericValue(y);
    }
    private int get_case(int x, int y){
        String c = coord_to_id(x,y);
        return this.getResources().getIdentifier(c, "id", this.getPackageName());
    }

    private void write_case(int x, int y, String s) {
        int resID = get_case(x, y);
        TextView tv = (TextView) findViewById(resID);
        tv.setText(s);
    }


    private void color_case(int x, int y, int colorID) {
        int resID = get_case(x, y);
        TextView tv = (TextView) findViewById(resID);
        tv.setBackgroundResource(colorID);
    }

    private void colorTint_case(int x, int y, int colorID) {
        int resID = get_case(x, y);
        TextView tv = (TextView) findViewById(resID);
        //SDK conflit
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tv.getBackground().setTint(ContextCompat.getColor(this, colorID));
        }
        //else {
            //tv.getBackground().setTint(ContextCompat.getColor(this, colorID));
        //}
    }

    //pb : colore les background et on perd donc les case grise à ne pas toucher ??
    public void color_helper(int x, int y, int colorID) {
        int resID;
        int xv,yv;
        //colorer colone
        for (int i = 0; i<9;i++) {
            color_case(x,i,colorID);
        }
        //colorer ligne
        for (int i = 0; i<9;i++) {
            color_case(i,y,colorID);
        }
        //colorer la zone 3x3
        int xc = (x/3)*3;
        int xy = (y/3)*3;
        for (int i = 0;i<3;i++) {
            for (int j = 0;j<3;j++) {
                color_case(xc+i,xy+j,colorID);
            }
        }
    }

    public void color_helper_delete(int x, int y) {
        int resID;
        int xv,yv;
        //colorer colone
        for (int i = 0; i<9;i++) {
            if (sudoku_write[x][i]){
                color_case(x,i, com.gmail.aina.nary.sudoku.R.color.colorWhite);
            }
            else {
                color_case(x,i, com.gmail.aina.nary.sudoku.R.color.colorGray);
            }
        }
        //colorer ligne
        for (int i = 0; i<9;i++) {
            if (sudoku_write[i][y]){
                color_case(i,y, com.gmail.aina.nary.sudoku.R.color.colorWhite);
            }
            else {
                color_case(i,y, com.gmail.aina.nary.sudoku.R.color.colorGray);
            }
        }
        //colorer la zone 3x3
        int xc = (x/3)*3;
        int xy = (y/3)*3;
        for (int i = 0;i<3;i++) {
            for (int j = 0;j<3;j++) {
                if (sudoku_write[xc+i][xy+j]){
                    color_case(xc+i,xy+j, com.gmail.aina.nary.sudoku.R.color.colorWhite);
                }
                else {
                    color_case(xc+i,xy+j, com.gmail.aina.nary.sudoku.R.color.colorGray);
                }
            }
        }
    }


    private void ajoute_note(String s, int x, int y){
        if (sudoku_note[x][y] == null) {
            sudoku_note[x][y] = sudoku_note[x][y] + s + " ";
        }
        else {
            //tester si contient deja le chiffre
            if (sudoku_note[x][y].indexOf(s) == -1) {
                if (sudoku_note[x][y].length() <= 16) {
                    if ((sudoku_note[x][y].length() - 4) % 6 == 0) { //si on doit retourner a la ligne
                        sudoku_note[x][y] = sudoku_note[x][y] + s + "\n";
                    } else {
                        sudoku_note[x][y] = sudoku_note[x][y] + s + " ";
                    }
                }
            }
        }
        ecrire_note(x,y);
    }
    private void ajoute_num(String s, int x, int y) { //ecrire dans les donnee
        int i = Integer.parseInt(s);
        if (sudoku_p[x][y] == 0) { //reecriture
            case_vide--;
        }
        sudoku_p[x][y] = i;

    }

    private void enleve_num(int x, int y) {
        if (sudoku_p[x][y] != 0) {
            sudoku_p[x][y] = 0;
            case_vide++;
        }
    }
    private void remove_note(int x, int y) {

        String s = sudoku_note[x][y];
        if (s.length()>1) {
            s = s.substring(0, s.length() - 2);
        }
        sudoku_note[x][y] = s;
        sudoku_save.setText(s);
    }

    //A tester
    private void remove_note_all(int x, int y){
        String s = "";
        sudoku_note[x][y] = s;
        sudoku_save.setText(s);
    }

    private void ecrire_note(int x, int y) {
        if (sudoku_save != null) {
            sudoku_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(com.gmail.aina.nary.sudoku.R.dimen.text_size_mini));
            sudoku_save.setText(sudoku_note[x][y]);
        }
    }
    private void ecrire_num(String s) { //ecrire dans l'UI
        if (sudoku_save != null) {
            //sudoku_save.setTextSize(numSize);
            sudoku_save.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(com.gmail.aina.nary.sudoku.R.dimen.text_size_normal));
            sudoku_save.setText(s);
        }
    }

    private void init_tabl() {
        for (int i=0;i<sizeS;i++) {
            for (int j=0;j<sizeS;j++) {
                sudoku_note[i][j] = "";
            }
        }
    }


    public void check_end2() {

        //boolean fin = sd.test_solution_sudoku(sudoku_p);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (sd.verifier_solution(sudoku_p)) { //erreur ! isValidAll test la prescence de 0
            chronosudo.stop();
            ending = true;
            showEndGame();
            long elapsedMillis = SystemClock.elapsedRealtime() - chronosudo.getBase();


            int outh = (int) elapsedMillis/3600000; //heure
            int outm = (int) (elapsedMillis - outh*360000)/60000;
            int outs = (int) (elapsedMillis - (outm*60000 + outh*3600000))/1000;
            String messageEnd = getResources().getString(com.gmail.aina.nary.sudoku.R.string.end_win_msg) +   " ";
            if (outh > 0) {
                messageEnd = messageEnd + outh + " h ";
            }
            if (outm >0) {
                messageEnd = messageEnd + outm + " min ";
            }
            messageEnd = messageEnd + outs + " sec";
            builder.setMessage(messageEnd)
                    .setTitle(getResources().getString(com.gmail.aina.nary.sudoku.R.string.end_win_title))
                    /*.setPositiveButton(getResources().getString(R.string.finish), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                            Activity_Sudoku.this.finish();
                        }
                    }) */
                    .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.back), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }
        else {
            Stack<Integer> si = new Stack<>();

            si = sd.numberCount(sudoku_p);

            if (si.size()==1) {

                String s = getResources().getString(com.gmail.aina.nary.sudoku.R.string.error1) + " " + Integer.toString(si.pop());
                builder.setMessage(s)
                        .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.back), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setTitle(getResources().getString(com.gmail.aina.nary.sudoku.R.string.error_title));
            }
            else {
                String s = Integer.toString(si.pop());
                while(si.size()>0) {
                    s = s + "-" + Integer.toString(si.pop());
                }
                s = getResources().getString(com.gmail.aina.nary.sudoku.R.string.error2)+  System.getProperty ("line.separator") + s;
                builder.setMessage(s)
                        .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.back), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setTitle(getResources().getString(com.gmail.aina.nary.sudoku.R.string.error_title));
            }
            /*
            builder.setMessage(getResources().getString(R.string.error))
                    .setNegativeButton(getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setTitle(getResources().getString(R.string.error_title));
                    */
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onBackPressed() {
        // your code.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(com.gmail.aina.nary.sudoku.R.string.back_message))
                .setPositiveButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Activity_Sudoku.this.finish();

                    }
                })
                .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void exitButton(View view) {
        onBackPressed();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case com.gmail.aina.nary.sudoku.R.id.menu_pause:

                if (!ending) {
                    hideGame();
                    stopChrono();
                    enpause = true;

                    menupause.setVisible(false);
                    menuplay.setVisible(true);
                }

                return true;
            case com.gmail.aina.nary.sudoku.R.id.menu_play:
                resumeChrono();
                showGame();
                enpause = false;

                menupause.setVisible(true);
                menuplay.setVisible(false);
                return true;
            case com.gmail.aina.nary.sudoku.R.id.menu_reload:

                builder.setMessage(getResources().getString(com.gmail.aina.nary.sudoku.R.string.restart_message))
                        .setPositiveButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                redemarrer_sudoku();
                                resetChrono();
                                resumeChrono();
                            }
                        })
                        .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                builder.show();

                return true;

            case com.gmail.aina.nary.sudoku.R.id.menu_next:

                builder.setMessage(getResources().getString(com.gmail.aina.nary.sudoku.R.string.new_start_message))
                        .setPositiveButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                delete_sudoku();

                                creer_sudoku();
                                afficher_sudoku();

                                creer_pb_sudoku();
                                resetChrono();
                                resumeChrono();
                                menupause.setVisible(true);
                                menuplay.setVisible(false);
                                showGame();
                            }
                        })
                        .setNeutralButton(getResources().getString(R.string.new_difficulty),
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        finish();

                                    }
                                })
                        .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                builder.show();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void stopChrono() {
        timeWhenStopped = chronosudo.getBase() - SystemClock.elapsedRealtime();
        chronosudo.stop();
        enpause = true;
    }

    private void resumeChrono() {
        chronosudo.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronosudo.start();
        enpause = false;
    }

    private void resetChrono() {
        chronosudo.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
    }

    private void hideGame() {
        layout_sudoku.setVisibility(View.GONE);
        layout_clavier.setVisibility(View.GONE);
        layout_pause.setVisibility(View.VISIBLE);
    }

    private void showGame() {
        layout_sudoku.setVisibility(View.VISIBLE);
        layout_clavier.setVisibility(View.VISIBLE);
        layout_pause.setVisibility(View.GONE);
    }

    private void showEndGame() {
        layout_clavier.setVisibility(View.GONE);
        layout_pause.setVisibility(View.VISIBLE);
    }

    private void hideEndGame() {
        layout_clavier.setVisibility(View.VISIBLE);
        layout_pause.setVisibility(View.GONE);
    }

    private void creer_pb_sudoku() {
        case_vide = 0;
        sudoku_save = null;
        init_tabl();
        for (int i=0;i<sizeS;i++) {
            for (int j = 0; j < sizeS; j++) {
                sudoku_note[i][j] = "";
                if (sudoku_p[i][j] == 0){
                    write_case(i,j," ");
                    sudoku_write[i][j] = true;
                    color_case(i,j, com.gmail.aina.nary.sudoku.R.color.colorWhite);
                    case_vide++;
                }
            }
        }
    }

    private void redemarrer_sudoku() {
        ending = false;
        hideEndGame();
        sudoku_p = sd.copyTab(sudoku_p_save);
        creer_pb_sudoku();
    }
    public void nouvel_partie(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(com.gmail.aina.nary.sudoku.R.string.new_start_message))
                .setPositiveButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delete_sudoku();

                        creer_sudoku();
                        afficher_sudoku();

                        creer_pb_sudoku();
                        resetChrono();
                        resumeChrono();
                        showGame();
                        menupause.setVisible(true);
                        menuplay.setVisible(false);
                    }
                })

                .setNeutralButton(getResources().getString(R.string.new_difficulty),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();

                            }
                        })
                .setNegativeButton(getResources().getString(com.gmail.aina.nary.sudoku.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        builder.show();

    }

    private void creer_sudoku() {
        sudoku = new int[sizeS][sizeS];
        sudoku = sd.genSudokuBC(sudoku);
        ending = false;
        //Generer le jeux selon difficulte
        sudoku_p = sd.pbgen2(sudoku,difficulte);
        sudoku_p_save = sd.copyTab(sudoku_p);
        String str = null;
        switch (difficulte) {
            case 1 :
                str = getString(com.gmail.aina.nary.sudoku.R.string.dif_easy)+ " ";break;
                //txt_difficulty.setText(getString(R.string.dif_easy)+ " "); break;
            case 2 :
                str = getString(com.gmail.aina.nary.sudoku.R.string.dif_med)+ " ";break;
                //txt_difficulty.setText(getString(R.string.dif_med)+ " ");break;
            case 3 :
                str = getString(com.gmail.aina.nary.sudoku.R.string.dif_hard)+ " "; break;
                //txt_difficulty.setText(getString(R.string.dif_hard)+ " ");break;
        }
        txt_difficulty.setText(str);
    }

    private void afficher_sudoku() {
        for (int i=0;i<sizeS;i++) {
            for (int j=0;j<sizeS;j++){
                write_case(i,j,Integer.toString(sudoku[i][j]));
                sudoku_write[i][j] = false;
                color_case(i,j, com.gmail.aina.nary.sudoku.R.color.colorGray);
            }
        }
    }

    private void delete_sudoku() {
        for (int i=0;i<sizeS;i++) {
            for (int j=0;j<sizeS;j++){
                write_case(i,j,"");
                color_case(i,j, com.gmail.aina.nary.sudoku.R.color.colorWhite);
                colorTint_case(i,j, com.gmail.aina.nary.sudoku.R.color.colorWhite);
            }
        }
    }
    public void finish() {
        super.finish();
        overridePendingTransition(com.gmail.aina.nary.sudoku.R.anim.fade_in, com.gmail.aina.nary.sudoku.R.anim.fade_out_long);
    }

    private void animationSlide() {

    }
}
