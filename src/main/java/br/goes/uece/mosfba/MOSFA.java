package br.goes.uece.mosfba;

import com.aparapi.natives.NativeLoader;
import jmetal.core.Problem;
import com.aparapi.Range;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.util.JMException;
import jmetal.util.Ranking;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Multi-Objective Scalarizing Function Based Algorithm
 */
public class MOSFA{
    SolutionSet result;
    Ranking rank;
    SA sa;
    Range range;
    Problem problem;
    float [] weights;



    MOSFA(double max, double delta, Problem problem) {
        this.problem = problem;
        weights = generateWeights(problem.getNumberOfObjectives(), max, delta);
        int nOfWeights = weights.length/problem.getNumberOfObjectives();
        range = Range.create(nOfWeights);
    }

    public SolutionSet execute() {

        //sa.execute(range);
        //weights = new float[]{12000.f, 11000.f, 10000.f, 11000.f, 10000.f, 11000.f, 10000.f, 11000.f, 10000.f, 11000.f};
        MNRP mNRP = (MNRP) problem;
        SolutionSet s = Pararell_MOSA_4_mNRP.execute(weights, mNRP.getCost(), mNRP.getValue(), mNRP.getPrecedence(),
                10, problem);
        Ranking r = new Ranking(s);
        SolutionSet front = r.getSubfront(0);
        removeDuplicatedSolutions(front);
        return front; // non-doninated solutions
    }

    public float [] generateWeights(int nOfWeights, double max, double delta) {
        double size = Math.pow(max/delta+1, 2)-1;

        ArrayList<double []> aux = new ArrayList<double[]>((int)size*nOfWeights);

        double [] nextVector = new double[nOfWeights];

        for (int i = 0; i < size; i++) {
            double [] w2 = nextVector;
            int pos = nOfWeights-1;
            w2[pos] += delta;
            while(w2[pos] > max) {
                w2[pos] = 0;
                pos = pos-1;
                if(pos < 0) break;
                w2[pos] += delta;
            }

            aux.add(w2);
            nextVector = new double[nOfWeights];
            if(i+1 < size) {
                for (int j = 0; j < nOfWeights; j++) {
                    nextVector[j] = w2[j];
                }
            }
        }

        removeRedundantConfigs(aux);

        float [] result = new float [aux.size()*nOfWeights];

        for (int i = 0; i < aux.size(); i++) {
            for (int j = 0; j < nOfWeights; j++) {
                result[i*nOfWeights+j] = (float) aux.get(i)[j];
            }
        }

        return result;
    }

    public void removeRedundantConfigs(ArrayList<double[]> array) {
        for (int i = 0; i < array.size(); i++) {
            for (int j = i+1; j < array.size(); j++) {
                if(isProportional(array.get(i), array.get(j)) && i!=j) {
                    array.remove(j);
                    j--;
                }
            }
        }
    }

    public boolean isProportional (double [] v1, double [] v2) {
        double ratio = 0;
        for (int i = 0; i < v1.length; i++) {
            if(v1[i] != 0) {

                if(v2[i] == 0) return false;

                ratio = v1[i]/v2[i];
            }
        }

        for (int i = 0; i < v1.length; i++) {
            if(v2[i]*ratio != v1[i]) return false;
        }
        return true;
    }

    public void removeDuplicatedSolutions(SolutionSet set) {
        for (int i = 0; i < set.size(); i++) {
            for (int j = i+1; j < set.size(); j++) {
                if(i!=j && isEqual(set.get(i), set.get(j))) {
                    set.remove(j);
                    j--;
                }
            }
        }
    }

    public boolean isEqual(Solution s1, Solution s2) {
        Variable [] v1 = s1.getDecisionVariables();
        Variable [] v2 = s2.getDecisionVariables();

        for (int i = 0; i < v1.length; i++) {
            try {
                if(v1[i].getValue() != v2[i].getValue()) {
                    return false;
                }
            } catch (JMException e) {
                e.printStackTrace();
            }
        }


        return true;
    }

}
