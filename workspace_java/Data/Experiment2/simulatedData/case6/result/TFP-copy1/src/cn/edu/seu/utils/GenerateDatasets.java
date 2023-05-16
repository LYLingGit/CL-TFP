package cn.edu.seu.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GenerateDatasets {
    //learner学习者数，project项目数，competence能力数，no第几个例子
    public static void generateDataExperiment1(String dir, int learner, int project, int competence, int no) {
        double[][] projectRequireMatrix = generateProjectRequireMatrix(project, competence);
        double[][] sociometricMatrix = generatesociometricMatrixExperiment1(learner);
        double[][] learnerCompetenceMatrix = generateLearnerCompetenceMatrix(learner, competence);
        double[][] learnerLearningrateMatrix = generateLearnerLearningrateMatrix(learner, competence);
        double[][] competenceGraph = generateCompetenceGraph(competence);
        //将生成的矩阵导出为csv格式
        CommonUtils.WriteMatrix(projectRequireMatrix, dir, "projectRequireMatrix.csv");
        CommonUtils.WriteMatrix(sociometricMatrix, dir, "sociometricMatrix.csv");
        CommonUtils.WriteMatrix(learnerCompetenceMatrix, dir, "learnerCompetenceMatrix.csv");
        CommonUtils.WriteMatrix(learnerLearningrateMatrix, dir, "learnerLearningrateMatrix.csv");
        CommonUtils.WriteMatrix(competenceGraph, dir, "competenceGraph.csv");
    }

    private static double[][] generateCompetenceGraph(int competence) {
        double[][] competenceGraph = new double[competence][competence];
        int simpleNum = competence/2;   //简单能力数量
        int componentNum = (int) (competence * 0.35);   //单纯依赖简单能力的数量
        //依赖复杂能力或简单能力的数量
        int complexComponentNum = competence - simpleNum - componentNum;
        for(int i=0; i<competence; i++) {
            if(i<simpleNum) {   //分配简单能力的能力图
                competenceGraph[i][i] = 1;
            } else if(i<simpleNum + componentNum) { //分配复合能力的能力图
                Random random = new Random();
                float randNum = random.nextFloat();
                if(randNum <= 0.3) {    //0.3的概率只依赖一个能力
                    int index = random.nextInt(simpleNum);
                    double randVal = (10+random.nextInt(30)*1.0)/100;
                    competenceGraph[i][index] = randVal;
                    competenceGraph[i][i] = 1 - randVal;
                } else {     //0.7的概率依赖多种能力
                    float randCategory = random.nextFloat();
                    int categoryNum;    //决定选依赖的能力种类
                    if(randCategory < 0.6) {
                        categoryNum = 1+random.nextInt(simpleNum/2);
                    } else {
                        categoryNum = 1+random.nextInt(simpleNum);
                    }
                    Set<Integer> indexSet = new HashSet<>();
                    double sum = 0;
                    for(int j=0; j < categoryNum; j++) {
                        int index = random.nextInt(simpleNum);
                        while(indexSet.contains(index)) {
                            index = random.nextInt(simpleNum);
                        }
                        indexSet.add(index);
                        double randVal = (10+random.nextInt(20)*1.0)/100;
                        competenceGraph[i][index] = randVal;
                        sum += randVal;
                        if(sum > 0.6) break;
                    }
                    competenceGraph[i][i] = 1-sum;
                }
            } else {    //分配复合能力的能力图
                Random random = new Random();
                int categoryNum = 1+random.nextInt((simpleNum+componentNum)/2);
                Set<Integer> indexSet = new HashSet<>();
                double sum = 0;
                for(int j=0; j < categoryNum; j++) {
                    int index = random.nextInt(simpleNum+componentNum);
                    while(indexSet.contains(index)) {
                        index = random.nextInt(simpleNum+componentNum);
                    }
                    indexSet.add(index);
                    double randVal = (10+random.nextInt(20)*1.0)/100;
                    competenceGraph[i][index] = randVal;
                    sum += randVal;
                    if(sum > 0.6) break;
                }
                competenceGraph[i][i] = 1-sum;
            }
        }
        return competenceGraph;
    }

    // 生成学习者-学习率矩阵
    // 以0.6的概率在[0.1,0.5]，以0.4的概率在[0.5,0.7)
    private static double[][] generateLearnerLearningrateMatrix(int learner, int competence) {
        return generateMatrix(learner, competence, 0.1, 0.7, false);
    }

    // 生成学习者-能力矩阵
    // 以0.3的概率为[1,10]，以0.4的概率为[10,15]，以0.2的概率为[15,20],以0.1的概率为[20,30]
    private static double[][] generateLearnerCompetenceMatrix(int learner, int competence) {
        return generateMatrix(learner, competence, 1, 30, false);
    }

    // 生成社会计量矩阵
    private static double[][] generatesociometricMatrixExperiment1(int learner) {
        return generateMatrix(learner, learner, 0, 1, true);
    }

    private static double[][] generateMatrix(int x, int y, double floor, double cell, boolean flag) {
        double[][] sociometrixMatrix = new double[x][y];
        Random random = new Random();
        double max = Integer.MIN_VALUE;
        double min = Integer.MAX_VALUE;
        for(int i=0; i<x; i++) {
            for(int j=0; j<y;j++) {
                if(flag && i==j) continue;
                sociometrixMatrix[i][j] = random.nextGaussian();
                max = Math.max(sociometrixMatrix[i][j], max);
                min = Math.min(sociometrixMatrix[i][j], min);
            }
        }
        for(int i=0; i<x; i++) {
            for(int j=0; j<y;j++) {
                if(flag && i==j) continue;
                sociometrixMatrix[i][j] = ((sociometrixMatrix[i][j] - min) / (max-min)) * (cell - floor) + floor;
            }
        }
        return sociometrixMatrix;
    }

    // 生成项目需求矩阵
    private static double[][] generateProjectRequireMatrix(int project, int competence) {
        double[][] projectRequireMatrix = new double[project][competence];
        for(int i=0; i<project; i++) {
            boolean flag = false;           //防止有个项目能力需求均为0
            for(int j=0; j<competence;j++) {
                Random random = new Random();
                float randNum = random.nextFloat();
                if(randNum < 0.7)
                    projectRequireMatrix[i][j] = 0;
                else if(randNum < 0.9) {
                    flag = true;
                    projectRequireMatrix[i][j] = (5+random.nextInt(5));
                } else {
                    flag = true;
                    projectRequireMatrix[i][j] = (10+random.nextInt(5));
                }
            }
            if(!flag) {     //若有个项目所有能力需求为0，随机选一个能力赋予项目需求
                Random random = new Random();
                int index = competence/2+random.nextInt(competence/2);
                projectRequireMatrix[i][index] = (15+random.nextInt(5));
            }
        }
        return projectRequireMatrix;
    }

    public static void generateDataFromTxt(String path, String dataName, int learner, int project, int competence, int dataNo) {
        double[][] projectRequireMatrix = generateProjectRequireMatrix(project, competence);
        double[][] sociometricMatrix = CommonUtils.ReadSocioMatrixFromTxt(path, learner);
        double[][] learnerCompetenceMatrix = generateLearnerCompetenceMatrix(learner, competence);
        double[][] learnerLearningrateMatrix = generateLearnerLearningrateMatrix(learner, competence);
        double[][] competenceGraph = generateCompetenceGraph(competence);
        //将生成的矩阵导出为csv格式
        String dir = "e:\\TeamFormationData\\Experiment4\\" + dataName + "\\case" + dataNo + "\\";
        CommonUtils.WriteMatrix(projectRequireMatrix, dir, "projectRequireMatrix.csv");
        CommonUtils.WriteMatrix(sociometricMatrix, dir, "sociometricMatrix.csv");
        CommonUtils.WriteMatrix(learnerCompetenceMatrix, dir, "learnerCompetenceMatrix.csv");
        CommonUtils.WriteMatrix(learnerLearningrateMatrix, dir, "learnerLearningrateMatrix.csv");
        CommonUtils.WriteMatrix(competenceGraph, dir, "competenceGraph.csv");
    }

    public static void generateDataExperiment5(String dataName, int learner, int project, int competence) {
        double[][] projectRequireMatrix = generateProjectRequireMatrix(project, competence);
        double[][] learnerCompetenceMatrix = generateLearnerCompetenceMatrix(learner, competence);
        double[][] learnerLearningrateMatrix = generateLearnerLearningrateMatrix(learner, competence);
        double[][] competenceGraph = generateCompetenceGraph(competence);
        switch (dataName) {
            case("ER"):
                for(int i=1; i<=5; i++) {
                    double[][] sociometricMatrix = GenerateNetworks.generateERRandomNetwork(learner, i*0.067);
                    String dir = "e:\\TeamFormationData\\Experiment5\\" + dataName + "\\case" + i + "\\";
                    CommonUtils.WriteMatrix(projectRequireMatrix, dir, "projectRequireMatrix.csv");
                    CommonUtils.WriteMatrix(sociometricMatrix, dir, "sociometricMatrix.csv");
                    CommonUtils.WriteMatrix(learnerCompetenceMatrix, dir, "learnerCompetenceMatrix.csv");
                    CommonUtils.WriteMatrix(learnerLearningrateMatrix, dir, "learnerLearningrateMatrix.csv");
                    CommonUtils.WriteMatrix(competenceGraph, dir, "competenceGraph.csv");
                }
                break;
            case("WS"):
                for(int i=1; i<=5; i++) {
                    double[][] sociometricMatrix = GenerateNetworks.generateWSNetwork(learner, 10 + 10*(i-1), 0.05);
                    String dir = "e:\\TeamFormationData\\Experiment5\\" + dataName + "\\case" + i + "\\";
                    CommonUtils.WriteMatrix(projectRequireMatrix, dir, "projectRequireMatrix.csv");
                    CommonUtils.WriteMatrix(sociometricMatrix, dir, "sociometricMatrix.csv");
                    CommonUtils.WriteMatrix(learnerCompetenceMatrix, dir, "learnerCompetenceMatrix.csv");
                    CommonUtils.WriteMatrix(learnerLearningrateMatrix, dir, "learnerLearningrateMatrix.csv");
                    CommonUtils.WriteMatrix(competenceGraph, dir, "competenceGraph.csv");
                }
                break;
            case("BA"):
                int[] addEdges = {6,24,45,67,100};
                for(int i=1; i<=5; i++) {
                    double[][] sociometricMatrix = GenerateNetworks.generateBANetwork(learner, 60, 1, addEdges[i-1]);
                    String dir = "e:\\TeamFormationData\\Experiment5\\" + dataName + "\\case" + i + "\\";
                    CommonUtils.WriteMatrix(projectRequireMatrix, dir, "projectRequireMatrix.csv");
                    CommonUtils.WriteMatrix(sociometricMatrix, dir, "sociometricMatrix.csv");
                    CommonUtils.WriteMatrix(learnerCompetenceMatrix, dir, "learnerCompetenceMatrix.csv");
                    CommonUtils.WriteMatrix(learnerLearningrateMatrix, dir, "learnerLearningrateMatrix.csv");
                    CommonUtils.WriteMatrix(competenceGraph, dir, "competenceGraph.csv");
                }
                break;
        }

        //将生成的矩阵导出为csv格式

    }
}
