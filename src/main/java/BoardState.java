import java.util.*;

public class BoardState {
    private int[] queens;   //Locations of the Queen pieces
    private int n;          //Dimensions of the board

    public BoardState(int n){
        this.n = n;
        queens = new int[n];

        Random randomGen = new Random();
        for(int i = 0; i<n; i++){
            queens[i] = randomGen.nextInt(n);
        }
    }

    public BoardState(int[] queens){
        this.queens = new int[queens.length];

        for(int i = 0; i<queens.length; i++){
            this.queens[i] = queens[i];
        }

        this.n = queens.length;
    }

    public int getN(){
        return n;
    }

    public int[] getQueens(){
        int[] copyQueens = new int[n];

        for(int i = 0; i<n; i++){
            copyQueens[i] = queens[i];
        }

        return copyQueens;
    }

    public int checkAttackability(){
        int susceptible = 0;

        //Compare one queen to each subsequent queen
        //Prevent duplicate pairs
        for(int i = 0; i<queens.length; i++){
            for(int j = i+1; j<queens.length; j++){
                if(queens[i] == queens[j]){
                    susceptible++;
                }

                if(Math.abs(i-j) == Math.abs(queens[i]-queens[j])){
                    susceptible++;
                }
            }
        }

        return susceptible;
    }

    public ArrayList<BoardState> getNeighbors(){
        ArrayList<BoardState> neighbors = new ArrayList<>();

        for(int i = 0; i<n; i++){
            for(int j = 0; j<n; j++){
                if(j == queens[i]){
                    continue;
                }

                int[] neighbor = new int[n];
                for(int k = 0; k<n; k++){
                    neighbor[k] = queens[k];
                }
                neighbor[i] = j;
                BoardState neighborState = new BoardState(neighbor);
                neighbors.add(neighborState);
            }
        }
        return neighbors;
    }

    public void printBoard(){
        for(int i = 0; i<n; i++){
            for(int j = 0; j<n; j++){
                if(queens[i] == j){
                    System.out.print(" Q ");
                }else{
                    System.out.print(" + ");
                }
            }
            System.out.println();
        }
    }
}
