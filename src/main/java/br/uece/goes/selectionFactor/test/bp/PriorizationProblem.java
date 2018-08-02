package br.uece.goes.selectionFactor.test.bp;

import br.uece.goes.selectionFactor.Evaluator;
import br.uece.goes.selectionFactor.ProblemWithSelectionFactor;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.RankPermutationSolutionType;
import jmetal.util.JMException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by Raphael on 24/05/2017.
 */
public class PriorizationProblem extends ProblemWithSelectionFactor {

    double priority[];
    double severity[];
    double score[];
    int votos[];
    int id[];
    public static int n_bugs;
    int n_bugs_selecionados;
    boolean matrix_dep[][];
    double func_importance;
    double func_score;
    double func_risk;
    int fator_Norm_Mult = 0;

    public PriorizationProblem(String filename){


        readBugsConfig("src"+File.separator+"instances"+File.separator + filename);
        numberOfVariables_ = n_bugs_selecionados;
        numberOfObjectives_ = 1;
        numberOfConstraints_ = 0;
        problemName_ = "PriorizationProblem";

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        for (int i = 0; i < numberOfVariables_; i++) {
            lowerLimit_[i] = 0;
            upperLimit_[i] = n_bugs - 1;
        }
        try {
            //solutionType_ = new IntSolutionType(this);
            solutionType_ = new RankPermutationSolutionType(this,numberOfVariables_);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* priority */
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{

                Variable _variable[] = solution.getDecisionVariables();
                Integer variable[] = new Integer[_variable.length];

                for (int i = 0; i < variable.length; i++)
                    variable[i] = (int) _variable[i].getValue();

                func_importance = 0;

                for (int i = 0; i < n_bugs_selecionados; i++) {

                    func_importance += priority[variable[i]]  * (n_bugs_selecionados - i);
                }

                return -func_importance;
             //   return func_importance;
            }
        });

