package br.uece.goes.selectionFactor;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import java.util.ArrayList;

/**
 * Created by italo on 19/05/17.
 */
public abstract class ProblemWithSelectionFactor extends Problem {

    protected ArrayList<Evaluator> evaluators_ = new ArrayList<Evaluator>();
    protected ArrayList<Double> weights_ = new ArrayList<Double>();

    public Evaluator getEvaluator(int i) {
        return evaluators_.get(i);
    }

    public void setEvaluator(int index, Evaluator evaluator) {
        evaluators_.set(index, evaluator);
    }

    public void setEvaluatorWeight(int index, double weight) {
        weights_.set(index, weight);
    }

    public int getNumberOfEvaluators() {
        return evaluators_.size();

    }

    public void removeEvaluator(int index) {
        evaluators_.remove(index);
        weights_.remove(index);
    }

    public void addEvaluator(Evaluator evaluator) {
        evaluators_.add(evaluator);
        weights_.add(1.0);
    }

    public void addEvaluator(Evaluator evaluator, double weight) {
        evaluators_.add(evaluator);
        weights_.add(weight);
    }

    public void evaluate(SolutionSet population) {
        double [][] values = new double[evaluators_.size()][population.size()];

        double [] minValues = new double[evaluators_.size()];
        double [] maxValues = new double[evaluators_.size()];

        // Getting absolute metrics values and their Max and Min values
        for (int i = 0; i < evaluators_.size(); i++) {

            Evaluator evaluator = evaluators_.get(i);
            minValues[i] = Double.MAX_VALUE;
            maxValues[i] = -Double.MAX_VALUE;

            for (int j = 0; j < population.size(); j++) {
                try {
                    values[i][j] = evaluator.evaluate(population.get(j));
                }catch (JMException e) {
                    e.printStackTrace();
                }

                if(values[i][j] > maxValues[i])
                    maxValues[i] = values[i][j];

                if(values[i][j] < minValues[i])
                    minValues[i] = values[i][j];
            }


        }

        /**
         * fitness of a solution_j =
         *      for each metric_i
         *          sum (metric_i(solution_j) - minValue_i)/(maxValue_i - minValue_j)
         */

        for (int j = 0; j < population.size(); j++) {
            double normalizedFitness = 0;
            for (int i = 0; i < evaluators_.size(); i++) {
                if (maxValues[i]-minValues[i] == 0)
                    continue;

                normalizedFitness +=
                        weights_.get(i)*(values[i][j]- minValues[i])/(maxValues[i]-minValues[i]);
            }
            population.get(j).setObjective(0, normalizedFitness);
        }


    }

    /**
     * Evaluate option for GPU work where only primitive java data can be handled
     *
     * @param solutions
     */
    public void evaluate (double [] solutions, int of, int n) {

    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        throw new RuntimeException("Cannot use this method" +
                " for classes that extends"+this.getClass().getName());
    }

}