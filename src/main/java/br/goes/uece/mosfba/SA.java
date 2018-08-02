package br.goes.uece.mosfba;
import com.aparapi.Kernel;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.util.JMException;

import java.util.Random;

public class SA extends Kernel{

    final float [] weights;
    final int nOfVariables;
    final int numberOfWeightConfigurations; // Bi-dimensional Abstraction
    final float E = (float)Math.E;
    final float T_init = 200.0f;
    final int niter = 100;

    final int COST = 0, VALUE = 1;

    final float alpha = 0.98f;
    final int RANDOM_SIZE = 50000;
    final int [] randomInt = new int[RANDOM_SIZE];
    final float [] randomdouble = new float[RANDOM_SIZE];
    final int solutionSetSize; // current, best, neighbor
    final int [] solutions;
    final int numberOfObjectives;
    final int CURRENT = 0;
    final int NEIGHBOR = 1;
    final int BEST = 2;
    final int [] precedence;
    final int [] best;
    final int [] worst;
    final int upperBound;
    final int [] value;
    final int [] cost;
    final int [] randomIndex;
    final int [] l;
    public SA(Problem problem, float [] weights_params) {
        weights = weights_params;
        nOfVariables = problem.getNumberOfVariables();
        numberOfWeightConfigurations = weights.length/problem.getNumberOfObjectives(); // Bi-dimensional Abstraction
        solutionSetSize = 3*nOfVariables; // current, best, neighbor
        solutions = new int[solutionSetSize*numberOfWeightConfigurations];
        randomIndex = new int[numberOfWeightConfigurations];
        l = new int[numberOfWeightConfigurations];
        /**
         * MNRP
         */
        numberOfObjectives = problem.getNumberOfObjectives();
        // 3 solutions per weights config
        best = new int[numberOfWeightConfigurations*problem.getNumberOfObjectives()]; // Stores best values
        worst = new int[numberOfWeightConfigurations*problem.getNumberOfObjectives()]; // Store worst values

        //Random Numbers
        Random r = new Random();
        for (int j = 0; j < RANDOM_SIZE; j++) {
            randomInt[j] = r.nextInt();
            randomdouble[j] = (float)r.nextDouble();
        }

        for (int i = 0; i < numberOfWeightConfigurations; i++) {
            randomIndex[i] = r.nextInt(RANDOM_SIZE);
        }

        upperBound = (int) problem.getUpperLimit(0);

        MNRP mNRP = (MNRP)problem;
        precedence = new int[nOfVariables*nOfVariables];

        for (int i = 0; i < nOfVariables; i++) {
            for (int j = 0; j < nOfVariables; j++) {
                precedence[i*nOfVariables+j] = mNRP.getPrecedence()[i][j];
            }
        }

        value = new int[mNRP.getValue().length];
        cost = new int[mNRP.getCost().length];

        for (int i = 0; i < value.length; i++) {
            value[i] = mNRP.getValue()[i];
            cost[i] = mNRP.getCost()[i];
        }

        //createInitialSolution(problem);

        /*setExplicit(true);
        put(randomIndex);
        put(weights);
        put(solutions);
        put(best);
        put(worst);
        put(precedence);
        put(randomdouble);
        put(randomInt);
        put(cost);
        put(value);*/

        //kernel.get(solutions);
    }

