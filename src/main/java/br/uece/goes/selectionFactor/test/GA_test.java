//  GA_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package br.uece.goes.selectionFactor.test;

import br.uece.goes.selectionFactor.ProblemWithSelectionFactor;
import br.uece.goes.selectionFactor.sample.KnapSackVariant.gGA;
import br.uece.goes.selectionFactor.test.bp.PriorizationProblem;
import br.uece.goes.selectionFactor.test.rplanner.ReleasePlanningProblem;
import jmetal.core.*;
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
public class GA_test {

  //public GA_test() { super("P");}
  public void main(String problemName,String complement,int cont,int cont2) throws JMException, ClassNotFoundException, IOException {


    Problem problem = configProblem(problemName,cont);
    SolutionSet population = configAlgorithm(problemName,problem);

    ProblemWithSelectionFactor problemSelectionFactor = (ProblemWithSelectionFactor) problem;

    double[] absoluteValue = new double[cont2];

    for (int i = 0; i < cont2; i++) {
      absoluteValue[i] = problemSelectionFactor.getEvaluator(i).evaluate(population.get(0));
      System.out.println(absoluteValue[i]);
    }

        /* Log messages */
    new File("results").mkdirs();
    population.printObjectivesToFile("results/"+problemName+complement+"_FUN.txt",absoluteValue);
    population.printVariablesToFile("results/"+problemName+complement+"_VAR.txt");

  } //main


  Problem configProblem(String problemName,int index) throws IOException {

    Problem problem=null ;
    ProblemWithSelectionFactor problemSelectionFactor;         // The problem to solve

    if(problemName=="Priorization"){
      problem = new PriorizationProblem("dataset_inst100.txt");

    }else if(problemName=="ReleasePlanning"){
      problem = new ReleasePlanningProblem("src\\instances\\data-set-1.rp");
    }

    problemSelectionFactor = (ProblemWithSelectionFactor) problem;

    //int t = problemSelectionFactor.getNumberOfEvaluation();
    //System.out.println("numero de avaliadores:::"+t);

    try{
    if(index==0){
      problemSelectionFactor.removeEvaluator(1);
      //problemSelectionFactor.removeEvaluator(1);
    }

    if(index==1){
      problemSelectionFactor.removeEvaluator(0);
      //problemSelectionFactor.removeEvaluator(1);
    }

    if(index==2){
      problemSelectionFactor.removeEvaluator(0);
      problemSelectionFactor.removeEvaluator(0);
    }

    }catch (Exception e){
      System.out.println(e);
    }

//      for (int i = 0; i < t; i++) {
//          try{
//              if(i!=index && index!=-1) {
//                System.out.println("removeu:::"+i);
//                  problemSelectionFactor.removeEvaluator(i);
//              }
//          }catch (Exception e){
//              System.out.println(e+":::tentou remover:::"+i+"index::: "+index);
//          }
//
//      }

    return problem;

  }

  SolutionSet configAlgorithm(String problemName,Problem problem) throws JMException, ClassNotFoundException {

    Algorithm algorithm =null;         // The algorithm to use
    Operator crossover =null ;         // Crossover operator
    Operator mutation =null ;         // Mutation operator
    Operator  selection;         // Selection operator
    HashMap  parameters ; // Operator parameters

    algorithm = new gGA(problem) ; // Generational GA

        /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize", 100);
    algorithm.setInputParameter("maxEvaluations", 100000);

    if(problemName=="ReleasePlanning") {

      // Mutation and Crossover for Binary codification
      parameters = new HashMap();
      parameters.put("probability", 0.9);
      crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover", parameters);

      parameters = new HashMap();
      parameters.put("probability", 0.01);
      mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);

    } else if(problemName=="Priorization"){

      // Mutation and Crossover for Binary codification
      parameters = new HashMap();
      parameters.put("probability", 0.9);
      crossover = CrossoverFactory.getCrossoverOperator("OrderOneCrossover", parameters);

      parameters = new HashMap();
      parameters.put("probability", 0.01);
      mutation = MutationFactory.getMutationOperator("RankSwapMutation", parameters);

    }

    /* Selection Operator */
    parameters = null;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

    /* Add the operators to the algorithm*/
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);


    return algorithm.execute();


  }


  /**
   * This class runs a single-objective genetic algorithm (GA). The GA can be
   * a steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
   * cGA (class scGA) or an asynchronous cGA (class acGA). The OneMax
   * problem is used to test the algorithms.
   */
  public static class RPFactorTest extends Teste{

    public RPFactorTest() {
      super("ReleasePlanning");
    }

    @Override
    protected Problem configProblem(){
      try {
        return new ReleasePlanningProblem("src"+File.separator+"instances"+File.separator+"data-set-1.rp");
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

      algorithm = new jmetal.metaheuristics.singleObjective.geneticAlgorithm.gGA(problem); // Generational GA

          /* Algorithm parameters*/
      algorithm.setInputParameter("populationSize", 100);
      algorithm.setInputParameter("maxEvaluations", 1000);

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
} // GA_main
