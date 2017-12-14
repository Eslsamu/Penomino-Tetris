package GameLogic;
import javafx.scene.paint.Color;

import java.util.ArrayList;


public class PentominoGenerator {
   private ArrayList<int[][]> pentominoList; 
	public PentominoGenerator(){
		//X/Y coordinates of the pieces of the 12 pentomino shapes
		pentominoList = new ArrayList<int[][]>();
                int [][] L = {{0,1,2,3,0},{0,0,0,0,1}}; //0
                pentominoList.add(L);
                int[][] I = {{0,1,2,3,4},{0,0,0,0,0}}; //1
                pentominoList.add(I);
                int[][] U = {{2,3,2,2,3},{0,0,1,2,2}}; //2
                pentominoList.add(U); 
                int[][] Z = {{1,2,2,2,3},{2,0,1,2,0}}; //3
                pentominoList.add(Z);
                int[][] X = {{2,1,2,3,2},{0,1,1,1,2}}; //4
                pentominoList.add(X);
                int[][] T = {{1,2,3,2,2},{0,0,0,1,2}}; //5
                pentominoList.add(T);
                int[][] V = {{2,2,2,3,4},{0,1,2,2,2}}; //6
                pentominoList.add(V);
                int[][] N = {{2,2,3,3,3},{0,1,1,2,3}}; //7
                pentominoList.add(N);
                int[][] F = {{2,3,1,2,2},{0,0,1,1,2}}; //8
                pentominoList.add(F); 
                int[][] W = {{2,2,3,3,4},{0,1,1,2,2}}; //9
                pentominoList.add(W);
                int[][] P = {{2,3,4,3,4},{0,0,0,1,1}}; //10
                pentominoList.add(P);
                int[][] Y = {{2,3,2,2,2},{0,1,1,2,3}}; //11
                pentominoList.add(Y);
	}
	
	public Pentomino getRandomPentomino(){
            int index = (int) (Math.random()*12);
            //exclude background color
            Color ranColor;
            do{
            ranColor = Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*254)); //254 to leave space for an exception store
            }
            while(ranColor.equals(Color.rgb(186, 216, 227)));
            
            Pentomino random = new Pentomino(pentominoList.get(index),ranColor); 
                
            return random;
	}

	public Pentomino getTestPentomino(int index){
            Color ranColor;
            do{
            ranColor = Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*254)); //254 to leave space for an exception store
            }
            while(ranColor.equals(Color.rgb(186, 216, 227)));
            
            return new Pentomino(pentominoList.get(index), ranColor);
	}
}