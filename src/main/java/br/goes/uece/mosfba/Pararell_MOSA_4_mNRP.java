package br.goes.uece.mosfba;

import com.aparapi.Kernel;
import com.aparapi.Range;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.util.JMException;
import jmetal.util.Ranking;

import java.util.Random;
import java.util.Stack;

public class Pararell_MOSA_4_mNRP {


    public static SolutionSet execute(
            final float[] weights, final int[] cost,
            final int[] value, final int[][] precedence_arg,
            final int nOfIterations, Problem problem) {

        final int nOfWeightsConfigurations = weights.length / 2;
        final int[] bestSol = new int[(weights.length / 2) * cost.length / nOfIterations];
        final int[] neighbSol = new int[(weights.length / 2) * cost.length / nOfIterations];
        final int[] currentSol = new int[(weights.length / 2) * cost.length / nOfIterations];
        final int RANDOM_SIZE = 50000;
        final int[] randomInt = new int[RANDOM_SIZE];
        final float[] randomFloat = new float[RANDOM_SIZE];
        final int[] randomIndex = new int[weights.length / 2];
        final int[] allPrecedences = new int[precedence_arg.length*precedence_arg.length];
        makeAllPrecedences(precedence_arg, allPrecedences);
        prepareRandomData(randomInt, randomFloat, randomIndex);
        final int[] passId = new int[]{0};
        final int[] bufferSize = new int[]{nOfWeightsConfigurations / nOfIterations};
        final int[] bestSolCost_ = new int[nOfWeightsConfigurations / nOfIterations];
        final int[] bestSolValue_ = new int[nOfWeightsConfigurations / nOfIterations];
        Kernel kernel = new Kernel() {
            final float T_init = 4000.0f;
            final float alpha = 0.97f;
            final float E = 2.7182818284590452353602874713527f;
            final int niter = 10;

            @Override
            public void run() {
                /**
                 * Simualted Annealing Parameters
                 */
                int index = getGlobalId();
                int indexForRand = randomIndex[passId[0] * bufferSize[0] + index];
                float T = T_init;
                int nOfVariables = cost.length;
                float bestFitness;
                float neighborFitness;
                float currentFitness;
                int neighborCost = 0;
                int neighborValue = 0;
                int currentCost = 0;
                int currentValue = 0;
                int bestSolCost = 0;
                int bestSolValue = 0;
                int bestCost, worstCost;
                int bestValue, worstValue;
                int pos;
                int aux;
                float delta;

                bestSolCost = currentCost;
                bestSolValue = currentValue;

                /**
                 * Initialize Extreme Values
                 */
                bestCost = currentCost;
                bestValue = currentValue;
                worstCost = currentCost;
                worstValue = currentValue;

                /**
                 * Initialize solutions
                 */

                for (int j = 0; j < nOfVariables; j++) {
                    neighbSol[index * nOfVariables + j] = 0;
                    currentSol[index * nOfVariables + j] = 0;
                    bestSol[index * nOfVariables + j] = 0;
                }

                while (T > 0.00001f) {
//                    for (int i = 0; i < niter; i++) {
                    /**
                     * Copy Current to Neighbor
                     */
                    for (int j = 0; j < nOfVariables; j++) {
                        neighbSol[index * nOfVariables + j] = currentSol[index * nOfVariables + j];
                    }
                    neighborCost = currentCost;
                    neighborValue = currentValue;

                    /**
                     * Mutate
                     */
                    indexForRand++;
                    pos = abs(randomInt[indexForRand % randomInt.length]) % nOfVariables;
                    aux = 1 - neighbSol[index * nOfVariables + pos]; // bitflip
                    neighbSol[index * nOfVariables + pos] = aux;

                    neighborCost += aux * cost[pos] - (1 - aux) * cost[pos]; // If aux = 0 subract, else sum;
                    neighborValue += aux * value[pos] - (1 - aux) * value[pos]; // If aux = 0 subract, else sum;

                    for (int j = 0; j < nOfVariables; j++) {
                        if (aux == 0 && allPrecedences[j * nOfVariables + pos] == 1
                                && neighbSol[index * nOfVariables + j] == 1
                                && pos != j) {
                            neighbSol[index * nOfVariables + j] = aux;
                            neighborCost -= cost[j];
                            neighborValue -= value[j];
                        }
                        if (aux == 1 && allPrecedences[pos * nOfVariables + j] == 1
                                && neighbSol[index * nOfVariables + j] == 0
                                && pos != j) {
                            neighbSol[index * nOfVariables + j] = aux;
                            neighborCost += cost[j];
                            neighborValue += value[j];
                        }
                    }

                    //Update Best Metrics Value
                    if (neighborValue > bestValue) bestValue = neighborValue;
                    if (neighborCost < bestCost) bestCost = neighborCost;
                    if (neighborValue < worstValue) worstValue = neighborValue;
                    if (neighborCost > worstCost) worstCost = neighborCost;

                    /**
                     * Evaluate Solutions According to Weights
                     */
                    neighborFitness = evaluate(neighborCost, neighborValue, worstValue, bestValue, worstCost,
                            bestCost, index);
                    currentFitness = evaluate(currentCost, currentValue, worstValue, bestValue, worstCost,
                            bestCost, index);
                    bestFitness = evaluate(bestSolCost, bestSolValue, worstValue, bestValue, worstCost,
                            bestCost, index);

                    delta = currentFitness - neighborFitness;
                    if (delta < 0.0f) {
                        /**
                         * Copy NeighBor to Current
                         */
                        for (int j = 0; j < nOfVariables; j++) {
                            currentSol[index * nOfVariables + j] = neighbSol[index * nOfVariables + j];
                        }
                        currentCost = neighborCost;
                        currentValue = neighborValue;

                        if (neighborFitness > bestFitness) {
                            /**
                             * Copy Neighbor to Best
                             */
                            for (int j = 0; j < nOfVariables; j++) {
                                bestSol[index * nOfVariables + j] = neighbSol[index * nOfVariables + j];
                            }
                            bestSolCost = neighborCost;
                            bestSolValue = neighborValue;
                            bestSolCost_[index] = bestSolCost;
                            bestSolValue_[index] = bestSolValue;
                        }
                    } else {

                        if (randomFloat[indexForRand % randomFloat.length] <= pow(E, -delta / T)) {
                            /**
                             * Copy Current to Neighbor
                             */
                            for (int j = 0; j < nOfVariables; j++) {
                                currentSol[index * nOfVariables + j] = neighbSol[index * nOfVariables + j];
                            }
                            currentCost = neighborCost;
                            currentValue = neighborValue;
                        }
                    }

//                    }
                    T = alpha * T;
                }

            }

            float evaluate(int cost, int value, int worstValue, int bestValue,
                           int worstCost, int bestCost, int index) {
                int valueDenominator = bestValue - worstValue;
                int costDenominator = bestCost - worstCost;

                float normalizedValue = 0;
                float normalizedCost = 0;

                if (valueDenominator != 0)
                    normalizedValue = ((float) (value - worstValue)) / (float) valueDenominator;
                if (costDenominator != 0)
                    normalizedCost = ((float) (cost - worstCost)) / (float) costDenominator;

                return weights[passId[0] * bufferSize[0] + index * 2] * normalizedCost
                        + weights[passId[0] * bufferSize[0] + index * 2 + 1] * normalizedValue;
            }
        };
        kernel.setExplicit(true);
        kernel.put(bestSol);
        kernel.put(neighbSol);
        kernel.put(currentSol);
        kernel.put(randomInt);
        kernel.put(randomFloat);
        kernel.put(allPrecedences);

        SolutionSet front = new SolutionSet(nOfWeightsConfigurations);
        for (passId[0] = 0; passId[0] <= nOfIterations; passId[0]++) {


            if (passId[0] == nOfIterations) {
                if (nOfWeightsConfigurations % nOfIterations > 0) {
                    kernel.put(passId).execute(Range.create(nOfWeightsConfigurations % bufferSize[0]));
                } else {
                    break;
                }
            } else if (passId[0] < nOfIterations) {
                kernel.put(passId);
                kernel.execute(Range.create(bufferSize[0]));
            }
            kernel.get(bestSol);

            SolutionSet aux = generateSolutions(bestSol, bestSolCost_, bestSolValue_, problem);
            front = front.union(aux);

        }
        return front;

    }


