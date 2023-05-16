package cn.edu.seu;

import cn.edu.seu.algorithm.*;
import cn.edu.seu.utils.*;
import cn.edu.seu.entity.*;

import java.util.ArrayList;
import java.util.List;

public class MainMethod {
    static int algorithmNum = 5;    //算法总数
    public static void experiment1(int dataNo, String dir, String resultDir, String cpath) {
        System.out.println("======Experiment" + dataNo + "=======");
        //读取数据
        double[][] projectRequireMatrix = CommonUtils.ReadMatrix(dir+"projectRequireMatrix.csv");
        double[][] competenceGraphMatrix = CommonUtils.ReadMatrix(dir+"competenceGraph.csv");
        double[][] sociometricMatrix = CommonUtils.ReadMatrix(dir+"sociometricMatrix.csv");
        double[][] learnerCompetenceMatrix = CommonUtils.ReadMatrix(dir+"learnerCompetenceMatrix.csv");
        double[][] learnerLearningrateMatrix = CommonUtils.ReadMatrix(dir+"learnerLearningrateMatrix.csv");

        String[] evolutionResultPath = {resultDir + "BF_evolution.txt", resultDir + "Random_evolution.txt" , resultDir + "GraphPartition_evolution.txt", resultDir + "SA_evolution.txt", resultDir + "KMeans_evolution.txt"};
        String[] allocationResultPath = {resultDir + "BF_allocation.txt", resultDir + "Random_allocation.txt" , resultDir + "GraphPartition_allocation.txt", resultDir + "SA_allocation.txt", resultDir + "KMeans_allocation.txt"};

        Learner.setGraph(competenceGraphMatrix);
        Learner.setProjectRequireMatrix(projectRequireMatrix);
        int times = 50;
        long execTime;
        StringBuilder sb = new StringBuilder();
        for(int i=1; i< 2; i++) {
            System.out.println("第" + (i+1) + "种算法计算中.....");
            long startTime = System.currentTimeMillis();
            double result = 0;
            for(int j=0; j<times; j++) {
                //初始化
                List<Learner> learners = generateLearners(learnerCompetenceMatrix, learnerLearningrateMatrix);
                CommonUtils.WriteTextAppend("============第"+ (j+1) + "轮==============\n", allocationResultPath[i]);
                result += execAlgorithm(i, evolutionResultPath, allocationResultPath, projectRequireMatrix, sociometricMatrix, learners, dataNo);
            }
            long endTime = System.currentTimeMillis();
            execTime = endTime - startTime;
            sb.append(result / times).append("\t");
            if(i==2) {
                int partitionTime = CommonUtils.ReadTextTime(cpath);
                sb.append(execTime * 1.0 / times + partitionTime).append("\n");
            } else
                sb.append(execTime * 1.0 / times).append("\n");
        }
        CommonUtils.WriteTextAppend(sb.toString(), resultDir + "result2.txt");
    }

    public static void main(String[] args) {
        // 实验一：不同算法在不同规模下的演化水平和运行效率
//        int[] learnerNum = {6, 9, 12, 15, 50, 100, 200, 500, 1000, 2000};
//        int[] projectNum = {2, 3, 3, 3, 10, 20, 20, 50, 100, 200};
//        int[] competenceNum = {5, 5, 5, 7, 7, 10, 10, 15, 20, 30};
//        for(int i=0; i<learnerNum.length; i++) {
//            String dir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment1\\case"+ (i+1) + "\\";
//            GenerateDatasets.generateDataExperiment1(dir, learnerNum[i], projectNum[i], competenceNum[i], i+1);
//            double[][] sociometricMatrix = CommonUtils.ReadMatrix(dir+"sociometricMatrix.csv");
//            outputGraphToMetis(learnerNum[i], projectNum[i], sociometricMatrix, i+1);
//        }
//        int caseNum = 4;
//        for(int i=7; i <= 7; i++) {
//            String dir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment1\\case"+ i + "\\";
//            String resultDir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment1\\case"+ i + "\\result\\";
//            String cpath = "D:\\Code\\lyl_TeamFormation\\workspace_c++\\Data\\Experiment1\\case" + i + "\\time.txt";
//            experiment1(i, dir, resultDir, cpath);
//        }

        // 实验二：NewMethod调参
                experiment2();

        // 实验四：真实网络下算法测试
//        int[] learnerNum = {50, 100, 200, 500};
//        int[] projectNum = {5, 10, 20, 50};
//        int[] competenceNum = {7, 10, 10, 15};
////        String[] dataNames = {"eu-core"};
//        String[] dataNames = {"caltech"};
//        for(String dataName : dataNames) {
//            String path = "E:\\TeamFormationData\\Experiment4\\" + dataName + ".txt";
//            for(int i=0; i<4; i++) {
//                String dir = "E:\\TeamFormationData\\Experiment4\\" + dataName + "\\case"+ (i+1) + "\\";
//                GenerateDatasets.generateDataFromTxt(path, dataName, learnerNum[i], projectNum[i], competenceNum[i], i+1);
//                double[][] sociometricMatrix = CommonUtils.ReadMatrix(dir+"sociometricMatrix.csv");
//                outputGraphToMetis(dataName, learnerNum[i], projectNum[i], sociometricMatrix, i+1);
//            }
//        }

//        int dataNo = 4;
//        for(int i=9; i < 10; i++) {
//            experiment(i);
//        }

        // 实验五：不同网络结构下算法的评测
//        int learnerNum = 300;
//        int projectNum = 30;
//        int competenceNum = 10;
//        String[] dataNames = {"ER","BA","WS"};
//        for(String dataName : dataNames) {
//            GenerateDatasets.generateDataExperiment5(dataName, learnerNum, projectNum, competenceNum);
//        }
//        for(String dataName : dataNames) {
//            for (int i = 0; i < 5; i++) {
//                String dir = "E:\\TeamFormationData\\Experiment5\\" + dataName + "\\case" + (i + 1) + "\\";
//                double[][] sociometricMatrix = CommonUtils.ReadMatrix(dir + "sociometricMatrix.csv");
//                outputGraphToMetis(dataName, learnerNum, projectNum, sociometricMatrix, i + 1);
//            }
//        }

    }

