package br.goes.uece.mosfba;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;

import java.io.File;

public class MNRP extends Problem {

    int cost[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int value[] = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    int risk [];
    int requirements;
    double releaseCost;
    String [] reqDescriptions;
    InstanceReader reader;
    int precedence [][];
    private int nOfcustomers;
    private int[] customerImportance;

    public MNRP(String instance) {

        this.reader = new InstanceReader(new File("instances/"+instance));
        problemName_ = "mNRP_"+instance;

        value = getRequirementsScore(reader.getCustomersImportance(), reader.getRequirementsImportances());
        cost = reader.getRequirementsCosts();
        precedence = reader.getPrecendenceMatrix();

        numberOfObjectives_ = 2;
        numberOfVariables_ = cost.length;
        solutionType_ = new IntSolutionType(this);

        lowerLimit_ = new double[cost.length];
        upperLimit_ = new double[cost.length];

        for (int i = 0; i < cost.length; i++) {
            lowerLimit_[i] = 0;
            upperLimit_[i] = 1;
        }



    }

    /**
     * Calculate cost of each requirement
     * @param customersImportance
     * @param requirementsImportance
     * @return
     */
    public int[] getRequirementsScore(double[] customersImportance,
                                         double[][] requirementsImportance) {
        int[] requirementsScore = new int[requirementsImportance[0].length];

        for (int i = 0; i < customersImportance.length; i++) {
            for (int j = 0; j < requirementsImportance[0].length; j++) {
                requirementsScore[j] += customersImportance[i]
                        * requirementsImportance[i][j];
            }
        }

        return requirementsScore;
    }

    public int[] getCost() {return cost;}

    public int[] getValue() {return value;}

    public int [] [] getPrecedence() {return precedence;}
    public void evaluate(Solution solution) throws JMException {
        Variable [] v = solution.getDecisionVariables();

        double cost = 0, value = 0;

        for (int i = 0; i < v.length; i++) {
            cost += v[i].getValue()*this.cost[i];
            value += v[i].getValue()*this.value[i];
        }
        int nOfBrokenDependences = evaluatePrecedences(solution);

        if(nOfBrokenDependences > 0) {
            cost = cost*nOfBrokenDependences*100000000;
            value = value/(nOfBrokenDependences*10000000);
        }

        solution.setObjective(0, cost);
        solution.setObjective(1, -value);
    }

    public int evaluatePrecedences(Solution solution) throws JMException {
        int count = 0;
        for (int i = 0; i < precedence.length; i++) {
            Variable req1 = solution.getDecisionVariables()[i];

            if(req1.getValue() == 0) continue;

            for (int j = 0; j < precedence.length; j++) {
                Variable req2 = solution.getDecisionVariables()[j];
                if(precedence[i][j] == 1 && req2.getValue() == 0) {
                    count++;
                }
            }

        }
        return count;
    }
}
