package com.gmail.aina.nary.sudoku;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SudokuTab {
	private final int Tsize = 9;
	private int[][] sudoTab = new int[Tsize][Tsize];
	private boolean generer = true;

	public SudokuTab(){
	}

	public int[][] generateSudoku(){
		int[][] tab = new int[Tsize][Tsize];

		// *generer la 1er ligne* /////////
		Stack<Integer> pileChiffres = new Stack<Integer>();
		Stack<Integer> ptmp = new Stack<Integer>();
		pileChiffres = fillStack19();
		ptmp = (Stack) pileChiffres.clone();

		for (int i=0;i<Tsize;i++){
			int r;
			r = (int) Math.round(Math.random()*(ptmp.size()-1));
			tab[0][i] = ptmp.remove(r);
		}
		//////////////////////////////////

		// *generer les lignes suivantes*//////
		for (int i=1;i<Tsize;i++){
			for (int j=0;j<Tsize;j++){
				tab[i][j] = genNumber(tab,i,j);
			}
		}
		//////////////////////////////////////
		return tab;
	}

	public int genNumber(int[][] sd, int x, int y){
		int number = 0;
		int r = 0;
		Stack<Integer> si = new Stack<Integer>();
		si = fillStack19();
		int toremove;

		//horizontale
		for (int i=x;i>=0;i--){
			toremove = sd[i][y];
			si.remove(Integer.valueOf(toremove));
		}

		//verticale
		for (int i=y;i>=0;i--){
			toremove = sd[x][i];
			si.remove(Integer.valueOf(toremove));
		}

		//cellule 9x9
		int h = (x/3)*3;
		int v = (y/3)*3;
		for (int i=0;i<3;i++){
			for (int j=0;j<3;j++){
				toremove = sd[i+h][j+v];
				si.remove(Integer.valueOf(toremove));
			}
		}
		if (si.isEmpty()) {
			return number;
		}
		else {
			r = (int) Math.round(Math.random()*(si.size()-1));
			number = si.elementAt(r);
			return number;
		}

	}

	//Rempli un stack de chiffre de 1 à 9
	public Stack<Integer> fillStack19(){
		Stack<Integer> sf = new Stack<Integer>();
		for (int i=1;i<Tsize+1;i++){
			sf.push(i);
		}
		return sf;
	}

	public boolean isValid3x3(int[][] tab){
		boolean ok = true;
		for (int i=0;i<3;i++){
			for (int j=0;j<3;j++){
				if ((tab[i][j]) == 0){
					ok = false;
				}
			}
		}
		return ok;
	}

	public boolean isValidAll(int[][] tab){
		boolean ok = true;
		for (int i=0;i<Tsize;i++){
			for (int j=0;j<Tsize;j++){
				if ((tab[i][j]) == 0){
					ok = false;
				}
			}
		}
		return ok;
	}

	//enlever 2-3 chiffre dans un bloc 3*3
	public int[][] pbgen1(int[][] tab, int difficulty){
		int tab2[][] = copyTab(tab);
		int h,v,r;
		int todelete;
		int diff1 = 2;
		int diff2 = 3;
		int p;
		int x,y;
		Stack<Integer> pioche = new Stack<Integer>();

		//set difficulty
		switch (difficulty){
			case -1 :
				diff1 = 0;
				diff2 = 0;
            case 0 :
                diff1 = 1;
                diff2 = 1;
                break;
			case 1:
				diff1 = 2;
				diff2 = 3;
				break;
			case 2:
				diff1 = 2;
				diff2 = 5;
				break;
			case 3:
				diff1 = 3;
				diff2 = 9;
				break;
		}
		int trou_debug = 0;
		for (int i=0;i<3;i++){
			h=i*3;
			for (int j=0;j<3;j++){
				v=j*3;
				pioche = fillStack19();
				p = ((int)((Math.random()*(diff2-diff1)))) + diff1; //piocher entre diff1 et diff2
				//System.out.println("p = " + p);
				for (int k=0;k<p;k++){
					r = (int) Math.round(Math.random()*(pioche.size()-1));
					todelete = pioche.elementAt(r) - 1;
					pioche.removeElementAt(r);
					x = todelete / 3;
					y = todelete % 3;
					int cordx = x+(i*3);
					int cordy = y+(j*3);
					tab2[cordx][cordy] = 0;
					trou_debug++;
				}
			}
		}

		return tab2;
	}
	public int[][] pbgen2(int[][] tab, int difficulty) {
		int tab2[][] = new int[Tsize][Tsize];
		tab2 = copyTab(tab);
		int h,v,r;
		int todelete;
		int diff1 = 0;
		int diff2 = 1;
		int p;
		int x,y;
		double dr = (int) Math.random()*2;
		Stack<Integer> pioche = new Stack<Integer>();
		Stack<Integer> n_remove = new Stack<Integer>();

		//set difficulty
		switch (difficulty){
			case 0 :
                n_remove.add(0);
                n_remove.add(0);
                n_remove.add(0);
                n_remove.add(0);
				n_remove.add(0);
				n_remove.add(0);
				diff1 = 1;
				diff2 = 1;
				break;
			case 1:
				if (dr > 1 ) {
					n_remove.add(5);
					n_remove.add(6);
                    n_remove.add(2);
					diff1 = 3;
					diff2 = 4;
				}
				else {
					n_remove.add(7);
					n_remove.add(5);
					n_remove.add(1);
					diff1 = 3;
					diff2 = 4;
				}
				break;
			case 2:
				if (dr > 1 ) {
					n_remove.add(1);
					n_remove.add(7);
					n_remove.add(8);
					diff1 = 3;
					diff2 = 6;
				}
				else {
					n_remove.add(2);
					n_remove.add(5);
					n_remove.add(7);
					diff1 = 3;
					diff2 = 5;
				}
				break;
			case 3:
				if (dr > 1 ) {
					n_remove.add(9);
					n_remove.add(8);
					n_remove.add(4);
					n_remove.add(4);
					n_remove.add(3);
					diff1 = 3;
					diff2 = 7;
				}
				else {
					n_remove.add(3);
					n_remove.add(8);
					n_remove.add(5);
					n_remove.add(4);
					diff1 = 3;
					diff2 = 8;
				}

				break;
		}

		while (n_remove.size() < 9) {
			p = (int) Math.round(Math.random()*(diff2-diff1)) + diff1; //piocher entre diff1 et diff2
			n_remove.add(p);
		}
		for (int i=0;i<3;i++){
			h=i*3;
			for (int j=0;j<3;j++){
				v=j*3;
				pioche = fillStack19();
				p = n_remove.remove((int)(Math.random()*(n_remove.size()-1)));
				for (int k=0;k<p;k++){
					r = (int) Math.round(Math.random()*(pioche.size()-1));

					todelete = pioche.elementAt(r) - 1;
					pioche.removeElementAt(r);
					x = todelete / 3;
					y = todelete % 3;
					int cordx = x+(i*3);
					int cordy = y+(j*3);
					tab2[cordx][cordy] = 0;
				}
			}
		}
		return tab2;
	}
    public boolean test_solution_sudoku (int[][] sudo) {
        boolean result = true;
        int c;
        Set<Integer> numbers = new HashSet<>();
        //tester chaque ligne
        for (int i=0;i<Tsize;i++){
            for (int j=0;j<Tsize;j++){
                numbers.add(sudo[i][j]);

            }
            if (numbers.size() != Tsize) {
                result = false;
            }
            numbers.clear();
        }
        //tester chaque colone
        for (int i=0;i<Tsize;i++){
            for (int j=0;j<Tsize;j++){
                numbers.add(sudo[j][i]);

            }
            if (numbers.size() != Tsize || numbers.contains(0)) {
                result = false;
            }
            numbers.clear();
        }

        //tester les zones 3x3
        for (int i=0;i<3;i++) {			//pour chaque
            for (int j=0;j<3;j++) {		//zone 3x3

                //tester la zone 3x3
                for (int i2=0;i2<3;i2++) {
                    for (int j2=0;j2<3;j2++) {
                        numbers.add(sudo[i2 +i*3][j2 + j*3]);
                    }
                }
                if (numbers.size() != Tsize || numbers.contains(0)) {
                    result = false;
                }
                numbers.clear();
            }
        }
        return result;
    }
	public int[][] copyTab(int[][] tab) {
		int[][] copy = new int[Tsize][Tsize];
		for (int i=0;i<Tsize;i++) {
			for (int j=0;j<Tsize;j++) {
				copy[i][j] = tab[i][j];
			}
		}
		return copy;
	}

	public int[][] genSudokuBC(int[][] tab) {
		CaseXYsudoku c = new CaseXYsudoku();
		tab = genNumber2(tab,c);
		return tab;
	}
	//tester si un chiffre est deja dans la ligne, a utiliser AVANT ajout
	public boolean vertical_ok(int[][] sudo,int x, int num) {
		boolean ok = true;
		HashSet<Integer> numlist = new HashSet<Integer>();

		for (int i = 0;i<Tsize;i++) {
			numlist.add(sudo[x][i]);
		}
		if (numlist.contains(num)) {
			ok = false;
		}
		return ok;
	}

	public boolean horizontal_ok(int[][] sudo,int y, int num) {
		boolean ok = true;
		HashSet<Integer> numlist = new HashSet<Integer>();

		for (int i = 0;i<Tsize;i++) {
			numlist.add(sudo[i][y]);
		}
		if (numlist.contains(num)) {
			ok = false;
		}
		return ok;
	}

	public boolean block_ok (int[][] sudo,int x, int y, int num) {
		boolean ok = true;
		HashSet<Integer> numlist = new HashSet<Integer>();
		int xc = (x/3)*3;
		int xy = (y/3)*3;
		for (int i = 0;i<3;i++) {
			for (int j = 0;j<3;j++) {
				numlist.add(sudo[i+xc][j+xy]);
			}
		}
		if (numlist.contains(num)) {
			ok = false;
		}
		return ok;
	}

	public boolean all_test (int[][] sudo,int x, int y, int num) {

		boolean ok = ((vertical_ok(sudo,x,num))&&((horizontal_ok(sudo,y,num)))&&(block_ok(sudo,x,y,num)));

		return ok;
	}

	public boolean verifier_solution (int[][] sudo){
		boolean gagne = true;
		int[][] sudocopy = new int[Tsize][Tsize];
		for(int i=0;i<Tsize;i++) {
			for(int j=0;j<Tsize;j++) {
				if (!all_test(sudocopy,i,j,sudo[i][j])) {
					gagne = false;
					//System.out.println("ij = (" +i+","+j+")" );
				}
				sudocopy[i][j] = sudo[i][j];
			}
		}
		return gagne;
	}

	public int[][] genNumber2(int[][] tab, CaseXYsudoku c){

		//utiliser cursor pour naviguer ?
		Stack<Integer> pileChiffres = new Stack<Integer>();
		pileChiffres = fillStack19();
		int r;
		int toadd;
		while (generer) {
			r = (int) Math.round(Math.random()*(pileChiffres.size()-1));
			toadd = pileChiffres.remove(r);
			if (all_test(tab,c.getX(),c.getY(),toadd)){
				tab[c.getX()][c.getY()] = toadd;
				if(c.getXY()==80) { //derniere case remplie, tt est ok
					generer = false;
					return tab;
				}
				genNumber2(tab,c.nextCase(c));
			}
			if (pileChiffres.isEmpty()) {
				if (generer) {
					tab[c.getX()][c.getY()] = 0;
					return tab;
				}
				else {
					return tab;
				}
			}
		}

		if (c.getXY()==0) {
			generer = true;
		}
		return tab;
	}

	public Stack<Integer> numberCount(int[][] sudo){

		//Verifier l'occurence de chaque chiffre//////
		Stack<Integer> si = new Stack<>();
		Map<Integer,Integer> ma = new HashMap<>();
		int temp;
		for (int i=1;i<10;i++) {
			Integer iInteger = new Integer(i);
			ma.put(iInteger,0);
		}
		for (int i=0;i<9;i++) {
			for (int j=0;j<9;j++) {
				temp = ma.get(sudo[i][j]);
				Integer iInteger = new Integer(sudo[i][j]);
				ma.put(iInteger,temp+1);
			}
		}
		for (int i=1;i<10;i++) {
			Integer iInteger = new Integer(i);
			if(ma.get(iInteger) > new Integer(9)) {
				if (!si.contains(i)) {
					si.add(i);
				}
			}
		}

		///Verifier par ligne/colone
		Stack<Integer> sv = new Stack<>();
		Stack<Integer> sh = new Stack<>();
		for (int i=0;i<9;i++) {
			sv.removeAllElements();
			sh.removeAllElements();
			for (int j=0;j<9;j++) {
				Integer iInteger1 = new Integer(sudo[i][j]);
				if (sv.contains(iInteger1)){ //si deja dans la liste, erreur
					if (!si.contains(iInteger1)) {
						si.add(iInteger1);
					}
				}
				Integer iInteger2 = new Integer(sudo[j][i]);
				if (sh.contains(iInteger2)){ //si deja dans la liste, erreur
					if (!si.contains(iInteger2)) {
						si.add(iInteger2);
					}
				}
				sv.add(iInteger1);
				sh.add(iInteger2);
			}
		}

		//verifier par block
		Stack<Integer> sblock = new Stack<>();
		for (int x=0;x<3;x++) {
			for (int y=0;y<3;y++) {
				int xc = x*3;
				int xy = y*3;
				sblock.removeAllElements();
				for (int i = 0;i<3;i++) {
					for (int j = 0;j<3;j++) {
						Integer iInteger3 = new Integer(sudo[i+xc][j+xy]);
						if (sblock.contains(iInteger3)) {//si deja dans la liste erreur
							if (!si.contains(iInteger3)) {
								si.add(iInteger3);
							}
						}
						sblock.add(sudo[i+xc][j+xy]);
					}
				}
			}
		}
		return si;
	}
}