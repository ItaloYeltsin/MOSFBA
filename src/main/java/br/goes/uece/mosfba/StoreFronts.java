package br.goes.uece.mosfba;

import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGA_Test;
import jmetal.util.JMException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StoreFronts {
    public static void main(String[] args) throws ClassNotFoundException, IOException, JMException {

        String instance = "dataset-2.txt";
        MNRP problem = new MNRP(instance);

        MOSFA mosfa = new MOSFA(0.0001, 0.000001, problem);
        NSGA_Test nsgaT = new NSGA_Test(instance);

        long initialTime = System.currentTimeMillis();
        SolutionSet mSet = mosfa.execute();
        long elapsedTime = System.currentTimeMillis() - initialTime;
        SolutionSet nSet = nsgaT.execute(elapsedTime);

        Files.createDirectories(Paths.get("results/"+instance));

        mSet.printObjectivesToFile("results/"+instance+"/"+"MOSFBA");
        nSet.printObjectivesToFile("results/"+instance+"/NSGA_II");
    }
}
