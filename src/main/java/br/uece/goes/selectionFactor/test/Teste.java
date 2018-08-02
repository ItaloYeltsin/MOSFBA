package br.uece.goes.selectionFactor.test;

import br.uece.goes.selectionFactor.ProblemWithSelectionFactor;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

import java.io.*;

/**
 * Created by italo on 09/06/17.
 */
public abstract class Teste {

    protected String problemName;
    protected Problem problem;
    protected String instance = "";
    protected Algorithm algorithm;
    protected final int MAX_NUMBER_OF_EVALUATIONS = 100000;
    protected final int POPULATION_SIZE = 100;

    protected Teste(String problemName) {
        this.problemName = problemName;
    }

    public void execute(int evaluations) throws JMException, IOException {
        problem = configProblem();
        algorithm = configAlgorithm();

        SolutionSet population = null;
        ProblemWithSelectionFactor problemSelectionFactor = (ProblemWithSelectionFactor) problem;
        new File("results").mkdirs();
        FileOutputStream fos   = new FileOutputStream("results"+File.separator+problemName+"_"+instance+".txt")     ;
        OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
        BufferedWriter bw      = new BufferedWriter(osw)        ;

        for (int i = 0; i < evaluations; i++) {

            double execTime = System.currentTimeMillis();

            try {
                population = algorithm.execute();
            } catch (JMException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            execTime = System.currentTimeMillis() - execTime;

            double [] value = new double[problemSelectionFactor.getNumberOfEvaluators()+1];
            value[value.length-1] = execTime;

            for (int j = 0; j < value.length-1; j++) {
                try {
                    value[j] = problemSelectionFactor.getEvaluator(j).evaluate(population.get(0));
                    bw.write(value[j]+" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(value[j]);
            }
            bw.write(value[value.length-1]+""+System.lineSeparator());
        /* Log messages */

        }
        bw.close();
    }

























    protected abstract Problem configProblem();

    protected abstract Algorithm configAlgorithm() throws JMException;
}