    public void mutate(int solution, int index) {
        int aux, i;
        i = abs(nextInt(index)) % nOfVariables;

        aux = solutions[solutionSetSize*index + solution*nOfVariables + i];
        solutions[solutionSetSize*index + solution*nOfVariables + i] = 1 - aux; // flip mutation

    }
    @Override
    public void run() {
//        System.out.println();
        int index = getGlobalId();
        createSolution(CURRENT, index);
        float bestFitness = 0;
        float neighborFitness = 0;
        float currentFitness = 0;
        int nOfBrokenDepBest = 0;
        int nofBrokenDepCurrent = 0;
        int nOfBrokenDepNeighbor = 0;
        float delta;
        float T = T_init;
        int cost = getCost(CURRENT, index);
        int value = getValue(CURRENT, index);
        setWorst(COST, index, cost);
        setBest(COST, index, cost);
        setWorst(VALUE, index, value);
        setBest(VALUE, index, value);
        copySolution(CURRENT, BEST, index);
        //nOfBrokenDepBest = evaluatePrecedences(BEST, index);
        while(T > 0.001f) {
            for (int i = 0; i < niter; i++) {
                copySolution(CURRENT, NEIGHBOR, index);
                mutate(NEIGHBOR, index);
                updateBestAndWorstValues(index);

                //nofBrokenDepCurrent = evaluatePrecedences(CURRENT, index);
                //nOfBrokenDepNeighbor = evaluatePrecedences(NEIGHBOR, index);

//                bestFitness = evaluate(BEST, index)/**(float)(1+nOfBrokenDepBest)*/;
//                neighborFitness = evaluate(NEIGHBOR, index)/**(float)(1+nOfBrokenDepNeighbor)*/;
//                currentFitness = evaluate(CURRENT, index)/**(float)(1+nofBrokenDepCurrent)*/;
////
//                delta = neighborFitness - currentFitness;
                delta = 0;
//                if(delta < .0f) {
//                    copySolution(NEIGHBOR, CURRENT, index);
//                    if(neighborFitness < bestFitness) {
//                        copySolution(NEIGHBOR, BEST, index);
////                       nOfBrokenDepBest = evaluatePrecedences(BEST, index);
//                    }
//                }
//              else {
//
//                    if(nextDouble(index) <= pow(E, -delta/T)) {
//                        //currentSolution = neighbor;
//                        copySolution(NEIGHBOR, CURRENT, index);
//                    }
//                }

            }
            T = alpha*T;
        }
    }

    /*public SolutionSet execute() {
        try {
            NativeLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return generateSolutions(problem);
    }*/

