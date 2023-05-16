package cn.edu.seu.utils;

import java.util.Random;

public class GenerateNetworks {
    //N个节点，p的概率有连边
    public static double[][] generateERRandomNetwork(int N, double p) {
        Random random = new Random();
        double[][] network = new double[N][N];
        int edges = 0;
        for(int i=1; i<N; i++) {
            for(int j=0; j<i; j++) {
                double randNum = random.nextDouble();
                if(randNum < p) {
                    network[i][j] = 1;
                    network[j][i] = 1;
                    edges += 1;
                } else {
                    network[i][j] = 0.1;
                    network[j][i] = 0.1;
                }
            }
        }
        double avgDegree = edges * 1.0 / N;
        System.out.println("ER图 p:" + p + ",平均度:" + avgDegree);
        return network;
    }

    public static double[][] generateBANetwork(int N, int inititalNodes, double p, int addEdges) {
        Random random = new Random();
        double[][] network = new double[N][N];
        int degreeSum = 0;
        int[] degree = new int[N];
        for(int i=0; i<N; i++) {
            for(int j=i+1; j<N; j++) {
                network[i][j] = 0.1;
                network[j][i] = 0.1;
            }
        }
        for(int i=0; i<inititalNodes; i++) {
            for(int j=i+1; j<inititalNodes; j++) {
                double randNum = random.nextDouble();
                if(randNum < p) {
                    network[i][j] = 1;
                    network[j][i] = 1;
                    degree[i] += 1;
                    degree[j] += 1;
                    degreeSum += 2;
                }
            }
        }
        for(int i=inititalNodes; i<N; i++) {
            for(int j=0; j<addEdges; j++) {
                //得到一个概率密度和矩阵
                double[] probabilities = calculateProbability(degreeSum, degree);
                double probability = random.nextDouble();
                for(int k=0; k<inititalNodes; k++) {
                    if(probability <= probabilities[k]) {
                        if(network[i][k] == 0.1) {
                            network[i][k] = 1;
                            network[k][i] = 1;
                            degree[i] += 1;
                            degree[k] += 1;
                            degreeSum += 2;
                            break;
                        }
                    }
                }
            }
        }
        double avgDegree = degreeSum * 1.0 / 2 / N;
        System.out.println("BA图 addEdges:" + addEdges + ",平均度:" + avgDegree);
        return network;
    }

    private static double[] calculateProbability(int degreeSum, int[] degree) {
        int degrees = 0;
        int index = 0;
        double[] probabilities = new double[degree.length];
        while (degrees < degreeSum) {
            if(index == 0) {
                probabilities[index] = degree[index] * 1.0 / degreeSum;
                degrees += degree[index];
            } else {
                probabilities[index] = degree[index] * 1.0 / degreeSum + probabilities[index-1];
                degrees += degree[index];
            }
            index++;
        }
        probabilities[index-1] = 1;
        return probabilities;
    }

    public static double[][] generateWSNetwork(int N, int K, double P) {
        Random random = new Random();
        double[][] network = new double[N][N];
        for(int i=0; i<N; i++) {
            for(int j=i+1; j<N; j++) {
                network[i][j] = 0.1;
                network[j][i] = 0.1;
            }
        }
        for (int i = 0; i < N; i++)
        {
            for (int j = 1; j <= K / 2; j++)
            {
                network[i][(i + j) % N] = 1;
                network[(i + j) % N][i] = 1;
            }
        }
        // p概率重连每条边，不得有自环和重边
        for (int i = 0; i < N; i++)
        {
            for (int j = i + 1; j <= i + K / 2; j++)
            {
                double p = random.nextDouble();
                if (p < P)                                                       //重连
                {
                    int k;
                    while (true) //随机选择一个端点：不可以产生自连和重边
                    {
                        k = random.nextInt(N);
                        if (network[i][k] == 0.1 && k != i)
                            break;
                    }
                    network[i][j % N] = 0.1;
                    network[j % N][i] = 0.1;
                    network[i][k] = 1;
                    network[k][i] = 1;
                }
            }
        }
        int edges = 0;
        for (int i = 0; i < N; ++i)
            for (int j = 0; j < N; ++j)
                if (network[i][j] == 1)
                    edges++;
        double avgDegree = edges * 1.0 / N;
        System.out.println("WS图 K:" + K + ",平均度:" + avgDegree);
        return network;
    }
}
