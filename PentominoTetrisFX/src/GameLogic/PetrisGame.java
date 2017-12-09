package GameLogic;
import java.util.ArrayList;

import Agent.Agent;

import Dynamics.Controlls;
import Dynamics.GameCycle;
import View.MainView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;


public class PetrisGame extends FloodFill{
    private final int HEIGHT = 15;
    private final int WIDTH = 5;
    
    private int level = 1;
    private int score = 0;
    private int rowsCleared = 0;
    private double delay = 500;
    private double speedIncrease = 0.8;
    
    private boolean isRunning = false; //+ get&setters
    private boolean hasBot = false;
    
    private MainView view;
    
    //private TimeCycle cycle;//TODO debug
    private GameCycle cycle;
    
    private Controlls controlls;
    
    private Color[][] gridMatrix;
    private Pentomino fallingBlock;
    private Pentomino nextBlock;
    private Agent agent;
	
    
    public PetrisGame() {
    	gridMatrix = new Color[HEIGHT][WIDTH];
    	PentominoGenerator startGenerator = new PentominoGenerator();
        nextBlock = startGenerator.getRandomPentomino();   
        
        view = new MainView(this);
    	cycle = new GameCycle(this);
        controlls = new Controlls(this);
        
        isRunning = true;
        this.hasBot = false;
        spawn();
        
    }
    
    public PetrisGame(boolean bot) {
    	gridMatrix = new Color[HEIGHT][WIDTH];
    	PentominoGenerator startGenerator = new PentominoGenerator();
        nextBlock = startGenerator.getRandomPentomino();   
        
        view = new MainView(this);
        cycle = new GameCycle(this);
        controlls = new Controlls(this);
        
        this.hasBot = bot;
        
        
        if(bot) {
        	agent = new Agent(this);
        	delay = 1000;
        }
        
        isRunning = true;
        spawn();
    }
    
    public PetrisGame(int height, int width,boolean bot){
        gridMatrix = new Color[height][width];
        PentominoGenerator startGenerator = new PentominoGenerator();
        nextBlock = startGenerator.getRandomPentomino(); 
        
        cycle = new GameCycle(this);
        view = new MainView(this);
        controlls = new Controlls(this);
        
        this.hasBot = bot;
        
        if(bot) {
        	agent = new Agent(this);
        }
        isRunning = true;
        spawn();
    }
    
    
    public Controlls getControlls() {
    	return controlls;
    }
    
    public MainView getView() {
    	return view;
    }
    
    public Scene getScene() {
    	Scene scene = new Scene(view);
    	scene.addEventFilter(KeyEvent.KEY_PRESSED, controlls);
    	return scene;
    }
    
    public GameCycle getCycle() {
    	return cycle;
    }
    
    public boolean getIsRunning() {
    	return isRunning;
    }
    
    public void setIsRunning(boolean r) {
    	isRunning = r;
    }
    
    public Pentomino getFallingBlock() {
    	return fallingBlock;
    }
    
    public Pentomino getNextBlock() {
    	return nextBlock;
    }
    
    public Color[][] getGrid(){
    	return gridMatrix;
    }
    
    public int getLevel() {
    	return level;
    }
    
    public int getScore() {
    	return score;
    }
    
    public int getRowsCleared() {
    	return rowsCleared;
    }
    public double getSpeed(){
        return delay;
    }
    
    
    public void pause() {
    	isRunning=false;
    	cycle.pause();
    }
    
    public void runGame() {
    	isRunning = true;
    	cycle.run();
    }
    
    public boolean gameOverCheck() {
        //gives errors, to be fixed
        int[][] coordinates = nextBlock.getCoordinates();
        for (int i = 0; i < coordinates[0].length; i++){
           if(gridMatrix[coordinates[1][i]][coordinates[0][i]] != null) {
    		//fallingBlock = null; //currently not needed
    		System.out.println("GameOver"); //will be printed twice due to double use of gameOverCheck(), which is needed
    		hasBot = false; //stop the bot from making a new move
    		isRunning = false;
    		cycle.getGameCycle().end();
    		System.out.println("check1");
            return true;
            }              
    	} 
        return false;
    }
    
    public void spawn() {
    	if(!isRunning) {
    		return;
    	}
    	fallingBlock = nextBlock;
    	PentominoGenerator startGenerator = new PentominoGenerator();
        nextBlock = startGenerator.getRandomPentomino();    
        if(hasBot) {
        	agent.makeMove();
        }
    }
    