    private static void experiment2() {
        System.out.println("======DataType1=======");
        for(int i = 5; i <= 5; i++) {
            System.out.println("=============case" + i + "=============");
            String fileDir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment2\\simulatedData\\case"+ i + "\\";
            String resultDir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment2\\simulatedData\\case"+ i + "\\result\\";
            experiment2Process(fileDir, resultDir);
        }
//
//        System.out.println("======DataType2=======");
//        String fileDir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment2\\realData\\seu-class\\";
//        String resultDir = "D:\\Code\\lyl_TeamFormation\\workspace_java\\Data\\Experiment2\\realData\\seu-class\\result\\";
//        experiment2Process(fileDir, resultDir);

//        System.out.println("======DataType3=======");
//        for(int i = 0; i < 5; i++) {
//            System.out.println("=============case" + (i + 1) + "=============");
//            String fileDir = "E:\\TeamFormationData\\Experiment6\\networks\\ER\\case"+ (i+1) + "\\";
//            String resultDir = "E:\\TeamFormationData\\Experiment6\\networks\\ER\\case"+ (i+1) + "\\result\\";
//            experiment6Process(fileDir, resultDir);
//            fileDir = "E:\\TeamFormationData\\Experiment6\\networks\\WS\\case"+ (i+1) + "\\";
//            resultDir = "E:\\TeamFormationData\\Experiment6\\networks\\WS\\case"+ (i+1) + "\\result\\";
//            experiment6Process(fileDir, resultDir);
//            fileDir = "E:\\TeamFormationData\\Experiment6\\networks\\BA\\case"+ (i+1) + "\\";
//            resultDir = "E:\\TeamFormationData\\Experiment6\\networks\\BA\\case"+ (i+1) + "\\result\\";
//            experiment6Process(fileDir, resultDir);
//        }
    }

    private static void experiment2Process(String fileDir, String resultDir) {
        double[][] projectRequireMatrix = CommonUtils.ReadMatrix(fileDir+"projectRequireMatrix.csv");
        double[][] competenceGraphMatrix = CommonUtils.ReadMatrix(fileDir+"competenceGraph.csv");
        double[][] sociometricMatrix = CommonUtils.ReadMatrix(fileDir+"sociometricMatrix.csv");
        double[][] learnerCompetenceMatrix = CommonUtils.ReadMatrix(fileDir+"learnerCompetenceMatrix.csv");
        double[][] learnerLearningrateMatrix = CommonUtils.ReadMatrix(fileDir+"learnerLearningrateMatrix.csv");
        Learner.setGraph(competenceGraphMatrix);
        Learner.setProjectRequireMatrix(projectRequireMatrix);
        List<Learner> learners = generateLearners(learnerCompetenceMatrix, learnerLearningrateMatrix);

        StringBuilder sb2 = new StringBuilder();
        double resultMax = 0, aMax = 0, bMax = 0, cMax = 0;
        long execTime = 0;
        int k;
        double[] arrNum = new double[101];
        arrNum[0] = 0;
        for(int i = 1; i <= 100; i++) {
            arrNum[i] = i / 100.0;
        }
        for(int i = 0; i < arrNum.length; i++){
            for(int j = 0; j < arrNum.length; j++) {
                if(i+j > 100) break;
                k = 100-i-j;
                StringBuilder sb1 = new StringBuilder();
                long startTime = System.currentTimeMillis();
                KMeansTestArgu kMeans = new KMeansTestArgu(projectRequireMatrix, sociometricMatrix, learners);
                double result = kMeans.proceed(arrNum[i], arrNum[j], arrNum[k]);
                long endTime = System.currentTimeMillis();
                sb1.append(arrNum[i]).append("_").append(arrNum[j]).append("_").append(arrNum[k]).append(" ").append(result).append(" ").append(endTime-startTime).append("\n");
                CommonUtils.WriteTextAppend(sb1.toString(), resultDir + "result.txt");
                if(result > resultMax) {
                    resultMax = result;
                    aMax = arrNum[i];
                    bMax = arrNum[j];
                    cMax = arrNum[k];
                    execTime = endTime - startTime;
                }
            }
        }
        sb2.append(aMax).append("_").append(bMax).append("_").append(cMax).append(" ").append(execTime).append(" ").append(resultMax).append("\n");
        CommonUtils.WriteTextAppend(sb2.toString(), resultDir + "resultMax.txt");
    }

