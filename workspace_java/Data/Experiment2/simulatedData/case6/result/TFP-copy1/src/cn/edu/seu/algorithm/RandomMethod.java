package cn.edu.seu.algorithm;

import cn.edu.seu.entity.Learner;
import cn.edu.seu.utils.AllocationUtils;
import cn.edu.seu.utils.CalculationUtils;
import cn.edu.seu.utils.CommonUtils;

import java.util.List;

public class RandomMethod {
    double[][] projectRequireMatrix;
    double[][] sociometricMatrix;
    List<Learner> learners;
    int projectNum;
    int learnerNum;
    int competenceNum;

    public RandomMethod(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners) {
        this.projectRequireMatrix = projectRequireMatrix;
        this.sociometricMatrix = sociometricMatrix;
        this.learners = learners;
        projectNum = projectRequireMatrix.length;
        competenceNum = projectRequireMatrix[0].length;
        learnerNum = sociometricMatrix.length;
    }

    public double proceed(String evolutionStorePath, String allocationStorePath) {
        long startTime = System.currentTimeMillis();
        CalculationUtils.setParamaters(projectRequireMatrix, sociometricMatrix, learners);
        int[][] allocationMatrix = AllocationUtils.allocateLearnersRandomly(learnerNum, projectNum);
        double systemEvolution = calculateEvolution(allocationMatrix);

        long endTime = System.currentTimeMillis();
        StringBuilder sbAllocation = new StringBuilder();
        for(int i=0; i<learnerNum; i++) {
            for(int j=0; j<projectNum; j++) {
                sbAllocation.append(allocationMatrix[i][j]).append("   ");
            }
            sbAllocation.append("\n");
        }
        sbAllocation.append("\n");
        CommonUtils.WriteTextAppend(systemEvolution + " " + (endTime-startTime) + "\n", evolutionStorePath);
        CommonUtils.WriteTextAppend(sbAllocation.toString(), allocationStorePath);
        return systemEvolution;
    }

    private double calculateEvolution(int[][] allocationMatrix) {
        CalculationUtils.calculateWillingness(allocationMatrix);
        boolean[] isSatisfied = CalculationUtils.judgeProjectRequirement(allocationMatrix);
        for(int i=0; i<projectNum; i++) {
            if(!isSatisfied[i]) {
                return 0;
            }
        }
        double[] learnerEvolution = CalculationUtils.calculateLearnerEvolution(allocationMatrix, isSatisfied);
        return CalculationUtils.calculateSystemEvolution(learnerEvolution);
    }
}
