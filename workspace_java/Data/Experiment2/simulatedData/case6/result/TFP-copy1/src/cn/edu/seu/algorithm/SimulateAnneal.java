package cn.edu.seu.algorithm;

import cn.edu.seu.entity.Learner;
import cn.edu.seu.utils.AllocationUtils;
import cn.edu.seu.utils.CalculationUtils;
import cn.edu.seu.utils.CommonUtils;

import java.util.List;
import java.util.Random;

public class SimulateAnneal {
    final double T_END = 1e-8;
    double[][] projectRequireMatrix;
    double[][] sociometricMatrix;
    List<Learner> learners;
    int projectNum;
    int learnerNum;
    int competenceNum;
    int[][] allocateMatrix;
    double systemEvolution;

    //SA算法相关变量
    double alpha = 0.999;
    double T_INIT = 1000;
    int[][] rangeMatrix;
    double optimalSystemEvolution = 0;
    int[][] optimalAllocation;

    public SimulateAnneal(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners) {
        this.projectRequireMatrix = projectRequireMatrix;
        this.sociometricMatrix = sociometricMatrix;
        this.learners = learners;
        projectNum = projectRequireMatrix.length;
        competenceNum = projectRequireMatrix[0].length;
        learnerNum = sociometricMatrix.length;
        this.allocateMatrix = AllocationUtils.allocateLearnersRandomly(learnerNum, projectNum);
    }

    public SimulateAnneal(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners, double alpha, double t_init) {
        this.projectRequireMatrix = projectRequireMatrix;
        this.sociometricMatrix = sociometricMatrix;
        this.learners = learners;
        projectNum = projectRequireMatrix.length;
        competenceNum = projectRequireMatrix[0].length;
        learnerNum = sociometricMatrix.length;
        this.allocateMatrix = AllocationUtils.allocateLearnersRandomly(learnerNum, projectNum);
        this.alpha = alpha;
        this.T_INIT = t_init;
    }

    public void execSA() {
        CalculationUtils.setParamaters(projectRequireMatrix, sociometricMatrix, learners);
        this.systemEvolution = calculateEvolution();
        this.rangeMatrix = AllocationUtils.allocateRangeMatrix(projectNum);
        optimalAllocation = allocateMatrix.clone();
        optimalSystemEvolution = systemEvolution;
        double t = T_INIT;
        while(t > T_END) {
            Random random = new Random();
            int randLearner = random.nextInt(learnerNum);
            int randAllocate = random.nextInt(projectNum+1);
            int[] originalAllocate = allocateMatrix[randLearner].clone();
            allocateMatrix[randLearner] = rangeMatrix[randAllocate].clone();
            double curEvolution = calculateEvolution();
            double dE = curEvolution - systemEvolution;
            if(dE > 0) {
                systemEvolution = curEvolution;
                if(systemEvolution > optimalSystemEvolution) {
                    optimalSystemEvolution = systemEvolution;
                    optimalAllocation = allocateMatrix.clone();
                }
            } else {
                double v = Math.random();
                if(Math.exp(dE / t) > v) {
                    systemEvolution = curEvolution;
                } else {
                    allocateMatrix[randLearner] = originalAllocate.clone();
                }
            }
            t = alpha * t;
        }
    }

    public double proceed(String evolutionStorePath, String allocationStorePath) {
        long startTime = System.currentTimeMillis();
        execSA();
        long endTime = System.currentTimeMillis();
        StringBuilder sbAllocation = new StringBuilder();
        for(int i=0; i<learnerNum; i++) {
            for(int j=0; j<projectNum; j++) {
                sbAllocation.append(allocateMatrix[i][j]).append("   ");
            }
            sbAllocation.append("\n");
        }
        sbAllocation.append("\n");
        CommonUtils.WriteTextAppend(systemEvolution + " " + (endTime-startTime) + "\n", evolutionStorePath);
        CommonUtils.WriteTextAppend(sbAllocation.toString(), allocationStorePath);
        return systemEvolution;
    }

    private double calculateEvolution() {
        CalculationUtils.calculateWillingness(allocateMatrix);
        boolean[] isSatisfied = CalculationUtils.judgeProjectRequirement(allocateMatrix);
        for(int i=0; i<projectNum; i++) {
            if(!isSatisfied[i]) {
                return 0;
            }
        }
        double[] learnerEvolution = CalculationUtils.calculateLearnerEvolution(allocateMatrix, isSatisfied);
        return CalculationUtils.calculateSystemEvolution(learnerEvolution);
    }
}