        /* votes */
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{

                Variable _variable[] = solution.getDecisionVariables();
                Integer variable[] = new Integer[_variable.length];

                for (int i = 0; i < variable.length; i++)
                    variable[i] = (int) _variable[i].getValue();

                func_score = 0;

                for (int i = 0; i < n_bugs_selecionados; i++) {
                    func_score += score[variable[i]] * (n_bugs_selecionados - i);
                }


                return -func_score;
                //return func_score;
            }
        });

        /* risk */
        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException{

                Variable _variable[] = solution.getDecisionVariables();
                Integer variable[] = new Integer[_variable.length];

                for (int i = 0; i < variable.length; i++)
                    variable[i] = (int) _variable[i].getValue();

                func_risk = 0;

                for (int i = 0; i < n_bugs_selecionados; i++) {
                    func_risk += severity[variable[i]] * (i + 1);
                }
                return func_risk;
            }
        });

        addEvaluator(new Evaluator(this) {
            @Override
            public double evaluate(Solution solution) throws JMException {
                Variable _variable[] = solution.getDecisionVariables();
                Integer variable[] = new Integer[_variable.length];

                for (int i = 0; i < variable.length; i++)
                    variable[i] = (int) _variable[i].getValue();
                return contrainsViolated(variable);
            }
        }, 100);
    }

    @Override
    public void evaluate(Solution solution) throws JMException {

        Variable _variable[] = solution.getDecisionVariables();
        Integer variable[] = new Integer[_variable.length];

        for (int i = 0; i < variable.length; i++)
            variable[i] = (int) _variable[i].getValue();

        func_importance = 0;
        func_risk = 0;

        for (int i = 0; i < n_bugs_selecionados; i++) {

            func_importance += (priority[variable[i]] + score[variable[i]]) / 2 * (n_bugs_selecionados - i)
                    / fator_Norm_Mult;
            func_risk += severity[variable[i]] * (i + 1) / fator_Norm_Mult;
        }

        double fitness = func_importance - func_risk;

        int constrains_violated = contrainsViolated(variable);

        if (constrains_violated == 0) {
            solution.setObjective(0, -fitness);

        } else {
            solution.setObjective(0, -fitness + 9000000);
        }

    }

    public int contrainsViolated(Integer variable[]) {

        // restri��o de dependencia
        int constrains_violated = 0;
        for (int i = 0; i < n_bugs_selecionados; i++) {

            for (int j = 0; j < n_bugs; j++) {
                if (matrix_dep[variable[i]][j] == true) {
                    boolean flag = false;
                    for (int k = 0; k <= i - 1; k++) {
                        if (j == variable[k]) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == false)
                        constrains_violated++;
                }
            }
        }

        // solucao sem bugs repetidos
        for (int i = 0; i < n_bugs_selecionados; i++) {
            for (int j = 0; j < n_bugs_selecionados; j++) {
                if (variable[i].equals(variable[j]) && i != j) {
                    constrains_violated++;
                }
            }
        }

        return constrains_violated;
    }

    public void readBugsConfig(String filename){

        Scanner scn=null;

        try {
            scn = new Scanner(new File(filename));
        } catch(Exception e) {
            System.out.println(e);
        }

        DecimalFormat fmt = new DecimalFormat("0.00");
        String caracter="#";

        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        PriorizationProblem.n_bugs = Integer.parseInt(caracter);

        caracter=scn.nextLine();
        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        n_bugs_selecionados = Integer.parseInt(caracter);

        int aux[];
        score = new double[PriorizationProblem.n_bugs];
        votos = new int[PriorizationProblem.n_bugs];
        aux = new int[PriorizationProblem.n_bugs];

        caracter=scn.nextLine();
        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        StringTokenizer tokens = new StringTokenizer(caracter);
        for (int i = 0; i < PriorizationProblem.n_bugs; i++) {
            votos[i] = Integer.parseInt(tokens.nextToken().trim());
            aux[i] = votos[i];
        }

        Arrays.sort(aux);
        double normal_factor = 0;

        for (int i = PriorizationProblem.n_bugs - 1; i >= PriorizationProblem.n_bugs - n_bugs_selecionados; i--) {
            normal_factor += aux[i];
        }

        for (int i = 0; i < PriorizationProblem.n_bugs; i++) {
            score[i] = ((double) votos[i]) / normal_factor;
        }

        priority = new double[PriorizationProblem.n_bugs];

        caracter=scn.nextLine();
        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        tokens = new StringTokenizer(caracter);

        for (int i = 0; i < PriorizationProblem.n_bugs; i++) {
            priority[i] = Double.parseDouble(tokens.nextToken().trim());
        }

        severity = new double[PriorizationProblem.n_bugs];

        caracter=scn.nextLine();
        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        tokens = new StringTokenizer(caracter);
        for (int i = 0; i < PriorizationProblem.n_bugs; i++) {
            severity[i] = Double.parseDouble(tokens.nextToken().trim());
        }


        id = new int[PriorizationProblem.n_bugs];

        caracter=scn.nextLine();
        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        tokens = new StringTokenizer(caracter);
        for (int i = 0; i < PriorizationProblem.n_bugs; i++) {
            id[i] = Integer.parseInt(tokens.nextToken().trim());
        }


        matrix_dep = new boolean[PriorizationProblem.n_bugs][PriorizationProblem.n_bugs];

        caracter=scn.nextLine();
        while(caracter.contains("#")){
            caracter=scn.nextLine();
        }

        tokens = new StringTokenizer(caracter);
        for (int i = 0; i < PriorizationProblem.n_bugs; i++) {
            int pre_req = Integer.parseInt(tokens.nextToken().trim());

            if (pre_req != 0) {
                for (int j = 0; j < PriorizationProblem.n_bugs; j++) {
                    if (id[j] == pre_req) {
                        matrix_dep[i][j] = true;
                    } else {
                        matrix_dep[i][j] = false;
                    }
                }
            } else {
                for (int j = 0; j < PriorizationProblem.n_bugs; j++) {
                    matrix_dep[i][j] = false;
                }
            }
        }

        for (int i = 1; i < n_bugs_selecionados + 1; i++) {
            fator_Norm_Mult += i;
        }
    }

    public double[] getPriority() {
        return priority;
    }

    public double[] getSeverity() {
        return severity;
    }

    public double[] getScore() {
        return score;
    }

    public int[] getVotos() {
        return votos;
    }

    public int[] getId() {
        return id;
    }

    public static int getN_bugs() {
        return n_bugs;
    }
}
