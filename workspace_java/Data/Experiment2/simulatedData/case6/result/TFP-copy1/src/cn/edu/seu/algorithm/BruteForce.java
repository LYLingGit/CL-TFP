package cn.edu.seu.algorithm;

import cn.edu.seu.entity.Learner;
import cn.edu.seu.utils.CalculationUtils;
import cn.edu.seu.utils.CommonUtils;

import java.util.List;

public class BruteForce {
    double[][] projectRequireMatrix;
    double[][] sociometricMatrix;
    List<Learner> learners;
    int projectNum;
    int learnerNum;
    int competenceNum;

    //最佳演化值和演化方案
    double optimalSystemEvolution = 0;
    int[][] optimalAllocation;

    public BruteForce(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners) {
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
        allocateLearner();  //穷举分配方案
        long endTime = System.currentTimeMillis();
        StringBuilder sbAllocation = new StringBuilder();
        for(int i=0; i<learnerNum; i++) {
            for(int j=0; j<projectNum; j++) {
                sbAllocation.append(optimalAllocation[i][j]).append("   ");
            }
            sbAllocation.append("\n");
        }
        sbAllocation.append("\n");
        CommonUtils.WriteTextAppend(optimalSystemEvolution + " " + (endTime-startTime) + "\n", evolutionStorePath);
        CommonUtils.WriteTextAppend(sbAllocation.toString(), allocationStorePath);
        return optimalSystemEvolution;
    }

    private void allocateLearner() {
        // 对于每个人来说共有projectNum+1种选择(可以不给他分配project)
        int[][] rangeMatrix = new int[projectNum+1][projectNum];
        for(int i=0; i<projectNum;i++) {
            rangeMatrix[i][i] = 1;
        }
        // 全都设为第一种方案（分配到同一项目）
        int[][] allocationMatrix = new int[learnerNum][projectNum];
        for(int i=0; i<learnerNum; i++) {
            allocationMatrix[i] = rangeMatrix[0];
        }
        dfs(rangeMatrix, allocationMatrix,0, projectNum, learnerNum);
    }


    //回溯法深搜
    private void dfs(int[][] rangeMatrix, int[][] allocationMatix, int index, int projectNum, int learnerNum) {
        if(index == learnerNum) {
            //一种分配方案
            int[][] tempAllocationMatrix = allocationMatix.clone();
            //计算当前方案的演化值
            double tempSystemEvolution = calculateEvolution(tempAllocationMatrix);
            //比较最优
            if(tempSystemEvolution > optimalSystemEvolution) {
                optimalSystemEvolution = tempSystemEvolution;
                optimalAllocation = tempAllocationMatrix;
            }
            return;
        }
        dfs(rangeMatrix, allocationMatix,index+1, projectNum, learnerNum);
        for(int i=1; i<rangeMatrix.length; i++) {
            allocationMatix[index] = rangeMatrix[i];
            dfs(rangeMatrix, allocationMatix,index+1, projectNum, learnerNum);
        }
        allocationMatix[index] = rangeMatrix[0];
    }

    private double calculateEvolution(int[][] tempAllocationMatrix) {
        CalculationUtils.calculateWillingness(tempAllocationMatrix);
        boolean[] isSatisfied = CalculationUtils.judgeProjectRequirement(tempAllocationMatrix);
        for(int i=0; i<projectNum; i++) {
            if(!isSatisfied[i]) {
                return 0;
            }
        }
        double[] learnerEvolution = CalculationUtils.calculateLearnerEvolution(tempAllocationMatrix, isSatisfied);
        return CalculationUtils.calculateSystemEvolution(learnerEvolution);
    }
}
