package br.uece.goes.selectionFactor.test.bp;

import br.uece.goes.selectionFactor.test.rplanner.ReleasePlanningProblem;
import jmetal.core.*;
import jmetal.util.JMException;
import jmetal.util.comparators.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

/**
 * The Release Planning Problem Class
 *
 * @author Italo Yeltsin
 * @since 2014-08-01
 * @version 1.0
 *
 */

public class FitnessGA extends Algorithm {

    private static final boolean DEBUG_SHOW_CURRENT_BEST_SOLUTION = false;

    private static final long serialVersionUID = 1L;

    public static final int POPULATION_SIZE = 100;

    public static final int MAX_GENERATIONS = 200;

    public static final double ELITISM_RATE = 0.2;

    public static final int N_GENS = 200;

    public static final int N_FEEDBACK = 2;

    public static final int N_ITERACTIONS = 5;

    protected Random random = new Random(System.currentTimeMillis());

    protected int generation = 0;

    protected SolutionSet population;
    protected SolutionSet offspringPopulation;

    protected Operator mutationOperator;
    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    @SuppressWarnings("rawtypes")
    protected Comparator comparator;

    protected int populationSize = POPULATION_SIZE;
    protected double elitismRate = ELITISM_RATE * populationSize;
    protected int maxGenerations = MAX_GENERATIONS;
    protected int nGens = N_GENS;
    protected int nFeedback = N_FEEDBACK;
    protected int nIteractions = N_ITERACTIONS;

    private Solution nonInteractiveSolution;

    private Solution interactiveSolution;

    /**
     *
     * Constructor Create a new IGA instance.
     *
     * @param problem
     *            Problem to solve.
     */
    public FitnessGA(Problem problem) {
        super(problem);

		/* Algorithm parameters */
        //setInputParameter("populationSize", FitnessGA.POPULATION_SIZE);
        setInputParameter("elitismRate", FitnessGA.ELITISM_RATE);
        //setInputParameter("nGens", FitnessGA.N_GENS);
        //setInputParameter("crossoverRate", 0.9);
        //setInputParameter("mutationRate", 0.01);



    } // GGA

    /**
     * Execute the IGA algorithm
     *
     * @throws JMException
     */
    public SolutionSet execute() throws JMException, ClassNotFoundException {

        comparator = new ObjectiveComparator(0); // Single objective comparator

        // Read the params
        populationSize = ((Integer) this.getInputParameter("populationSize"))
                .intValue();
        //maxGenerations = ((Integer) this.getInputParameter("maxGenerations"))
        //		.intValue();
        elitismRate = (int) (populationSize * ((Double) this
                .getInputParameter("elitismRate")));
        nGens = (int) ((Integer)getInputParameter("maxEvaluations")/populationSize);
        //nFeedback = (int) (this.getInputParameter("nFeedback"));
        //nIteractions = (int) (this.getInputParameter("nIteractions"));
        // Initialize the variables
        population = new SolutionSet(populationSize);
        offspringPopulation = new SolutionSet(populationSize);

        // Read the operators
        mutationOperator = this.operators_.get("mutation");
        crossoverOperator = this.operators_.get("crossover" );
        selectionOperator = this.operators_.get("selection");
        createInitialPopulation();
        executeBy(nGens);
        interactiveSolution = population.get(0);


        SolutionSet resultPopulation = new SolutionSet(1);
        resultPopulation.add(population.get(0));

        return resultPopulation;
    }

    private void createInitialPopulation() throws ClassNotFoundException,
            JMException {
        for (int i = 0; i < populationSize; i++) {
            Solution newIndividual = new Solution(problem_);
            problem_.evaluate(newIndividual);

            population.add(newIndividual);
        }

        // Sort population
        population.sort(comparator);
    }

    public void executeOneGeneration() throws JMException {
        generation++;

        // Copy the best individuals to the offspring population
        for (int i = 0; i < elitismRate; i++) {
            offspringPopulation.add(new Solution(population.get(i)));
        }

        Solution[] offspring;
        // Reproductive cycle
        for (int i = 0; i < (populationSize / 2 - elitismRate / 2); i++) {
            // Selection

            Solution[] parents = new Solution[2];

            parents[0] = (Solution) selectionOperator.execute(population);
            parents[1] = (Solution) selectionOperator.execute(population);

            // Crossover
            offspring = (Solution[]) crossoverOperator.execute(parents);

            // Mutation
            mutationOperator.execute(offspring[0]);
            mutationOperator.execute(offspring[1]);

            // offspring
            // population
            offspringPopulation.add(offspring[0]);
            offspringPopulation.add(offspring[1]);

        } // for

        // The offspring population becomes the new current population
        population.clear();

        for (int i = 0; i < populationSize; i++) {
            problem_.evaluate(offspringPopulation.get(i));
            population.add(offspringPopulation.get(i));
        }
        offspringPopulation.clear();
        population.sort(comparator);

        if (DEBUG_SHOW_CURRENT_BEST_SOLUTION) {
            System.out.println("Generation: " + generation);
            System.out.println("\tBest Value: " + population.get(0).toString());
        }
    }

    public void executeBy(int nGens) throws JMException {
        Solution bestIndividual = population.get(0);
        int count = 0;
        while (count < nGens) {

            executeOneGeneration();
            if(-(double)bestIndividual.getObjective(0) < -(double)population.get(0).getObjective(0)){
                bestIndividual = population.get(0);
                count = 0;
            }
            else
                count++;
        }
    }



} // IGA