    public void updateView() {
    	view.updateMain();
    	System.out.println("updated view in model...");
    }
    
    public void move(Direction aDirection){
        if(!doesCollide(aDirection)&&isRunning) {
            int[][] changeCoords = fallingBlock.getCoordinates();
            switch(aDirection) {
        	case DOWN:  {       		
                for(int i = 0; i < changeCoords[1].length; i++){
                    changeCoords[1][i]++;
                }
        		break;
        	}	
        	case RIGHT: {
            	for(int i = 0; i < changeCoords[0].length; i++){
                	changeCoords[0][i]++;
            	}
            	break;
        	}        	
        	case LEFT: {
        		for(int i = 0; i < changeCoords[0].length; i++){
        			changeCoords[0][i]--;
        		}
        		break;
        	}
        	case CLOCKWISE: {
        		changeCoords = rotate(changeCoords, 90,true); //board is swapped around, so 90 instead of 270 
                        break;
        	}   
        	case COUNTERCLOCKWISE: {
        		 changeCoords = rotate(changeCoords, 270,true); 
                         break; 
        	}                    
            }
            fallingBlock.setCoordinates(changeCoords);
        }
    }    
    
    public int[][] rotate(int[][] coords, double degrees, boolean smallBoardRotation){//smallBoardRotation is a feature to rotate the block even if it would hit a wall in that position
            int rotationPoint = coords[0].length / 2;
            int reducedAmountX = coords[0][rotationPoint];
            int reducedAmountY = coords[1][rotationPoint];
                    
            for(int i = 0; i < coords[0].length; i++){
                coords[0][i] -= reducedAmountX;
                coords[1][i] -= reducedAmountY;
            } 
        
            int cos = (int) Math.cos(Math.toRadians(degrees));
            int sin = (int) Math.sin(Math.toRadians(degrees));
            int[][] transformationMatrix = {{cos,-sin},{sin,cos}};
        
            coords = multiplyMatrix(transformationMatrix, coords);
        
            for(int i = 0; i < coords[0].length; i++){
              coords[0][i] += reducedAmountX;
              coords[1][i] += reducedAmountY;
            }
            /* following code until return is something additionally added
                if your pentomino is out of the board after rotating it, it will return it to the board
                the usual games do this and is easier to play, because the board is small and for almost all pentos
                the rotation works if they are in column 2
            */
            if(smallBoardRotation) {
            	for(int i = 0; i < coords[0].length; i++){
                	while(coords[0][i] >= gridMatrix[0].length){
                    	for(int x = 0; x < coords[0].length; x++){
                        	coords[0][x] = coords[0][x] - 1;
                    	}
                	}
                	while(coords[0][i] < 0){
                    	for(int x = 0; x < coords[0].length; x++){
                        	coords[0][x] = coords[0][x] + 1;
                    	}
                	}    
                	while(coords[1][i] >= gridMatrix.length){
                    	for(int x = 0; x < coords[0].length; x++){
                        	coords[1][x] = coords[1][x] - 1;
                    	}
                	}
                	while(coords[1][i] < 0){
                    	for(int x = 0; x < coords[0].length; x++){
                        	coords[1][x] = coords[1][x] + 1;
                    	}
                	}  
            	}
            }
            return coords;
    }
    public boolean doesCollide(int x, int y){
        if(y + 1 >= gridMatrix.length || gridMatrix[y + 1][x] != null){
            return true;
        }
        return false;
    }
    
