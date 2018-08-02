package br.goes.uece.mosfba;

import com.aparapi.Kernel;
import com.aparapi.Range;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGA_Test;
import jmetal.qualityIndicator.GenerationalDistance;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.Spread;
import jmetal.util.JMException;

import java.io.*;

public class Test {
    static Hypervolume hv = new Hypervolume();
    static GenerationalDistance gd = new GenerationalDistance();
    static Spread sp = new Spread();

    public static void main(String[] args) throws ClassNotFoundException, IOException, JMException {

        String instance = "dataset-2.txt";
        int evaluations = 100;
        BufferedWriter bw = new BufferedWriter(new FileWriter("results/"+instance+"/metrics", false));
        bw.write("m_hvr\tm_gd\tm_sp\tn_hvr\tn_gd\tn_sp\telapsedTime");
        bw.newLine();
        MNRP problem = new MNRP(instance);


        double  [][] paretoFront = objectivesParetoFrontArray(instance);

        MOSFA mosfa = new MOSFA(1, 0.01, problem);
        NSGA_Test nsgaT = new NSGA_Test(instance);
        for (int i = 0; i < evaluations; i++) {
            long initialTime = System.currentTimeMillis();
            SolutionSet mSet = mosfa.execute();
            long elapsedTime = System.currentTimeMillis() - initialTime;
            SolutionSet nSet = nsgaT.execute(elapsedTime);

            double [][] mosfaObjMatrix = mSet.writeObjectivesToMatrix();
            double mosfaHvr = hv.hypervolume(mosfaObjMatrix, paretoFront, 2);
            double mosfaGD = gd.generationalDistance(mosfaObjMatrix, paretoFront, 2);
            double mosfaSP = sp.spread(mosfaObjMatrix, paretoFront, 2);

            double [][] nsgaObjMatrix = nSet.writeObjectivesToMatrix();
            double nsgaHvr = hv.hypervolume(nsgaObjMatrix, paretoFront, 2);
            double nsgaGD = gd.generationalDistance(nsgaObjMatrix, paretoFront, 2);
            double nsgaSP = sp.spread(nsgaObjMatrix, paretoFront, 2);

            bw.write(mosfaHvr+" "+mosfaGD+" "+mosfaSP+" "
                    +nsgaHvr+" "+nsgaGD+" "+nsgaSP+" "+elapsedTime);
            bw.newLine();
            System.out.println("eval: "+(i+1));

        }

        bw.close();


    }


    public static double [][] objectivesParetoFrontArray(String instance) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("results/"+instance+"/paretoFront"));
            int count = 0;
            br.mark(2000000);
            while(br.ready()) {
                br.readLine();
                count++;
            }
            br.reset();
            double [][] array = new double[count][];
            for (int i = 0; i < count; i++) {
                String [] aux = br.readLine().split(" ");
                array[i] = new double[aux.length];
                for (int j = 0; j < 2; j++) {
                    array[i][j] = Double.parseDouble(aux[j]);
                }
            }

            return array;

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
