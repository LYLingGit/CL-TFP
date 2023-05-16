package cn.edu.seu.algorithm;

import cn.edu.seu.entity.Learner;
import cn.edu.seu.utils.AllocationUtils;
import cn.edu.seu.utils.CalculationUtils;
import cn.edu.seu.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KMeansTestArgu {
    double[][] projectRequireMatrix;
    double[][] sociometricMatrix;
    List<Learner> learners;
    int projectNum;
    int learnerNum;
    int competenceNum;
    double[] clusters;
    //分配矩阵
    int[][] allocateMatrix;
    int[][] rangeMatrix;
    boolean[] isSelected;

    public KMeansTestArgu(double[][] projectRequireMatrix, double[][] sociometricMatrix, List<Learner> learners) {
        this.projectRequireMatrix = projectRequireMatrix;
        this.sociometricMatrix = sociometricMatrix;
        this.learners = learners;
        projectNum = projectRequireMatrix.length;
        competenceNum = projectRequireMatrix[0].length;
        learnerNum = sociometricMatrix.length;
        allocateMatrix = new int[learnerNum][projectNum];
        clusters = new double[projectNum];
        rangeMatrix = AllocationUtils.allocateRangeMatrix(projectNum);
        isSelected = new boolean[learnerNum];
    }

    public double proceed(double a, double b, double c) {
        CalculationUtils.setParamaters(projectRequireMatrix, sociometricMatrix, learners);
        InitialLearners();
        InititalClusters();
        InitialKMeans();

        boolean[] isSatisfied;
        double[][] competenceDistance = calculateCompetenceDistanceOrder();
        double[][] socioDistance;
        int iteratorNum = 0;
        while (iteratorNum < 10) {
            iteratorNum++;
            CalculationUtils.calculateWillingness(allocateMatrix);
            isSatisfied = CalculationUtils.judgeProjectRequirement(allocateMatrix);
            boolean flag = true;
            for(int i = 0; i < projectNum; i++) {
                if (!isSatisfied[i]) {
                    flag = false;
                    break;
                }
            }
            if(flag) break;
            else {
                socioDistance = calculateSocioDistanceOrder();
                adjustProjectMemberBasedRequirement(isSatisfied, competenceDistance, socioDistance, a, b, c);
            }
        }
        socioDistance = calculateSocioDistanceOrder();
        return adjustProjectMemberBasedSocio(socioDistance);
    }

    private double adjustProjectMemberBasedSocio(double[][] socioDistance) {
        int[] projectIndex = new int[learnerNum];
        for(int i = 0; i < projectNum; i++) {
            for(int j = 0; j < learnerNum; j++) {
                if(allocateMatrix[j][i] == 1) {
                    projectIndex[j] = i;
                }
            }
        }
        double curEvolution = calculateEvolution();
        double changedEvolution;
        for(int i = 0; i < projectNum; i++) {
            for(int j = 0; j < learnerNum; j++) {
                if(socioDistance[i][j] > 0) {
                    allocateMatrix[j][i] = 1;
                    allocateMatrix[j][projectIndex[j]] = 0;
                    changedEvolution = calculateEvolution();
                    if(changedEvolution > curEvolution) {
                        curEvolution = changedEvolution;
                        projectIndex[j] = i;
                    } else {
                        allocateMatrix[j][i] = 0;
                        allocateMatrix[j][projectIndex[j]] = 1;
                    }
                }
            }
        }
        return curEvolution;
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

    private void adjustProjectMemberBasedRequirement(boolean[] isSatisfied, double[][] competenceDistance, double[][] socioDistance, double a, double b, double c) {
        for(int i = 0; i < projectNum; i++) {
            if(!isSatisfied[i]) {
                double[] projectDistance = calculateProjectDistance(i);
                double[] weightDistance = new double[learnerNum];
                for(int j = 0; j < learnerNum; j++) {
                    weightDistance[j] = a * competenceDistance[i][j] + b * socioDistance[i][j] + c * projectDistance[j];
                }
                int[] sortedProjectOrder = CommonUtils.sortReturnIndex(weightDistance);
                for(int j = 0; j < learnerNum; j++) {
                    if(allocateMatrix[sortedProjectOrder[j]][i] == 0) {
                        int preProject = 0;
                        for(; preProject < projectNum; preProject++) {
                            if(allocateMatrix[sortedProjectOrder[j]][preProject] == 1) {
                                allocateMatrix[sortedProjectOrder[j]][preProject] = 0;
                                allocateMatrix[sortedProjectOrder[j]][i] =1;
                                break;
                            }
                        }
                        // 调过去以后，是否满足项目需求
                        if(CalculationUtils.isSatisfiedRequired(allocateMatrix, preProject)) {
                            break;
                        } else {
                            allocateMatrix[sortedProjectOrder[j]][i] =0;
                            allocateMatrix[sortedProjectOrder[j]][preProject] = 1;
                        }
                        if(CalculationUtils.isSatisfiedRequired(allocateMatrix, i)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private double[] calculateProjectDistance(int toProject) {
        double[] projectDistance = new double[learnerNum];
        double sum = 0;
        for(int i = 0; i < learnerNum; i++) {
            for(int j = 0; j < projectNum; j++) {
                if(allocateMatrix[i][j] == 1) {
                    projectDistance[i] = learners.get(i).getEvaluationProject(j) - learners.get(i).getEvaluationProject(toProject);
                    sum += projectDistance[i] * projectDistance[i];
                }
            }
        }
        sum = Math.sqrt(sum);
        for(int i = 0; i < learnerNum; i++) {
            projectDistance[i] = 1 - projectDistance[i] / sum;
        }
        return projectDistance;
    }

    private double[][] calculateSocioDistanceOrder() {
        Map<Integer, List<Integer>> map = new HashMap<>();
        int[] projectIndex = new int[learnerNum];
        for(int i = 0; i < projectNum; i++) {
            map.put(i, new ArrayList<>());
            for(int j = 0; j < learnerNum; j++) {
                if(allocateMatrix[j][i] == 1) {
                    projectIndex[j] = i;
                    map.get(i).add(j);
                }
            }
        }
        double[][] socioDistance = new double[projectNum][learnerNum];
        for(int i = 0; i < learnerNum; i++) {
            double reduce = calculateReduce(map.get(projectIndex[i]), i);
            for(int j = 0; j < projectNum; j++) {
                double increase = calculateIncrease(map.get(j), i);
                socioDistance[j][i] = increase + reduce;
            }
        }
        for(int i = 0; i < projectNum; i++) {
            double sum = 0;
            for(int j = 0; j < learnerNum; j++) {
                if(i != projectIndex[i]) {
                    sum += socioDistance[i][j] * socioDistance[i][j];
                }
            }
            sum = Math.sqrt(sum);
            for(int j = 0; j < learnerNum; j++) {
                socioDistance[i][j] /= sum;
            }
        }
        return socioDistance;
    }

    private double calculateIncrease(List<Integer> list, int selected) {
        if (list.size() == 0) return 0;
        double currentSum = 0;
        double originalSum = 0;
        for(int learner : list) {
            originalSum += learners.get(learner).getWillingness();
            currentSum += learners.get(learner).getWillingness() * list.size() + sociometricMatrix[learner][selected] + sociometricMatrix[selected][learner];
        }
        return currentSum / (list.size()+1) - originalSum;
    }

    private double calculateReduce(List<Integer> list, int selected) {
        if (list.size() <= 1) return 0;
        double currentSum = 0;
        double originalSum = 0;
        for(int learner : list) {
            originalSum += learners.get(learner).getWillingness();
            if(learner == selected) continue;
            currentSum += learners.get(learner).getWillingness() * list.size() - sociometricMatrix[learner][selected];
        }
        return currentSum / (list.size()-1) - originalSum;
    }

    private double[][] calculateCompetenceDistanceOrder() {
        double[][] competenceDistance = new double[projectNum][learnerNum];
        for(int i = 0; i < projectNum; i++) {
            double sum = 0;
            for(int j = 0; j < learnerNum; j++) {
                for(int k = 0; k < competenceNum; k++) {
                    competenceDistance[i][j] += learners.get(j).getCompetence(k) - projectRequireMatrix[i][k];
                    sum += competenceDistance[i][j] * competenceDistance[i][j];
                }
            }
            sum = Math.sqrt(sum);
            for(int j = 0; j < learnerNum; j++) {
                competenceDistance[i][j] /= sum;
            }
        }
        return competenceDistance;
    }

    private void InitialKMeans() {
        for(int i = 0; i < learnerNum; i++) {
            double evolutionMax = Integer.MIN_VALUE;
            int projectIndex = -1;
            for(int j = 0; j < projectNum; j++) {
                double diffEvolution = learners.get(i).getEvaluationProject(j) - clusters[j];
                if(diffEvolution > evolutionMax) {
                    evolutionMax = diffEvolution;
                    projectIndex = j;
                }
            }
            allocateMatrix[i][projectIndex] = 1;
        }
    }

    private void InititalClusters() {
        for(int i = 0; i < learnerNum; i++) {
            for(int j = 0; j < projectNum; j++) {
                clusters[j] = learners.get(i).getEvaluationProject(j);
            }
        }
        for(int i = 0; i < projectNum; i++) {
            clusters[i] /= learnerNum;
        }
    }

    private void InitialLearners() {
        for(int i=0; i<learnerNum; i++) {
            learners.get(i).calculateLearnerEvolutionProject();
        }
    }

}
