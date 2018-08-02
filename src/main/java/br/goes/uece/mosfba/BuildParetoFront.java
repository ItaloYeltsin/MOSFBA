package br.goes.uece.mosfba;

import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.ibea.IBEA;
import jmetal.metaheuristics.ibea.IBEA_test;
import jmetal.metaheuristics.mocell.MOCell;
import jmetal.metaheuristics.mocell.MOCell_main;
import jmetal.metaheuristics.nsgaII.NSGA_Test;
import jmetal.util.JMException;
import jmetal.util.Ranking;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BuildParetoFront {



    public static void main (String [] args) {
        String instance = "dataset-2.txt";
        Problem problem1 = new MNRP(instance);

        NSGA_Test nt = new NSGA_Test(instance);
        MOSFA mosfa = new MOSFA(0.0001, 0.000001, problem1);
        IBEA_test ibea_test = new IBEA_test();
        MOCell_main moCellMain = new MOCell_main();
        SolutionSet paretoFront = new SolutionSet();
        try {
            paretoFront = paretoFront.union(nt.execute(10000));
            paretoFront = paretoFront.union(mosfa.execute());
            paretoFront = paretoFront.union(ibea_test.main(problem1));
            paretoFront = paretoFront.union(moCellMain.execute(problem1));
            paretoFront = new Ranking(paretoFront).getSubfront(0);

            Files.createDirectories(Paths.get("results/"+instance));
            paretoFront.printObjectivesToFile("results/"+instance+"/paretoFront");
            BufferedWriter bw = new BufferedWriter(new FileWriter("results/"+instance+"/result.plot"));

            bw.write("set key bottom box width 2 height 3 opaque"); bw.newLine();
            bw.write("plot 'NSGA_II' using ($1):(-$2) title 'NSGA-II',");
            bw.write("'MOSFBA' using ($1):(-$2) title 'MOSFBA',");
            bw.write("'paretoFront' using ($1):(-$2) title 'Frente de Pareto'" +
                    " lc rgb \"orange\" pointtype 12 ps 2"); bw.newLine();
            bw.write("set ylabel \"Valor\""); bw.newLine();
            bw.write("set xlabel \"Custo\""); bw.newLine();
            bw.write("set key font \",20\""); bw.newLine();
            bw.close();

        } catch (JMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