    static void prepareRandomData(int[] intRandom, float[] floatRandom, int[] randomIndex) {
        Random r = new Random();
        for (int i = 0; i < intRandom.length; i++) {
            intRandom[i] = r.nextInt();
            floatRandom[i] = r.nextFloat();
        }
        for (int i = 0; i < randomIndex.length; i++) {
            randomIndex[i] = r.nextInt(intRandom.length);
        }
    }

    static int[] getLinearPrecedenceMatrix(int[][] precedence) {
        int size = precedence.length * precedence.length;
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = precedence[i / precedence.length][i % precedence.length];
        }
        return result;
    }

    public static void makeAllPrecedences(int [][] precedence, int [] allPrecedence) {
        for (int i = 0; i < precedence.length; i++) {
            boolean [] alreadyVisited = new boolean[precedence.length];
            boolean exit = false;
            Stack<Integer> toBeVisited = new Stack<Integer>();
            toBeVisited.push(i);
            while (toBeVisited.size() > 0) {
                int element = toBeVisited.pop();
                if(!alreadyVisited[element]) {
                    alreadyVisited[element] = true;
                    for (int j = 0; j < precedence.length; j++) {
                        if(precedence[element][j] == 1) {
                            allPrecedence[precedence.length*i + j] = 1;
                            if(!alreadyVisited[j]) toBeVisited.push(j);
                        }
                    }
                }
            }
        }
    }

    public static SolutionSet generateSolutions(int[] solutions, int [] solCost, int [] solValue, Problem problem) {
        int numberOfWeightConfigurations = solutions.length / problem.getNumberOfVariables();
        SolutionSet result = new SolutionSet(numberOfWeightConfigurations);
        for (int i = 0; i < numberOfWeightConfigurations; i++) {
            Solution s = null;
            try {
                s = new Solution(problem);
            } catch (ClassNotFoundException e) {

            }
            Variable[] v = s.getDecisionVariables();
            for (int j = 0; j < v.length; j++) {
                try {
                    v[j].setValue(solutions[i * problem.getNumberOfVariables() + j]);
                } catch (JMException e) {
                    e.printStackTrace();
                }
            }
            s.setObjective(0, solCost[i]);
            s.setObjective(1, -solValue[i]);

            result.add(s);
        }
        return result;
    }

}
