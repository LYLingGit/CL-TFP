package cn.edu.seu.entity;

import java.util.Arrays;

public class Learner {
    double[] competences;   //每个技能的能力值
    double[] lr;            //每个技能的学习率
    double[] evolution;     //每个技能的演化水平
    double[] evaluationProject;     //每个能力者在项目上会有多少演化水平（不考虑社会计量矩阵）
    double willingness;
    static double[][] competenceGraph;
    static double[][] projectRequireMatrix;

    public Learner(double[] learnerCompetenceMatrix, double[] learnerLearningrateMatrix) {
        competences = learnerCompetenceMatrix;
        lr = learnerLearningrateMatrix;
        evolution = new double[competences.length];
        calculateEvolution();
        willingness = 0;
    }

    public static void setGraph(double[][] graph) {
        competenceGraph = graph;
    }

    public static void setProjectRequireMatrix(double[][] projectRequireMatrix) {
        Learner.projectRequireMatrix = projectRequireMatrix;
    }

    public void setWillingness(double willingness) {
        this.willingness = willingness;
    }

    public double getWillingness() {
        return willingness;
    }

    public double getCompetence(int competenceIndex) {
        return competences[competenceIndex];
    }

    public double getEvolution(int evolutionIndex) {
        return evolution[evolutionIndex];
    }

    public double getEvaluationProject(int projectIndex) {
        return evaluationProject[projectIndex];
    }

    public double[] getEvaluationProject() {
        return evaluationProject;
    }

    //评估每个人在项目上的演化
    public void calculateLearnerEvolutionProject() {
        int projectNum = projectRequireMatrix.length;
        int competenceNum = projectRequireMatrix[0].length;
        evaluationProject = new double[projectNum];
        for(int i=0; i<projectNum;i++) {
            for(int j=0; j<competenceNum; j++) {
                if(projectRequireMatrix[i][j]>0) {
                    evaluationProject[i] += evolution[j];
                }
            }
        }
    }

    private void calculateEvolution() {
        int competenceNum = competenceGraph.length;
        for(int i=0; i<competenceNum; i++) {
            for(int j=0; j<= i; j++) {
                if(competenceGraph[i][j] > 0) {
                    if(i != j) {    //competenceGraph[i][j]当前能力依赖的权重
                        evolution[i] += competenceGraph[i][j] * evolution[j];
                    } else {        //competenceGraph[i][j]当前能力自身的权重
                        evolution[i] += competenceGraph[i][j] * competences[i] * lr[i];
                    }
                }
            }
        }
    }


}
