package br.uece.goes.selectionFactor.sample.KnapSackVariant;

import br.uece.goes.selectionFactor.Evaluator;
import br.uece.goes.selectionFactor.ProblemWithSelectionFactor;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.util.JMException;

/**
 * Problem: A thief is going to steal a story where the products
 *          are packaged. Each package contains a label with the
 *          volume, weight and value of the product inside of it.
 *          The thief needs to select the set of most valued pro-
 *          ducts which fit in his/her bag. Another important as-
 *          pect is to select lighter products, in order to faci-
 *          litate his/her escape.
 *
 *          Thus, the problem consists in:
 *
 *          Maxmize the sum of the value of the selected set
 *          Minimize the sum of the weights of the selected set
 *
 *          Subject to:
 *              sum of the volumes <= bag's capacity
 *
 * Created by italo on 20/05/17.
 */
public class KnapSackVariantProblem extends ProblemWithSelectionFactor {

    double [] volume = {5000.0,   20000.0,   12000.0,  3000.0,   15000.0,  30000.0};    //cm³
    double [] weight = {0.04,   10.0,      20.0,     30.0,     3.0,      4.0   };    //Kg
    double [] value =  {10.0,     300000.0,  80.0,     0.125,  0.5,    1.0    };    //U$

    double capacity = 20000;

    public KnapSackVariantProblem() {

        solutionType_ = new IntSolutionType(this);
        numberOfObjectives_ = 1;
        numberOfVariables_ = volume.length;
        problemName_ = "KnapSackVariant";

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        for (int i = 0; i < numberOfVariables_; i++) {
            upperLimit_[i] = 1;
            lowerLimit_[i] = 0;
        }




        //Soft Constraint of the bag capacity
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{
                Variable [] variables = solution.getDecisionVariables();

                double sumOfVolumes = 0;

                for (int i = 0; i < variables.length; i++) {
                    sumOfVolumes += variables[i].getValue()*volume[i];
                }

                return (sumOfVolumes > capacity)? 1 : 0;
            }
        }, 50);

        //Evaluator for Values
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{
                Variable [] variables = solution.getDecisionVariables();

                double sumOfValues = 0;

                for (int i = 0; i < variables.length; i++) {
                    sumOfValues += variables[i].getValue()*value[i];
                }

                return -sumOfValues;
            }
        });

        //Evaluator for Weights
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{
                Variable [] variables = solution.getDecisionVariables();

                double sumOfWeights = 0;

                for (int i = 0; i < variables.length; i++) {
                    sumOfWeights += variables[i].getValue()*weight[i];
                }

                return sumOfWeights;
            }
        });

    }


}
