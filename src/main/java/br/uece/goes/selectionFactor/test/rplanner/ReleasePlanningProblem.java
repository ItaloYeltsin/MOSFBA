package br.uece.goes.selectionFactor.test.rplanner;

import br.uece.goes.selectionFactor.Evaluator;
import br.uece.goes.selectionFactor.ProblemWithSelectionFactor;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.InstanceReader;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Release Planning Problem Class
 *
 * @author Thiago Nascimento
 * @since 2014-07-30
 * @version 1.0
 *
 */
public class ReleasePlanningProblem extends ProblemWithSelectionFactor {

    protected int[] risk;

    protected int[] cost;

    private int[] satisfaction;

    private int[][] customerSatisfaction;

    private int[] customerImportance;

    protected int releases;

    protected int requirements;

    protected int customers;

    protected int[] releaseCost;

    protected InstanceReader reader;

    private String filename;

    private String simulator;

    private String scenario;

    private int[][] precedence;

    private double alpha = 1; // feedback weight

    private String[] reqDescriptions;

    public int[][] getPrecedence() {
        return precedence;
    }

    public String getFilename() {
        return filename;
    }

    public String getScenario() {
        return scenario;
    }

    public void setSimulator(String simulator) {
        this.simulator = simulator;

    }

    public ReleasePlanningProblem(String filename) throws IOException {
        this.filename = filename;
        this.reader = new InstanceReader(filename);

        reader.open();

        readParameters();
        readCustomerImportance();
        readRiskAndCost();
        readCustomerSatisfaction();
        readReleaseCost();
        precedence = reader.readIntMatrix(requirements, requirements, " ");
        readDescriptions();
        reader.close();

        problemName_ = "ReleasePlanningProblem";
        numberOfVariables_ = getRequirements();
        numberOfObjectives_ = 1;
        numberOfConstraints_ = 1;

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        for (int i = 0; i < numberOfVariables_; i++) {
            upperLimit_[i] = getReleases();
            lowerLimit_[i] = 0;
        }

        try {
            solutionType_ = new IntSolutionType(this);
        } catch (Exception e) {
            System.out.println(e);
        }

        /* Satisfaction */
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{
                double solutionScore = 0;
                solutionScore = calculateScore(solution);

                return -solutionScore;
            }
        });

        /* Risk */
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{
                double solutionRisk = 0;
                solutionRisk = calculateRisk(solution);

                return solutionRisk;
            }
        });

        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException {

                return evaluatePrecedences(solution);
            }
        }, 50);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        double solutionScore = 0;
        double solutionRisk = 0;
        solutionScore = calculateScore(solution);
        solutionRisk = calculateRisk(solution);
        double utility = (solutionScore - solutionRisk)/(evaluatePrecedences(solution)+1);

        solution.setObjective(0, -utility ); // objective
    }

    private void readDescriptions() {
        reqDescriptions = new String[requirements];

        for (int i = 0; i < reqDescriptions.length; i++) {
            reqDescriptions[i] = i+". "+reader.readLine();
        }

    }

    private void readRiskAndCost() {
        this.risk = new int[requirements];
        this.cost = new int[requirements];

        int[][] info = reader.readIntMatrix(requirements, 2, " ");

        for (int i = 0; i < requirements; i++) {
            this.cost[i] = info[i][0];
            this.risk[i] = info[i][1];
        }
    }

    private void readReleaseCost() {
        this.releaseCost = reader.readIntVector(" ");
    }

    public void readCustomerSatisfaction() {
        this.setSatisfaction(new int[requirements]);
        this.setCustomerSatisfaction(reader.readIntMatrix(customers,
                requirements, " "));

        for (int i = 0; i < requirements; i++) {
            for (int j = 0; j < customers; j++) {
                getSatisfaction()[i] += getCustomerImportance()[j]
                        * getCustomerSatisfaction()[j][i];
            }
        }
    }

    private void readParameters() {
        int[] params = reader.readIntVector(" ");

        this.releases = params[0];
        this.requirements = params[1];
        this.customers = params[2];
    }

    private void readCustomerImportance() {
        this.setCustomerImportance(reader.readIntVector(" "));
    }

    /**
     * Return number of Releases
     *
     * @return number of Releases
     */
    public int getReleases() {
        return releases;
    }

    /**
     * Return number of Requirements
     *
     * @return number of Requirements
     */
    public int getRequirements() {
        return requirements;
    }

    /**
     * Return the Requirement Risk
     *
     * @param i
     *            Requirement ID
     * @return The Requirement Risk
     */
    public int getRisk(int i) {
        if (i < 0 || i + 1 > requirements) {
            throw new IllegalArgumentException("requirement id not found");
        }

        return risk[i];
    }

    /**
     *
     * @param solution
     * @return The fitness value of a given solution
     * @throws JMException
     */
    public double calculateScore(Solution solution) throws JMException {
        double solutionScore = 0;
        Variable[] individual = solution.getDecisionVariables();

        for (int i = 0; i < getRequirements(); i++) {
            int gene = (int) individual[i].getValue();
            if (gene == 0)
                continue;

            solutionScore += (double) getSatisfaction()[i]
                    * (getReleases() - gene + 1);

        }
        return solutionScore;
    }

    public double calculateRisk(Solution solution) throws JMException {
        double solutionRisk = 0;
        Variable[] individual = solution.getDecisionVariables();

        for (int i = 0; i < getRequirements(); i++) {
            int gene = (int) individual[i].getValue();
            if (gene == 0)
                continue;

            solutionRisk += (double) getRisk(i) * gene;

        }
        return solutionRisk;
    }

    /**
     *
     * @param solution
     * @return the number of broken precedence constraints of a solution
     * @throws JMException
     */
    public int evaluatePrecedences(Solution solution) throws JMException {
        Variable[] variables = solution.getDecisionVariables();
        int counter = 0;
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].getValue() != 0) {
                for (int j = 0; j < variables.length; j++) {
                    if (precedence[i][j] == 1
                            && variables[j].getValue() > variables[i]
                            .getValue())
                        counter++;
                }
            }
        }
        return counter;
    }

    public int[][] getCustomerSatisfaction() {
        return customerSatisfaction;
    }

    public void setCustomerSatisfaction(int[][] customerSatisfaction) {
        this.customerSatisfaction = customerSatisfaction;
    }

    public int[] getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int[] satisfaction) {
        this.satisfaction = satisfaction;
    }

    public int[] getCustomerImportance() {
        return customerImportance;
    }

    public void setCustomerImportance(int[] customerImportance) {
        this.customerImportance = customerImportance;
    }


    public double getCost(int i) {
        return cost[i];
    }

    public double getBudget(int i) {
        return releaseCost[i-1];
    }
} // ReleasePlanningProblem