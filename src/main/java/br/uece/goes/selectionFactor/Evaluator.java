package br.uece.goes.selectionFactor;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

/**
 * Created by italo on 20/05/17.
 */


public abstract class Evaluator{

    Problem problem_;

    public Evaluator(Problem problem_) {
        this.problem_ = problem_;
    }

    public abstract double evaluate(Solution solution) throws JMException;

}