    public boolean doesCollide(Direction movingDirection){
        int[][] checkCoords = new int[fallingBlock.getCoordinates().length][fallingBlock.getCoordinates()[0].length];
        
        for(int i = 0; i < checkCoords.length; i++){
            checkCoords[i] = fallingBlock.getCoordinates()[i].clone();
        }  
        
        if(movingDirection == Direction.DOWN){
            for(int i = 0; i < checkCoords[1].length; i++){                     	
                if(checkCoords[1][i] + 1 >= gridMatrix.length || gridMatrix[ checkCoords[1][i] + 1 ][ checkCoords[0][i] ]!=null){
                   //here a new block gets created as it fell down to the ground
                   placePento(fallingBlock);
                   clearRows();
                   return true;
                }
            }   
        }
        else if(movingDirection == Direction.LEFT) {
        	for(int i = 0; i < checkCoords[0].length;i++) {
        		if(checkCoords[0][i] - 1 < 0 || gridMatrix[ checkCoords[1][i] ][checkCoords[0][i] - 1 ]!=null) {
        			return true;
        		}
        	}
        }
        else if(movingDirection == Direction.RIGHT) {
        	for(int i = 0; i < checkCoords[0].length;i++) {
        		if(checkCoords[0][i] + 1 >= gridMatrix[0].length || gridMatrix[ checkCoords[1][i] ][checkCoords[0][i] + 1 ]!=null) {

        			return true;
        		}
        	}
        }
        else if(movingDirection == Direction.CLOCKWISE) {     
            int[][] checkRotationCoords = rotate(checkCoords,90,true);
            for(int i = 0; i < checkRotationCoords[0].length; i++){
                if(checkRotationCoords[0][i]>=gridMatrix[0].length || checkRotationCoords[1][i]>=gridMatrix.length || checkRotationCoords[0][i]<0 || checkRotationCoords[1][i]<0 || gridMatrix[checkRotationCoords[1][i]][checkRotationCoords[0][i]]!=null){
                    return true;
                }
            }
        }
        else if(movingDirection == Direction.COUNTERCLOCKWISE) {     
            int[][] checkRotationCoords = rotate(checkCoords,270,true);
            for(int i = 0; i < checkRotationCoords[0].length; i++){
                if(checkRotationCoords[0][i]>=gridMatrix[0].length || checkRotationCoords[1][i]>=gridMatrix.length || checkRotationCoords[0][i]<0 || checkRotationCoords[1][i]<0 || gridMatrix[checkRotationCoords[1][i]][checkRotationCoords[0][i]]!=null){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void placePento(Pentomino aPentomino){
            int[][] whereToPlace = aPentomino.getCoordinates();
            Color colorIndex = aPentomino.getColorIndex();
            for(int i = 0; i < whereToPlace[0].length; i++){
                gridMatrix[whereToPlace[1][i]][whereToPlace[0][i]] = colorIndex;
            }
            gameOverCheck();
            if(isRunning) {
            	spawn();
            }
    }
    
    public void levelUp() {
    	if(score/level >= 500){
            level++;
            delay = delay*speedIncrease;
        }
    }
    
    public ArrayList<Integer> rowsToClear(){
        ArrayList<Integer> rowsToClear = new ArrayList<>();
        for(int i = 0; i < gridMatrix.length; i++){
            boolean isRowFull = true;
            for(int j=0;j<gridMatrix[0].length;j++){
                if(gridMatrix[i][j]==null){
                    isRowFull = false;
                }
            }
            if(isRowFull){
                rowsToClear.add(i);
            }
        }
        return rowsToClear;
    }
    
    
    public void clearRows(){
        ArrayList<Integer> rowsToClear = rowsToClear();
        //if there are no rows to clear, then nothing happens (because rowToClear.size = 0)
        while(rowsToClear.size() != 0){
          rowsCleared = rowsCleared + rowsToClear.size();
          score = score + 100*rowsToClear.size();
          levelUp();
          //for debugging
          System.out.println("Rows cleared: " + rowsCleared + " Score: " + score + " Level: " + level + " Speed: " + delay);
          /* This part clears a row that rowsToClear returns and then will move everything above it one row down
            until there are no more rows in rowsToClear
          */
            for(int i = 0; i < rowsToClear.size(); i++){
                //move everything one down
                for(int x = rowsToClear.get(i) - 1; x > -1; x--){
                            for(int t = 0; t < gridMatrix[0].length; t++){
                                gridMatrix[x + 1][t] = gridMatrix[x][t];
                                if(x == 0){
                                    gridMatrix[x + 1][t] = gridMatrix[x][t];
                                    gridMatrix[x][t] = null;
                                }
                            }
                        }
            }
            //use the floodFill algorithm and create a subGrid above the line we last cleared, which is used to find coordinates of connectedShapes
            Color[][] subGrid = FloodFill.getSubGrid(gridMatrix);
            //we get the coordinates from getConnectedShapes
            ArrayList<ArrayList<Integer>> connectedShapes = FloodFill.getConnectedShapes(subGrid);
            /* 
            The while loop will iterate as long as any shape has moved
            */
            boolean hasMoved = true;
            while(hasMoved == true){
                hasMoved = false;
                //go through each shape and move it down as much as it can
                for(int c = 0; c < connectedShapes.size(); c++){
                        //initialize starting column to check
                        int x = 0;        
                        //initialize starting row to check
                        int y = 0;
                        int moveDown = 16;
                        //make a moveDown variable for the moves this shapes needs to do
                        //while loop that gets through all possible columns and finds minimum moves for each of them
                        while(x < 5){
                            for(int j = 0; j < connectedShapes.get(c).size(); j+=2){
                                if(connectedShapes.get(c).get(j) == x){
                                    //find the biggest Y,(lowest row) in this column to check for moves
                                    for(int p = 0; p < connectedShapes.get(c).size(); p+=2){
                                        if(connectedShapes.get(c).get(p) == x)
                                            y = Math.max(y, connectedShapes.get(c).get(p + 1));
                                    }
                                    //move Y to find until when this column could be moved
                                    int lastPossibleY = y;
                                    while(!doesCollide(x, lastPossibleY)){
                                        lastPossibleY++;
                                    }
                                    int move = lastPossibleY - y;
                                    //store the minimum moves of all possible columns of this shape
                                    moveDown = Math.min(moveDown, move);
                                    //check for next column
                                }
                            }    
                            x++;
                            y = 0;
                        }
                        //a temporal grid is used to move the shape
                        Color[][] tempGrid = new Color[HEIGHT][WIDTH];
                        for(int j = 0; j < connectedShapes.get(c).size(); j+=2){
                            int yCoordinate = connectedShapes.get(c).get(j + 1);
                            int xCoordinate = connectedShapes.get(c).get(j);
                                tempGrid[yCoordinate + moveDown][xCoordinate] = gridMatrix[yCoordinate][xCoordinate];
                                gridMatrix[yCoordinate][xCoordinate] = null;
                        }
                        for(int j = 0; j < connectedShapes.get(c).size(); j+=2){
                            int yCoordinate = connectedShapes.get(c).get(j + 1);
                            int xCoordinate = connectedShapes.get(c).get(j);
                            gridMatrix[yCoordinate + moveDown][xCoordinate] = tempGrid[yCoordinate + moveDown][xCoordinate];
                            connectedShapes.get(c).set(j + 1, connectedShapes.get(c).get(j + 1) + moveDown);
                        }
                        //if moveDown of any shape is not equal to 0 then the shape is has been moved, so we need to check if it is floating
                        if(moveDown != 0){
                            hasMoved = true;
                        }
                    }
                }
            rowsToClear = rowsToClear();
        }
    }
    
    public void testPrint() {
    	int[][] testPentominoCoords = fallingBlock.getCoordinates();
    	System.out.println("The coordinates of the falling Pentomino:");
    	for(int i = 0; i < testPentominoCoords.length; i++) {
    		for(int j = 0; j < testPentominoCoords[i].length;j++) {
    			System.out.print(testPentominoCoords[i][j]);
    		}
    		System.out.println();
    	}
    	
    	for(int i = 0; i < gridMatrix.length;i++) {
    		for (int j = 0; j < gridMatrix[0].length; j++) {
    			System.out.print(" "+gridMatrix[i][j]+" ");
    		}
    		System.out.println();
    	}
    }
    
    public int[][] multiplyMatrix(int[][] matrix1, int[][] matrix2){
		int[][] matrix = new int[matrix1.length][matrix2[0].length];
		if (matrix1[0].length != matrix2.length){
			System.out.println("The sum is illegal - widths or lengths of matrices don't match!");
		}
		
		for(int matrixRow = 0;matrixRow<matrix1.length;matrixRow++){
			for(int matrixCell1 =0;matrixCell1<matrix1[0].length;matrixCell1++){
				for(int matrixColumn = 0;matrixColumn<matrix2[0].length;matrixColumn++){
					int partialMultiplication = 0;
					for(int matrixCell2 = 0; matrixCell2<matrix2.length;matrixCell2++){
						partialMultiplication += matrix1[matrixRow][matrixCell2]*matrix2[matrixCell2][matrixColumn];					
					}
					matrix[matrixRow][matrixColumn] = partialMultiplication;
				}
			} 
		}
		return matrix;
	}
}
