package cn.edu.seu.algorithm;

import cn.edu.seu.entity.Learner;
import cn.edu.seu.utils.CalculationUtils;
import cn.edu.seu.utils.CommonUtils;

import java.util.List;

import static cn.edu.seu.utils.CommonUtils.sortReturnIndex;


public class GraphPartition {
    double[][] projectRequireMatrix;
    double[][] sociometricMatrix;
    List<Learner> learners;
    int projectNum;
    int learnerNum;
    int competenceNum;
    int dataNo;
    //遗传算法变量
    int[][] allocationMatrix;

    public GraphPartition(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners, int dataNo) {
        this.projectRequireMatrix = projectRequireMatrix;
        this.sociometricMatrix = sociometricMatrix;
        this.learners = learners;
        projectNum = projectRequireMatrix.length;
        competenceNum = projectRequireMatrix[0].length;
        learnerNum = sociometricMatrix.length;
        allocationMatrix = new int[learnerNum][projectNum];
        this.dataNo = dataNo;
    }

    public double proceed(String evolutionStorePath, String allocationStorePath) {
        long startTime = System.currentTimeMillis();
        CalculationUtils.setParamaters(projectRequireMatrix, sociometricMatrix, learners);
        int[][] partition = intputMetisResult();
        int[] projectSortIndex = sortProject();
        allocateTeamToProject(partition, projectSortIndex);
        double systemEvolution = calculateEvolution();
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

    private void allocateTeamToProject(int[][] partition, int[] projectSortIndex) {
        int[] teamToProjectIndex = new int[projectNum]; //记录哪个团队被分配到哪个项目
        boolean[] teamToProjectFlag = new boolean[projectNum];
        for(int projectIndex : projectSortIndex) {
            double[] teamToProjectEvaluation = new double[projectNum];
            for(int i=0; i<learnerNum; i++) {
                if(teamToProjectFlag[partition[i][1]]) continue;    //该learner所在团队已被分配过项目
                teamToProjectEvaluation[partition[i][1]] += learners.get(i).getEvaluationProject(projectIndex);
            }
            double projectIndexMaxEvaluation = Integer.MIN_VALUE;
            int teamIndexMax = 0;
            for(int i=0; i<projectNum; i++) {
                if(teamToProjectEvaluation[i] > projectIndexMaxEvaluation) {
                    projectIndexMaxEvaluation = teamToProjectEvaluation[i];
                    teamIndexMax = i;
                }
            }
            teamToProjectIndex[teamIndexMax] = projectIndex;    //团队teamIndexMax被分配至项目projectIndex
            teamToProjectFlag[teamIndexMax] = true;
        }
        for(int i=0; i<learnerNum; i++) {
            allocationMatrix[i][teamToProjectIndex[partition[i][1]]] = 1;
        }
    }

    private int[] sortProject() {
        double[] projectScoreSum = new double[projectNum];
        for(int i=0; i<learnerNum; i++) {
            learners.get(i).calculateLearnerEvolutionProject();
        }
        for(int j=0; j<projectNum; j++) {
            for(int i=0; i<learnerNum; i++) {
                projectScoreSum[j] += learners.get(i).getEvaluationProject(j);
            }
        }
        return sortReturnIndex(projectScoreSum);
    }

    private double calculateEvolution() {
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

    private int[][] intputMetisResult() {
        String readPath = "D:\\Code\\lyl_TeamFormation\\workspace_c++\\Data\\Experiment1\\case" + dataNo + "\\partition.txt";
        int[][] partition = CommonUtils.ReadText(readPath);
        return partition;
    }

}
