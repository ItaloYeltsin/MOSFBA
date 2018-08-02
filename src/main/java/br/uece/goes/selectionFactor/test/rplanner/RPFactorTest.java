package br.uece.goes.selectionFactor.test.rplanner;

import br.uece.goes.selectionFactor.test.Teste;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be 
 * a steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
 * cGA (class scGA) or an asynchronous cGA (class acGA). The OneMax
 * problem is used to test the algorithms.
 */
public class RPFactorTest extends Teste {

  public RPFactorTest() {
    super("ReleasePlanning_FACTOR");
    super.instance = "dataset-2";
  }

  @Override
  protected Problem configProblem(){
    try {
      return new ReleasePlanningProblem("src"+File.separator+"instances"+File.separator+instance+".rp");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected Algorithm configAlgorithm() throws JMException {

    Algorithm algorithm;         // The algorithm to use
    Operator crossover =null ;         // Crossover operator
    Operator mutation =null ;         // Mutation operator
    Operator  selection;         // Selection operator
    HashMap  parameters ; // Operator parameters

    algorithm = new FactorGA(problem); // Generational GA

        /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize", super.POPULATION_SIZE);
    algorithm.setInputParameter("maxEvaluations", super.MAX_NUMBER_OF_EVALUATIONS);

    // Mutation and Crossover for Binary codification
    parameters = new HashMap();
    parameters.put("probability", 0.9);
    crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);

    parameters = new HashMap();
    parameters.put("probability", 0.01);
    mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);


    /* Selection Operator */
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

    /* Add the operators to the algorithm*/
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);


    return algorithm;
  }

} // GA_main
