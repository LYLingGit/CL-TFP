package cn.edu.seu.utils;

import cn.edu.seu.entity.Learner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalculationUtils {
    static double[][] projectRequireMatrix;
    static double[][] sociometricMatrix;
    static List<Learner> learners;
    static int learnerNum;
    static int projectNum;
    static int competenceNum;

    public static void setParamaters(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners) {
        CalculationUtils.projectRequireMatrix = projectRequireMatrix;
        CalculationUtils.sociometricMatrix = sociometricMatrix;
        CalculationUtils.learners = learners;
        learnerNum = sociometricMatrix.length;
        projectNum = projectRequireMatrix.length;
        competenceNum = projectRequireMatrix[0].length;
    }

    public static void calculateWillingness(int[][] allocationMatrix) {
        for(Learner learner : learners) {
            learner.setWillingness(0);
        }
        for(int i=0; i<projectNum; i++) {
            calculateWillingnessByProject(allocationMatrix, i);
        }
    }

    public static void calculateWillingnessByProject(int[][] allocationMatrix, int projectId) {
        Set<Integer> projectMember;
        projectMember = new HashSet<>();
        for(int j=0; j<learnerNum; j++) {
            if(allocationMatrix[j][projectId] == 1){
                projectMember.add(j);
            }
        }
        if(projectMember.size() <= 1) {
            return;
        }
        for(int self : projectMember) {
            double sociometricSum = 0;    //当前成员和其他成员的社会计量值总和
            for(int other: projectMember) {
                if(self == other) continue;
                sociometricSum += sociometricMatrix[self][other];
            }
            double willingness = sociometricSum / (projectMember.size()-1);
            learners.get(self).setWillingness(willingness);
        }
    }

    public static boolean[] judgeProjectRequirement(int[][] allocationMatrix) {
        //每个项目是否满足项目需求
        boolean[] isSatisfied = new boolean[projectNum];
        for(int i=0; i<projectNum; i++) {
            isSatisfied[i] = isSatisfiedRequired(allocationMatrix, i);
        }
        return isSatisfied;
    }

    //判断某个项目的团队成员是否满足项目需求
    public static boolean isSatisfiedRequired(int[][] allocationMatrix, int fromProjectIndex) {
        for(int i=0; i<competenceNum; i++) {
            //得到有项目需求的能力索引
            if (projectRequireMatrix[fromProjectIndex][i] != 0) {
                double teamCompetence = 0;
                for (int k = 0; k < learnerNum; k++) {
                    //判断该学习者是否在该项目中
                    if (allocationMatrix[k][fromProjectIndex] == 1) {
                        //将该学生能力值加入团队能力值中
                        Learner learner = learners.get(k);
                        teamCompetence += learner.getWillingness() * learner.getCompetence(i);
                    }
                }
                //判断团队能力值是否符合项目需求
                if (teamCompetence < projectRequireMatrix[fromProjectIndex][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double[] calculateLearnerEvolution(int[][] allocationMatrix, boolean[] isSatisfied) {
        double[] leanerEvolution = new double[learnerNum];
        for(int i=0; i<projectNum;i++) {
            if(isSatisfied[i]) {    //项目需求约束被满足
                for(int k=0; k<learnerNum; k++) {
                    if(allocationMatrix[k][i] == 1) {       //当前learner被分配到项目中
                        for(int j=0; j<competenceNum; j++) {
                            if(projectRequireMatrix[i][j]>0) {
                                Learner learner = learners.get(k);
                                leanerEvolution[k] += learner.getEvolution(j) * learner.getWillingness();
                            }
                        }
                    }
                }
            }
        }
        return leanerEvolution;
    }

    public static double calculateSystemEvolution(double[] learnerEvolution) {
        double sum = 0;
        for (double evolution : learnerEvolution) {
            sum += evolution;
        }
        return sum;
    }


    //评估每个人在项目上的演化
    public static double[][] calculateLearnerEvaluationProject() {
        double[][] leanerEvalutationProject = new double[learnerNum][projectNum];
        for(int i=0; i<projectNum;i++) {
            for(int k=0; k<learnerNum; k++) {
                for(int j=0; j<competenceNum; j++) {
                    if(projectRequireMatrix[i][j]>0) {
                        Learner learner = learners.get(k);
                        leanerEvalutationProject[k][i] += learner.getEvolution(j);
                    }
                }
            }
        }
        return leanerEvalutationProject;
    }
}
