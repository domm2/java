/* 
Dominique Mittermeier's 1501 Assignment 1 
Execution: java Crossword dict8.txt test.txt
*/

import java.io.*;
import java.util.*;

public class Crossword {
    private DictInterface D;
	private char [][] board;
    private int size;
    private StringBuilder rowstr[];
    private StringBuilder colstr[];
    private StringBuilder rowSolution;
    private StringBuilder colSolution;
    private int score = 0;


    public static void main(String[] args) throws IOException{
       String d = args[0];
       String t = args[1];
       new Crossword(d, t);
    }//end main

    public Crossword(String d, String t) throws IOException{
        //read in dict from command line and create MyDictionary 
        Scanner s = new Scanner(new FileInputStream(d));
        String st;
		D = new MyDictionary();

		while (s.hasNext())
		{
			st = s.nextLine();
			D.add(st);
		}//end while
		s.close();

        //read in board txt file and store size 
        Scanner s2 = new Scanner(new FileInputStream(t));
        size = s2.nextInt();
        int row = size; int col = size; 
        rowstr = new StringBuilder[size]; colstr = new StringBuilder[size]; 
        for(int i =0; i < size; i++){
            rowstr[i] = new StringBuilder(); colstr[i] = new StringBuilder(); 
        }
        //System.out.println(); 
        buildBoard(size, s2, row, col);
        //printBoard(rowstr);
        solve(0, 0, rowstr, colstr);
    }//end crossword

    public void buildBoard(int size, Scanner s2, int row, int col){
        board = new char[row][col]; 
        
        for(int r=0; r < size; r++){ 
            
            String wholeRow = s2.next();

            for(int c=0; c < size; c++){
                board[r][c] = wholeRow.charAt(c);
            }//end cfor
        }//end rfor
    }//end buildBoard

    public void printBoard(StringBuilder[] rowstr) throws IOException{
        Scanner t = new Scanner(new FileInputStream("letterpoints.txt"));
        for(int r = 0; r < size; r++){
            int c = 0;
            String line;
            while(c<size){
                //String p = t.nextLine();
                //System.out.println(p);
                //System.out.println(score);
               // System.out.println(rowstr[r].charAt(c));
                line = t.nextLine();
                    if(rowstr[r].charAt(c) == Character.toLowerCase(line.charAt(0))){
                        //System.out.println(t.nextInt());
                        int x = line.charAt(2) - '0'; 
                        //System.out.println(x);
                        score = score + x;
                        c=c+1;
                    }//endif
                    if(t.hasNextLine() == false){
                        t.close();
                        t = new Scanner(new FileInputStream("letterpoints.txt"));
                    }
            }//endwhile
            System.out.println(rowstr[r].toString().toUpperCase());
        }//end forr

        System.out.println("Score: " + score); 
    }//end printBoard

    
    private Coordinates nextCoordinates(int row, int col){
		Coordinates result = null;
		if(col< size -1){ //col is not at edge
			result = new Coordinates(row, col+1);
            return result; 
        }//end if
        if(col == size -1){ //col is at edge
			result = new Coordinates(row+1, col - (size-1));
            return result; 
        }//end if
        return result; 
    }//end nextCoordinates

    public boolean isValid(char c, int row, int col){
        rowstr[row].append(c);
        int rval = D.searchPrefix(rowstr[row]);
        colstr[col].append(c);
        int cval = D.searchPrefix(colstr[col]);
        boolean val = false;
        rowstr[row].deleteCharAt(rowstr[row].length()-1); colstr[col].deleteCharAt(colstr[col].length()-1);

        //1 only prefix 
        //2 only word
        //3 both

        //if col is not at 3 then rowstr + c should be a PREFIX
        if(col < size-1){
            if(rval == 2 || rval == 0){ return false; }
        }//end if

        //if col IS at 3 then rowstr + c should be a WORD
        if(col == size -1){
            if(rval == 1 || rval == 0){ return false; }
        }//endif

        if(row < size-1){
            if(cval == 2 || cval == 0){ return false; }
        }//end if

        if(row == size -1){
            if(cval == 1 || cval == 0){ return false; }
        }//endif
       
        return true;
    }//end isValid


    public void solve(int row, int col, StringBuilder[] rowstr, StringBuilder[] colstr) throws IOException{
        switch(board[row][col]){
            case '+' :
                for(char c = 'a'; c <= 'z'; c++){
                    if (isValid(c, row, col)){ 
                        //append c to rowstr[row] and colstr[col]
                        rowstr[row].append(c); colstr[col].append(c);
                        //if at bottom right print sol and exit
                        if(row == size-1 && col == size-1){ printBoard(rowstr); System.exit(0); }//end if
                        //else solve(next coordinates, rowstr, colstr)
                        else{ 
                            Coordinates nextCoords = nextCoordinates(row, col);
                            solve(nextCoords.row, nextCoords.col, rowstr, colstr);
                        }//end else
                        //delete last char of rowstr[row] and colstr[col]
                        rowstr[row].deleteCharAt(rowstr[row].length()-1); colstr[col].deleteCharAt(colstr[col].length()-1);
                    }//end if
                }//end for
                break;
            case '-' :
                    //append - to rowstr[row] and colstr[col]
                    rowstr[row].append('-'); colstr[col].append('-');
                    //if at bottom right print sol and exit
                    if(row == size-1 && col == size-1){ printBoard(rowstr); System.exit(0); }//end if
                    //else solve(next coordinates, rowstr, colstr)
                    else{ 
                        Coordinates nextCoords = nextCoordinates(row, col);
                        solve(nextCoords.row, nextCoords.col, rowstr, colstr); 
                    }//end else
                    //delete last char of rowstr[row] and colstr[col]
                    rowstr[row].deleteCharAt(rowstr[row].length()-1); colstr[col].deleteCharAt(colstr[col].length()-1);
                break; 
            default:
                if (isValid(board[row][col], row, col)){
                    //append c to rowstr[row] and colstr[col]
                    rowstr[row].append(board[row][col]); colstr[col].append(board[row][col]);
                    //if at bottom right print sol and exit
                    if(row == size-1 && col == size-1){ printBoard(rowstr); System.exit(0); }//end if
                    //else solve(next coordinates, rowstr, colstr)
                    else{ 
                        Coordinates nextCoords = nextCoordinates(row, col);
                        solve(nextCoords.row, nextCoords.col, rowstr, colstr); 
                    }//end else
                    //delete last char of rowstr[row] and colstr[col]
                    rowstr[row].deleteCharAt(rowstr[row].length()-1); colstr[col].deleteCharAt(colstr[col].length()-1);
                }//end if
                break;
        }//end switch
        return;
    }//end solve

    //inner class
	private class Coordinates {
		int row;
		int col;

		private Coordinates(int row, int col){
			this.row = row;
			this.col = col;
		}//end Coordinates
	}//end Coordinates class
}//end class