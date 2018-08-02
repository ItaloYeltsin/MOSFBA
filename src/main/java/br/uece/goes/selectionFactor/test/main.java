package br.uece.goes.selectionFactor.test;

import br.uece.goes.selectionFactor.test.bp.BPFactorTest;
import br.uece.goes.selectionFactor.test.bp.BPFitnessTest;
import br.uece.goes.selectionFactor.test.bp.BPMinMaxTest;
import br.uece.goes.selectionFactor.test.rplanner.RPFactorTest;
import br.uece.goes.selectionFactor.test.rplanner.RPFitnessTest;
import br.uece.goes.selectionFactor.test.rplanner.RPMinMaxTest;
import jmetal.util.JMException;

import java.io.IOException;

/**
 * Created by Raphael on 26/05/2017.
 */
public class main {

    public static void main(String[] args) throws ClassNotFoundException, IOException, JMException {
        //Teste test = new BPFitnessTest();
        //Teste test = new BPFactorTest();
        Teste test = new BPMinMaxTest();


        test.execute(30);


    }

}
