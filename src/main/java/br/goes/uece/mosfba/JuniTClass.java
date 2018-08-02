package br.goes.uece.mosfba;

import jmetal.core.Problem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JuniTClass {
    MOSFA mosfa;
    MNRP problem;
    Pararell_MOSA_4_mNRP pmm;
    int [][] precedence;
    @Before
    public void initialize() {
        problem = new MNRP("dataset-1.rp");
        mosfa = new MOSFA(10000, 100, problem);
        precedence = problem.getPrecedence();
    }
    @Test
    public void testPmm() {
        int [] precResult = Pararell_MOSA_4_mNRP.getLinearPrecedenceMatrix(precedence);
        for (int i = 0; i < precedence.length; i++) {
            for (int j = 0; j < precedence.length; j++) {
                System.out.println(i + " " + j);
                Assert.assertEquals(precResult[i*precedence.length + j], precedence[i][j]);
            }
        }
    }

}
