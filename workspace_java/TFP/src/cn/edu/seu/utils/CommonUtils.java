package cn.edu.seu.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommonUtils {
    // 例：[4, 2, 5, 1, 3]
    // 返回：[2, 0, 4, 1, 3]
    public static int[] sortReturnIndex(double[] arr) {
        double[] arrCopy = arr.clone();
        Arrays.sort(arrCopy);
        int length = arr.length;
        int[] indexes = new int[length];
        //防止arr有重复元素，记录这个数是否加入了indexes
        boolean[] flags = new boolean[length];
        //遍历arrCopy，排好序的
        for(int i=0; i<length; i++) {
            //遍历arr
            for(int j=0; j<length; j++) {
                if(!flags[j]) {
                    if(arrCopy[i] == arr[j]) {
                        flags[j] = true;
                        indexes[length - i - 1] = j;
                        break;
                    }
                }
            }
        }
        return indexes;
    }

    public static double[][] ReadMatrix(String path) {
        try {
            BufferedReader in =new BufferedReader(new InputStreamReader(new FileInputStream(path),"gbk"));
            String line;
            String[] onerow;
            double[] row;
            List<double[]> list = new ArrayList<>();
            while ((line=in.readLine())!=null) {
                onerow = line.split(",");
                row = new double[onerow.length];
                for(int i=0; i < onerow.length; i++) {
                    row[i] = Double.parseDouble(onerow[i]);
                }
                list.add(row);
            }
            double[][] matrix = new double[list.size()][];
            for(int i=0; i<list.size();i++) {
                matrix[i] = list.get(i);
            }
            return matrix;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double[][] ReadSocioMatrixFromTxt(String path, int learnerNum) {
        try {
            BufferedReader in =new BufferedReader(new InputStreamReader(new FileInputStream(path),"gbk"));
            String line;
            String[] onerow;
            int size = Integer.parseInt(in.readLine());
            double[][] sociometricMatrix = new double[learnerNum][learnerNum];
            for(int i=0; i<learnerNum; i++) {
                for(int j=0; j<learnerNum; j++) {
                    Random random = new Random();
                    if(i==j) continue;
                    sociometricMatrix[i][j] = 0;
                }
            }
            while ((line=in.readLine())!=null) {
                onerow = line.split(" ");
                int a = Integer.parseInt(onerow[0]);
                int b = Integer.parseInt(onerow[1]);
                if(a != b && a < learnerNum && b < learnerNum) {
                    sociometricMatrix[a][b] = 1;
                }
            }
            return sociometricMatrix;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void WriteMatrix(double[][] matrix, String dir, String filename) {
        //E:\TeamFormationData\case1
        File file = new File(dir,filename);
        //构建输出流，同时指定编码
        OutputStreamWriter ow;
        try {
            ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
            for (double[] tmp : matrix) {
                for (int j = 0; j < matrix[0].length; j++) {
                    ow.write(String.valueOf(tmp[j]));
                    ow.write(",");
                }
                ow.write("\r\n");
            }
            ow.flush();
            ow.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //导出List到txt文件
    public static void WriteText(List<List<Integer>> list, String path)
    {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"gbk"));
            for(List<Integer> arrList : list) {
                for(int i=0; i<arrList.size(); i++) {
                    if(i == arrList.size()-1)
                        out.write(String.valueOf(arrList.get(i)));
                    else
                        out.write(arrList.get(i) +" ");
                }
                out.newLine();
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //导出字符串到txt文件
    public static void WriteText(String str, String path)
    {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"gbk"));
            out.write(str);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[][] ReadText(String path) {
        try {
            BufferedReader in =new BufferedReader(new InputStreamReader(new FileInputStream(path),"gbk"));
            String line;
            String[] onerow;
            int[] row;
            List<int[]> list = new ArrayList<>();
            while ((line=in.readLine())!=null) {
                onerow = line.split(" ");
                row = new int[onerow.length];
                for(int i=0; i < onerow.length; i++) {
                    row[i] = Integer.parseInt(onerow[i]);
                }
                list.add(row);
            }
            int[][] matrix = new int[list.size()][];
            for(int i=0; i<list.size();i++) {
                matrix[i] = list.get(i);
            }
            return matrix;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int ReadTextTime(String path) {
        try {
            BufferedReader in =new BufferedReader(new InputStreamReader(new FileInputStream(path),"gbk"));
            String line = in.readLine();
            return (int) Double.parseDouble(line);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //导出字符串到txt文件
    public static void WriteTextAppend(String str, String path)
    {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true),"gbk"));
            out.write(str);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
