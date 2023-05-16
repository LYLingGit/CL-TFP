package cn.edu.seu.utils;

import java.util.Random;

public class AllocationUtils {
    public static int[][] allocateLearnersRandomly(int learnerNum, int projectNum) {
        Random random = new Random();
        int[][] allocationMatrix = new int[learnerNum][projectNum];
        for(int i = 0; i<learnerNum; i++) {
            int index = random.nextInt(projectNum+1);
            if(index < projectNum) {
                allocationMatrix[i][index] = 1;
            }
        }
        return allocationMatrix;
    }

    //一个人每种项目都可能参加(projectNum)，或者都不参加(projectNum+1)
    public static int[][] allocateRangeMatrix(int projectNum) {
        int[][] rangeMatrix = new int[projectNum+1][projectNum];
        for(int i=0; i<projectNum;i++) {
            rangeMatrix[i][i] = 1;
        }
        return rangeMatrix;
    }
}