    private static double execAlgorithm(int index, String[] evolutionResultPath, String[] allocationResultPath, double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners, int dataNo) {
        double systemEvolution = 0;
        switch (index) {
            case(0):
                BruteForce bruteForce = new BruteForce(projectRequireMatrix, sociometricMatrix, learners);
                systemEvolution = bruteForce.proceed(evolutionResultPath[0], allocationResultPath[0]);
                break;
            case(1):
                RandomMethod randomMethod = new RandomMethod(projectRequireMatrix, sociometricMatrix, learners);
                systemEvolution = randomMethod.proceed(evolutionResultPath[1], allocationResultPath[1]);
                break;
            case(2):
                GraphPartition graphPartition = new GraphPartition(projectRequireMatrix, sociometricMatrix, learners, dataNo);
                systemEvolution = graphPartition.proceed(evolutionResultPath[2], allocationResultPath[2]);
                break;
            case(3):
                SimulateAnneal simulateAnneal = new SimulateAnneal(projectRequireMatrix, sociometricMatrix, learners);
                systemEvolution = simulateAnneal.proceed(evolutionResultPath[3], allocationResultPath[3]);
                break;
            case(4):
                double[] a = {1, 0.8, 1, 0.9, 0.6, 0.1, 0, 0.3, 0.6};
                double[] b = {1, 0.2, 1, 0.2, 0.3, 0.6, 1, 0.9, 0.5};
                double[] c = {1, 0.5, 1, 0.8, 0.5, 0.4, 1, 1.0, 0.4};
                KMeans kMeans = new KMeans(projectRequireMatrix, sociometricMatrix, learners);
                systemEvolution = kMeans.proceed(evolutionResultPath[4], allocationResultPath[4], a[dataNo-1], b[dataNo-1], c[dataNo-1]);
                break;
            default:
                break;
        }
        return systemEvolution;
    }

    private static void outputGraphToMetis(int learnerNum, int projectNum, double[][] sociometricMatrix, int dataNo) {
        int vertexNum = learnerNum;
        int edgeNum = learnerNum * (learnerNum-1) / 2;
        List<List<Integer>> arrList = new ArrayList<>();
        List<Integer> head = new ArrayList<>();
        head.add(vertexNum);
        head.add(edgeNum);
        head.add(projectNum);
        arrList.add(head);
        for(int i=0; i<learnerNum; i++) {
            List<Integer> temp = new ArrayList<>();
            for(int j=0; j<learnerNum; j++) {
                if(i!=j) {
                    temp.add(j);
                    temp.add((int) ((sociometricMatrix[i][j] + sociometricMatrix[j][i]) * 10));
                }
            }
            arrList.add(temp);
        }
        String writePath = "D:\\Code\\lyl_TeamFormation\\workspace_c++\\Data\\Experiment1\\case" + dataNo + "\\graph.txt";
        CommonUtils.WriteText(arrList, writePath);
    }

    private static void outputGraphToMetis(String dataName, int learnerNum, int projectNum, double[][] sociometricMatrix, int dataNo) {
        int vertexNum = learnerNum;
        int edgeNum = learnerNum * (learnerNum-1) / 2;
        List<List<Integer>> arrList = new ArrayList<>();
        List<Integer> head = new ArrayList<>();
        head.add(vertexNum);
        head.add(edgeNum);
        head.add(projectNum);
        arrList.add(head);
        for(int i=0; i<learnerNum; i++) {
            List<Integer> temp = new ArrayList<>();
            for(int j=0; j<learnerNum; j++) {
                if(i!=j) {
                    temp.add(j);
                    if(sociometricMatrix[i][j] == 0) {
                        temp.add(1);
                    } else {
                        temp.add(1000);
                    }
//                    temp.add((int) ((sociometricMatrix[i][j] + sociometricMatrix[j][i]) * 10));
                }
            }
            arrList.add(temp);
        }
        String writePath = "E:\\workspace_c++\\GraphComposition\\GraphComposition\\data\\Experiment4\\" + dataName + "\\case" + dataNo + "\\graph.txt";
        CommonUtils.WriteText(arrList, writePath);
    }

    private static List<Learner> generateLearners(double[][] learnerCompetenceMatrix, double[][] learnerLearningrateMatrix) {
        List<Learner> learners = new ArrayList<>();
        for(int i=0; i<learnerCompetenceMatrix.length; i++) {
            Learner learner = new Learner(learnerCompetenceMatrix[i], learnerLearningrateMatrix[i]);
            learners.add(learner);
        }
        return learners;
    }
}
