package com.gmail.aina.nary.sudoku;

public class CaseXYsudoku {

	private int x;
	private int y;
	private int xy;
	
	public CaseXYsudoku() {
		x = 0;
		y = 0;
		xy = 0;
	}
	
	public CaseXYsudoku(int x, int y, int xy){
		this.x = x;
		this.y = y;
		this.xy = xy;
	}
	
	public CaseXYsudoku(int x, int y) {
		this.x = x;
		this.y = y;
		xy = x + y*9;
	}
	
	public CaseXYsudoku(int xy) {
		this.xy = xy;
		y = xy/9;
		x = xy - 9*y;
	}
	
	public CaseXYsudoku nextCase(CaseXYsudoku c) {
		
		int xy2 = c.getXY() + 1;
		if (xy2 >80) {
			return c;
		}
		else {
			int y2 = xy2 /9;
			int x2 = xy2 - 9*y2;
			CaseXYsudoku c2 = new CaseXYsudoku(x2,y2,xy2);
			return c2;
		}
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getXY() {
		return xy;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	
}
