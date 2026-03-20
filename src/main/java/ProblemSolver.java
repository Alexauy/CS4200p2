import java.util.*;

public class ProblemSolver {
    private int hillClimbSteps = 0;
    private long hillClimbTime = 0;
    private int hillClimbNodes = 0;

    public BoardState hillClimbing(BoardState initial){
        int steps = 0;
        long start = System.currentTimeMillis();

        BoardState current = new BoardState(initial.getQueens());
        int currentConflicts = current.checkAttackability();

        //>>> ChatGPT was used to debug some for loop logic
        int numNodes = 0;
        while(true){
            steps++;

            ArrayList<BoardState> neighbors = current.getNeighbors();
            numNodes += neighbors.size();

            BoardState bestNeighbor = null;
            int lowestConflicts = currentConflicts;

            for(int i = 0; i<neighbors.size(); i++) {
                BoardState neighbor = neighbors.get(i);
                int neighborConflicts = neighbor.checkAttackability();

                if (neighborConflicts < lowestConflicts) {
                    lowestConflicts = neighborConflicts;
                    bestNeighbor = neighbor;
                }
            }
            //<<<

                if(bestNeighbor == null){
                    hillClimbSteps = steps;
                    hillClimbTime = System.currentTimeMillis() - start;
                    hillClimbNodes = numNodes;
                    return current;
                }

                current = bestNeighbor;
                currentConflicts = lowestConflicts;

                if(currentConflicts == 0){
                    break;
                }
        }
        hillClimbSteps = steps;
        hillClimbTime = System.currentTimeMillis() - start;
        hillClimbNodes = numNodes;
        return current;
    }

    //Fitness function is measure of non-attacking queen pairs, helper for GA
    public int calcFitness(BoardState state){
        int n = state.getN();
        return (n*(n-1))/2 - state.checkAttackability();
    }

    public BoardState geneticAlgorithm(int n){
        int population = 30;
        int maxGens = 300;
        Random picker = new Random();
        //Randomization will help introduce diversity and avoid getting stuck

        ArrayList<BoardState> pop = new ArrayList<>();
        for(int i = 0; i<population; i++){
            pop.add(new BoardState(n));
        }

        BoardState bestBoard = pop.get(0);

        //Check at the start if any of them are the solution
        for(int gen = 0; gen<maxGens; gen++){
            for(BoardState state : pop){
                int fitnessVal = calcFitness(state);

                if(state.checkAttackability() == 0){
                    return state;
                }

                if(fitnessVal > calcFitness(bestBoard)){
                    bestBoard = state;
                }
            }

            ArrayList<BoardState> newPop = new ArrayList<>();
            //Merge parents from pop together to create new pop with children
            while(newPop.size() < population){
                BoardState aParent;
                BoardState bParent;

                //Choosing the first parent ------------
                BoardState aMom = pop.get(picker.nextInt(pop.size()));
                BoardState aDad = pop.get(picker.nextInt(pop.size()));

                if(calcFitness(aMom) > calcFitness(aDad)){
                    aParent = aMom;
                }else{
                    aParent = aDad;
                }

                //Choosing the second parent ------------
                BoardState bMom = pop.get(picker.nextInt(pop.size()));
                BoardState bDad = pop.get(picker.nextInt(pop.size()));

                if(calcFitness(bMom) > calcFitness(bDad)){
                    bParent = bMom;
                }else{
                    bParent = bDad;
                }
//>>> ChatGPT was used to debug this section
                //Randomly mix the parents together to create a child
                //Experimenting: higher crossover rates improves success
                int[] childQueens = new int[n];

                int[] aParentQ = aParent.getQueens();
                int[] bParentQ = bParent.getQueens();

                for(int i = 0; i<n; i++){
                    if(picker.nextBoolean()){
                        childQueens[i] = aParentQ[i];
                    }else{
                        childQueens[i] = bParentQ[i];
                    }
                }
                //<<<

                BoardState abChild = new BoardState(childQueens);

                //Experimenting: around 4% mutation rate seems to produce satisfactory results
                //Mutate only some percent of the time
                int[] mutated = abChild.getQueens();
                for(int i = 0; i<mutated.length; i++){
                    if(picker.nextDouble(1.0) < 0.04){
                        mutated[i] = picker.nextInt(n);
                    }
                }
                abChild = new BoardState(mutated);

                newPop.add(abChild);
            }

            pop = newPop;
        }

        return bestBoard;
    }


    public static void main(String[] args) {
        int[] dimensions = {8, 12, 16};
        ProblemSolver ps = new ProblemSolver();

        System.out.println("----------Hill-Climbing Algorithm----------");
        for(int n : dimensions){
            long totalTime = 0;
            int totalSteps = 0;
            int totalNodes = 0;

            BoardState sampleSol = null;
            int successes = 0;

            for(int i = 0; i<100; i++){
                BoardState aBoard = new BoardState(n);
                BoardState output = ps.hillClimbing(aBoard);

                int conflicts = output.checkAttackability();

                if(conflicts == 0){
                    successes++;

                    if(sampleSol == null){
                        sampleSol = output;
                    }
                }

                totalTime += ps.hillClimbTime;
                totalSteps += ps.hillClimbSteps;
                totalNodes += ps.hillClimbNodes;
            }

            double avgTime = (double)totalTime/100;
            int avgSteps = totalSteps/100;
            double avgNodes = (double)totalNodes/100;

            //Fills sample solution with a board in the case that the 100 tests
            //does not produce a single viable board
            if(sampleSol == null){
                sampleSol = ps.hillClimbing(new BoardState(n));
            }

            double successRate = (float)successes/100;
            double successPercentage = successRate*100;
            System.out.printf("n: %d | Success: %.2f%% | Avg Steps: %d | Avg Nodes: %.3f | Avg Time: %.3f ms\n", n, successPercentage, avgSteps, avgNodes, avgTime);

            System.out.println("Sample solution for n = " + n);
            sampleSol.printBoard();
            System.out.println();
        }

        //Genetic algo running
        System.out.println("\n----------Genetic Algorithm----------");
        int gaSuccesses = 0;
        for(int n : dimensions){
            BoardState sampleSol = null;
            for(int i = 0; i<20; i++){
                BoardState output = ps.geneticAlgorithm(n);

                if(output.checkAttackability() == 0){
                    gaSuccesses++;

                    if(sampleSol == null){
                        sampleSol = output;
                    }
                }
            }

            if(sampleSol == null){
                sampleSol = ps.geneticAlgorithm(n);
            }

            System.out.println("n: " + n +  " | Successes: " + gaSuccesses + "/20");
            System.out.println("Sample solution for n = " + n);
            sampleSol.printBoard();
            System.out.println();

            gaSuccesses = 0;
        }

    }

}