    void createInitialSolution(Problem problem) {
        Random r = new Random();
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = r.nextInt(2);
        }
    }

    void createSolution(int solution, int index) {
        for (int i = 0; i < nOfVariables; i++) {
            solutions[solutionSetSize*index + nOfVariables*solution + i] = abs(nextInt(index))%2;
        }
    }

    public SolutionSet generateSolutions (Problem problem) {
        get(solutions);
        SolutionSet result = new SolutionSet(numberOfWeightConfigurations);
        for (int i = 0; i < numberOfWeightConfigurations; i++) {
            Solution s = null;
            try {
                s = new Solution(problem);
            }catch (ClassNotFoundException e) {

            }
            Variable [] v = s.getDecisionVariables();
            for (int j = 0; j < v.length; j++) {
                try {
                    v[j].setValue(solutions[i*solutionSetSize+CURRENT*nOfVariables+j]);
                } catch (JMException e) {
                    e.printStackTrace();
                }
            }

            try {
                problem.evaluate(s);
            } catch (JMException e) {
                e.printStackTrace();
            }

            result.add(s);
        }
        return result;
    }

    void copySolution(int from, int to, int index) {
        int j = (index*solutionSetSize) + (to*nOfVariables);
        for (int i = (index*solutionSetSize) + (from*nOfVariables);
             i < ((index*solutionSetSize) + (from*nOfVariables)) + nOfVariables; i++) {
            solutions[j] = solutions[i];
            j += 1;
        }
    }

    float nextDouble(int index) {
        float value = randomdouble[randomIndex[index]%randomdouble.length];
        randomIndex[index] += 1;
        return value;
    }

    int nextInt(int index) {
        int value = randomInt[randomIndex[index]%randomdouble.length];
        randomIndex[index] += 1;
        return value;
    }

    /**
     *
     * @param solution
     * @param index
     * @return
     */
    float evaluate(final int solution, final int index) {

        //int cost = getCost(solution, index);
        //int value = getValue(solution, index);
        //int cost = 10;
        //int value = 10;
//        int bestCost = getBest(COST, index);
//        int worstCost = getWorst(COST, index);
//        int bestValue = getBest(VALUE, index);
//        int worstValue = getWorst(VALUE, index);
//
        //int bestCost = 5;
        //int worstCost = 20;
        //int bestValue = 20;
        //int worstValue = 5;

        //int costDenominator = bestCost - worstCost;
        //int valueDenominator = bestValue - worstValue;

        //float normalizedCost = 1;
        //float normalizedValue = 1;

//        if(costDenominator !=0) {
//            normalizedCost = 1 - (float)(cost - worstCost)/(float)costDenominator;
//        }
//        if(valueDenominator != 0) {
//            normalizedValue = 1 - (float)(value - worstValue)/(float)valueDenominator;
//        }

//        float result = weights[index*numberOfObjectives+COST]*normalizedCost
//                + weights[index*numberOfObjectives+VALUE]*normalizedValue;
        //float result = 0;
        return 0;
    }

    /**
     *
     * @param att
     * @param index
     * @param value
     */
    void setBest(int att, int index, int value) {
        best[index*numberOfObjectives+att] = value;
    }

    /**
     *
     * @param att
     * @param index
     * @return
     */
    int getBest(int att, int index) {
        return best[index*numberOfObjectives+att];
    }

    /**
     *
     * @param att
     * @param index
     * @param value
     */
    void setWorst(int att, int index, int value) {
        worst[index*numberOfObjectives+att] = value;
    }

    /**
     *
     * @param att
     * @param index
     * @return
     */
    int getWorst(int att, int index) {
        return worst[index*numberOfObjectives+att];
    }

    int getCost (final int solution, final int index) {
        int costSum = 0;
        for (int i = 0; i < nOfVariables; i++) {
            costSum = costSum + solutions[index*solutionSetSize + solution*nOfVariables+i]*cost[i];
        }
        return costSum;
    }

    int getValue (final int solution, final int index) {
        int valueSum = 0;
        for (int i = 0; i < nOfVariables; i++) {
            valueSum = valueSum + solutions[index*solutionSetSize + solution*nOfVariables+i]*value[i];
        }
        return valueSum;
    }

    void updateBestAndWorstValues(int index) {
        int currentSolCost = getCost(CURRENT, index);
        int currentSolValue = getValue(CURRENT, index);

        int bestSolCost = getCost(BEST, index);
        int bestSolValue = getValue(BEST, index);

        int neighSolCost = getCost(NEIGHBOR, index);
        int neighSolValue = getValue(NEIGHBOR, index);

        // Costs
        if(currentSolCost > getWorst(COST, index)) setWorst(COST, index, currentSolCost);
        if(bestSolCost > getWorst(COST, index)) setWorst(COST, index, bestSolCost);
        if(neighSolCost > getWorst(COST, index)) setWorst(COST, index, neighSolCost);

        if(currentSolCost < getBest(COST, index)) setBest(COST, index, currentSolCost);
        if(bestSolCost < getBest(COST, index)) setBest(COST, index, bestSolCost);
        if(neighSolCost < getBest(COST, index)) setBest(COST, index, neighSolCost);

        //Values
        if(currentSolValue > getBest(VALUE, index)) setBest(VALUE, index, currentSolValue);
        if(bestSolValue > getBest(VALUE, index)) setBest(VALUE, index, bestSolValue);
        if(neighSolValue > getBest(VALUE, index)) setBest(VALUE, index, neighSolValue);

        if(currentSolValue < getWorst(VALUE, index)) setWorst(VALUE, index, currentSolValue);
        if(bestSolValue < getWorst(VALUE, index)) setWorst(VALUE, index, bestSolValue);
        if(neighSolValue < getWorst(VALUE, index)) setWorst(VALUE, index, neighSolValue);
    }

//    int evaluatePrecedences(int solution, int index) {
//        int count = 0;
//        int req1;
//        int req2;
//        int valor1;
//        int valor2;
//
//        for (int i = 0; i < precedence.length; i++) {
//            req1 = i / nOfVariables;
//            req2 = i % nOfVariables;
//            valor1 = solutions[index * solutionSetSize + solution * nOfVariables + req1];
//            if(valor1 == 0) {
//                i = (req1 + 1)*nOfVariables;
//            }
//            else {
//                valor2 = solutions[index * solutionSetSize + solution * nOfVariables + req2];
//                if(precedence[i] == 1 && valor2 == 0) {
//                    count+= 1;
//                }
//            }
//        }
//        return count;
//    }
}
