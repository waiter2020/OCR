package pub.upc.ocr.my_switch;


import java.util.ArrayList;

public class Transform2 {
    public static int len = 20;
    public static double minRowRate = 0.5;
    public static double maxRowRate = 1.8;
    public static double minColRate = 0.5;
    public static double maxColRate = 3;
    public static double rowErrorRate = 0.5;
    public static double colErrorRate = 0.5;

    public static MyMatrix stringToMatirix(String src) {
        // TODO Auto-generated method stub
        MyMatrix result = new MyMatrix();
        String[] strList = src.split("#");
        int[][] array = new int[100][6];  //bbox的id，xmin,ymin,xmax,ymax和状态
        int count = 0;
        System.out.print("图片中目标总个数： ");
        System.out.println(strList.length);
        for (int i = 0; i < strList.length; ++i) {
            array[count][0] = i;
            String[] temp = strList[i].split(" ");
            for (int j = 0; j < 6; ++j) {
                switch (j) {
                    case 0:
                        if (temp[j].equals("ON"))
                            array[count][5] = 1;
                        else if (temp[j].equals("OFF"))
                            array[count][5] = 2;
                        break;
                    case 2:
                        array[count][1] = Integer.parseInt(temp[j]);
                        break;
                    case 3:
                        array[count][2] = Integer.parseInt(temp[j]);
                        break;
                    case 4:
                        array[count][3] = Integer.parseInt(temp[j]);
                        break;
                    case 5:
                        array[count][4] = Integer.parseInt(temp[j]);
                        break;
                    default:
                        break;
                }
            }
            ++count;
        }

        int[][] myArray = new int[count][4];   //id,中心点的x,y坐标，状态
        int sum = 0, aveHeight = 0;
        for (int i = 0; i < count; ++i) {
            myArray[i][0] = array[i][0]; //bbox的id
            myArray[i][1] = (array[i][1] + array[i][3]) / 2; //中心点x坐标
            myArray[i][2] = (array[i][2] + array[i][4]) / 2; //中心点y坐标
            myArray[i][3] = array[i][5]; //目标的分类
            sum = sum + (array[i][4] - array[i][2]); //bbox高的和
        }

        aveHeight = sum / count;
        System.out.print("图片中bbox的平均高度： ");
        System.out.println(aveHeight);
        int[] leastXY = findLeast(myArray, aveHeight);
        for (int i = 0; i < leastXY.length; ++i) {
            System.out.print(leastXY[i] + " ");
        }
        System.out.println();
        int[][] minRow = findMinRow(myArray, leastXY, aveHeight);
        ArrayList<ArrayList<int[]>> arrayListToMatrix = new ArrayList<>();
        // int lenOfCol = 0;
        for (int i = 0; i < minRow.length; ++i) {
            int[] Ymin = minRow[i];
            ArrayList<int[]> theCol = findCol(myArray, Ymin, aveHeight);
            //   lenOfCol = theCol.size();
            arrayListToMatrix.add(theCol);
        }
        int[][][] temp = new int[20][20][4];
        for (int i = 0; i < arrayListToMatrix.size(); ++i) {
            int[][] a = arrayListToMatrix.get(i).toArray(new int[20][4]);
            //System.out.println(a[0][0]);
            temp[i] = a;
            //	System.out.println(temp[i][0][0]);
        }
        int[][][] matrix = new int[len][len][2];
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 20; ++j) {
                try {
                    matrix[j][i][0] = temp[i][j][0];
                    matrix[j][i][1] = temp[i][j][3];
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    //System.out.println();
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j][1] + " ");
            }
            System.out.println();    //换行
        }
        result.coordinates = array;
        result.matrix = matrix;
        return result;
    }

    public static int[] findLeast(int myArray[][], int aveHeight) {  //先行后列
        ArrayList<int[]> min = new ArrayList<>();
        int[] tempArray = new int[4];
        int[] minArray = {10000, 10000, 10000, 0};
        int minX = 10000, //x坐标的最小值
                minY = 10000,
                tagX = 0, //x坐标取得最小值时的标号
                tagY = 0; //y坐标取得最小值时的标号
        for (int i = 0; i < myArray.length; ++i) { //找到y坐标最小的那个点
            if (myArray[i][2] < minY) {
                minY = myArray[i][2];
                tagY = i;
            }
        }
        tempArray = myArray[tagY];
        min.add(tempArray);
        //  System.out.println(tempArray[0]+"  "+myArray[tagY][3]);
        while (true) {          //向右找同一行的
            int count = 0, tag = 0;
            ArrayList<int[]> near = new ArrayList<>(4);
            for (int i = 0; i < myArray.length; ++i) {
                if ((Math.abs(tempArray[1] - myArray[i][1]) > (aveHeight * minRowRate))
                        && (Math.abs(tempArray[1] - myArray[i][1]) < (aveHeight * maxRowRate))
                        && ((tempArray[1] - myArray[i][1]) < 0)
                        && (Math.abs(tempArray[2] - myArray[i][2]) < (aveHeight) * colErrorRate)
                        && ((Math.pow((tempArray[1] - myArray[i][1]), 2) + (Math.pow((tempArray[2] - myArray[i][2]), 2))) > Math.pow(aveHeight / 2, 2))   //消除重复的框
                        && (tempArray[0] != myArray[i][0])) {
                    //    System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
                    near.add(myArray[i]);
                    tag = 1;
                }

            }
            ++count;
            if (tag == 0 || count > myArray.length)
                break;
            tempArray = findRRNearest(near);
            min.add(tempArray);

        }
        tempArray = myArray[tagY];
        while (true) {          ////向左查找同一行的
            int count = 0, tag = 0;
            ArrayList<int[]> near = new ArrayList<>(4);
            for (int i = 0; i < myArray.length; ++i) {
                if ((Math.abs(tempArray[1] - myArray[i][1]) > (aveHeight * minRowRate))
                        && (Math.abs(tempArray[1] - myArray[i][1]) < (aveHeight * maxRowRate))
                        && ((tempArray[1] - myArray[i][1]) > 0)
                        && (Math.abs(tempArray[2] - myArray[i][2]) < (aveHeight * colErrorRate))
                        && ((Math.pow((tempArray[1] - myArray[i][1]), 2) + (Math.pow((tempArray[2] - myArray[i][2]), 2))) > Math.pow(aveHeight / 2, 2))   //消除重复的框
                        && (tempArray[0] != myArray[i][0])) {
                    //    System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
                    near.add(myArray[i]);
                    tag = 1;
                }

            }
            if (tag == 0 || count > myArray.length)
                break;
            tempArray = findRRNearest(near);
            min.add(tempArray);
            ++count;

        }


        int[][] temp = min.toArray(new int[min.size()][4]);
       /* System.out.println("*************************************");
        for(int i=0;i<temp.length;i++)
        {
            for(int j=0;j<temp[i].length;j++)
            {
                System.out.print(temp[i][j]+" ");
            }
            System.out.println();	//换行
        }
        System.out.println("*************************************");*/

        for (int i = 0; i < temp.length; ++i) {
            if ((temp[i][1] + temp[i][2]) < (minArray[1] + minArray[2])) {
                minArray = temp[i];
                tagX = i;
            }
        }
        return temp[tagX];
    }


    public static int[][] findMinRow(int myArray[][], int leastXY[], int aveHeight) {  //找出最上面一行并排序
        int[] tempArray = new int[4];
        tempArray = leastXY;
        ArrayList<int[]> minRow = new ArrayList<>();
        minRow.add(tempArray);
        while (true) {          //向右找同一行的
            int count = 0, tag = 0;
            ArrayList<int[]> near = new ArrayList<>(4);
            for (int i = 0; i < myArray.length; ++i) {
                if ((Math.abs(tempArray[1] - myArray[i][1]) > (aveHeight * minRowRate))
                        && (Math.abs(tempArray[1] - myArray[i][1]) < (aveHeight * maxRowRate))
                        && ((tempArray[1] - myArray[i][1]) < 0)
                        && (Math.abs(tempArray[2] - myArray[i][2]) < (aveHeight) * colErrorRate)
                        && ((Math.pow((tempArray[1] - myArray[i][1]), 2) + (Math.pow((tempArray[2] - myArray[i][2]), 2))) > Math.pow(aveHeight / 2, 2))   //消除重复的框
                        && (tempArray[0] != myArray[i][0])) {
                    //    System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
                    near.add(myArray[i]);
                    tag = 1;
                }
            }
            if (tag == 0 || count > myArray.length)
                break;
            tempArray = findRRNearest(near);
            minRow.add(tempArray);
            ++count;

        }
        tempArray = leastXY;
        while (true) {          ////向左查找同一行的
            int count = 0, tag = 0;
            ArrayList<int[]> near = new ArrayList<>(4);
            for (int i = 0; i < myArray.length; ++i) {
                if ((Math.abs(tempArray[1] - myArray[i][1]) > (aveHeight * minRowRate))
                        && (Math.abs(tempArray[1] - myArray[i][1]) < (aveHeight * maxRowRate))
                        && ((tempArray[1] - myArray[i][1]) > 0)
                        && (Math.abs(tempArray[2] - myArray[i][2]) < (aveHeight * colErrorRate))
                        && ((Math.pow((tempArray[1] - myArray[i][1]), 2) + (Math.pow((tempArray[2] - myArray[i][2]), 2))) > Math.pow(aveHeight / 2, 2))   //消除重复的框
                        && (tempArray[0] != myArray[i][0])) {
                    //    System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
                    near.add(myArray[i]);
                    tag = 1;
                }
            }
            if (tag == 0 || count > myArray.length)
                break;
            tempArray = findRLNearest(near);
            minRow.add(tempArray);
            ++count;

        }

        int[][] minYRow = minRow.toArray(new int[minRow.size()][3]);//冒泡排序
        //System.out.println();	//换行
        int[] temp = minYRow[0];
        System.out.println("基准行的长度： " + minYRow.length);

        for (int i = (minYRow.length - 1); i > 0; i--) {

            for (int j = 0; j < i; j++) {
                if (minYRow[j][1] > minYRow[j + 1][1]) {
                    temp = minYRow[j + 1];
                    minYRow[j + 1] = minYRow[j];
                    minYRow[j] = temp;
                }
            }
        }
        return minYRow;
    }


    public static ArrayList<int[]> findCol(int array[][], int minY[], int aveHeight) { //找到最上面的那个元素对应的一列
        ArrayList<int[]> col = new ArrayList<>();
        int[] tempArray = minY;
        col.add(minY);
        while (true) {
            int count = 0, tag = 0;
            ArrayList<int[]> near = new ArrayList<>(4);
            for (int i = 0; i < array.length; ++i) {
                if ((Math.abs(tempArray[2] - array[i][2]) > (aveHeight * minColRate))
                        && (Math.abs(tempArray[2] - array[i][2]) < (aveHeight * maxColRate))
                        && ((tempArray[2] - array[i][2]) < 0)
                        && (Math.abs(tempArray[1] - array[i][1]) < (aveHeight * colErrorRate))
                        && ((Math.pow((tempArray[1] - array[i][1]), 2) + (Math.pow((tempArray[2] - array[i][2]), 2))) > Math.pow(aveHeight / 2, 2))    //消除重复的框
                        && (tempArray[0] != array[i][0])) {
                    near.add(array[i]);
                    tag = 1;
                }
            }
            if (tag == 0 || count > array.length)
                break;
            tempArray = findCDNearest(near);
            col.add(tempArray);
            ++count;

        }
        for (int i = 0; i < (col.size() - 1); ++i) {
            //   System.out.println("11");
            for (int j = (col.size() - 1); j > 0; --j) {
                int[] a = col.get(j);
                int[] b = col.get(j - 1);
                //System.out.println(a[1]);
                if (a[2] < b[2]) {
                    col.set(j - 1, a);
                    col.set(j, b);
                }
            }
        }
        //     System.out.println("列的长度： "+col.size()+"  "+minY[0]);
        return col;
    }

    /*相邻的几个点中找到最近的*/
    public static int[] findRLNearest(ArrayList<int[]> near) {
        int x = 0;
        for (int i = 0; i < near.size(); ++i) {
            if (near.get(x)[1] > near.get(x)[1]) {
                x = i;
            }
        }
        return near.get(x);

    }

    public static int[] findRRNearest(ArrayList<int[]> near) {
        int x = 0;
        for (int i = 0; i < near.size(); ++i) {
            if (near.get(x)[1] < near.get(x)[1]) {
                x = i;
            }
        }
        return near.get(x);

    }

    public static int[] findCUNearest(ArrayList<int[]> near) {
        int x = 0;
        for (int i = 0; i < near.size(); ++i) {
            if (near.get(x)[2] < near.get(x)[2]) {
                x = i;
            }
        }
        return near.get(x);

    }

    @org.jetbrains.annotations.Contract(pure = true)
    public static int[] findCDNearest(ArrayList<int[]> near) {
        int x = 0;
        for (int i = 0; i < near.size(); ++i) {
            if (near.get(x)[2] > near.get(x)[2]) {
                x = i;
            }
        }
        return near.get(x);

    }


}
