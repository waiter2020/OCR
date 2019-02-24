package pub.upc.ocr.my_switch;

import java.util.ArrayList;

public class Transform {
	public static int len = 12;
	public static double minRowRate = 0.5;
	public static double maxRowRate = 1.8;
	public static double minColRate = 0.5;
	public static double maxColRate = 3;
	public static double rowErrorRate = 0.5;
	public static double colErrorRate = 0.5;
	public static MyMatrix stringToMatirix(String src) {
		MyMatrix result = new MyMatrix();
		String[] strList = src.split("#");
		int[][] array = new int[100][6];  //bbox的id，xmin,ymin,xmax,ymax和状态
		int count = 0;
		System.out.print("图片中目标总个数： ");
		System.out.println(strList.length);
		for(int i = 0; i<strList.length; ++i){
			array[count][0] = i;
			String[] temp = strList[i].split(" ");
			for(int j = 0; j<6; ++j){
				switch(j){
					case 0:
						if(temp[j].equals("ON"))
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
		for(int i = 0; i<count; ++i){
			myArray[i][0] = array[i][0]; //bbox的id
			myArray[i][1] = (array[i][1] + array[i][3])/2; //中心点x坐标
			myArray[i][2] = (array[i][2] + array[i][4])/2; //中心店y坐标
			myArray[i][3] = array[i][5]; //目标的分类
			sum = sum + (array[i][4] - array[i][2]); //bbox高的和
		}

		aveHeight = sum/count;
		System.out.print("图片中bbox的平均高度： ");
		System.out.println(aveHeight);
		int[] leastXY = findLeast(myArray,aveHeight);
		for(int i = 0; i<leastXY.length; ++i){
			System.out.print(leastXY[i] + " ");
		}
		System.out.println();
		int[][] minCol = findMinColumn(myArray, leastXY, aveHeight);
		ArrayList<ArrayList<int[]>> arrayListToMatrix= new ArrayList<>();
		int lenOfRow = 0;
		for(int i = 0; i<minCol.length; ++i){
			int[] Xmin = minCol[i];
			ArrayList<int[]> theRow = findRow(myArray, Xmin, aveHeight);
			lenOfRow = theRow.size();
			arrayListToMatrix.add(theRow);
		}
		int[][][] temp = new int[10][10][4];
		for(int i = 0; i<arrayListToMatrix.size(); ++i){
			int[][] a = arrayListToMatrix.get(i).toArray(new int[10][4]);
			//System.out.println(a[0][0]);
			temp[i] = a;
			//	System.out.println(temp[i][0][0]);
		}
		int[][][] matrix = new int[len][len][2];
		for(int i = 0; i<10; ++i){
			for(int j = 0; j<10; ++j){
				try {
					matrix[i][j][0] = temp[i][j][0];
					matrix[i][j][1] = temp[i][j][3];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//System.out.println();
				}
			}
		}
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[i].length;j++)
			{
				System.out.print(matrix[i][j][1]+" ");
			}
			System.out.println();	//换行
		}
		result.coordinates = array;
		result.matrix = matrix;
		return result;
	}


	public static int[] findLeast(int myArray[][],int aveHeight){    //先列后行
		ArrayList<int[]> min = new ArrayList<>();
		int[] tempArray = new int[4];
		int[] minArray = {10000,10000,10000,0};
		int minX = 10000, //x坐标的最小值
				minY = 10000,
				tagX = 0, //x坐标取得最小值时的标号
				tagY = 0; //y坐标取得最小值时的标号
		for(int i = 0; i< myArray.length; ++i){ //找到x坐标最小的那个点
			if(myArray[i][1] < minX){
				minX = myArray[i][1];
				tagX = i;
			}
		}
		tempArray = myArray[tagX];
		min.add(tempArray);
		System.out.println(tempArray[0]+"  "+myArray[tagX][3]);
		while(true){		  //向下查找同一列的
			int count = 0, tag = 0;
			for(int i = 0; i< myArray.length; ++i){
				if((Math.abs(tempArray[2]-myArray[i][2])>(aveHeight*minColRate))
						&&(Math.abs(tempArray[2]-myArray[i][2])<(aveHeight*maxColRate))
						&&(Math.abs(tempArray[1]-myArray[i][1])<(aveHeight*colErrorRate))
						&& ((tempArray[2]-myArray[i][2])<0)
						&&((Math.pow((tempArray[1]-myArray[i][1]),2)+(Math.pow((tempArray[2]-myArray[i][2]),2)))>Math.pow(aveHeight/2,2))   //消除重复的框
						&& (tempArray[0] != myArray[i][0])){
					System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
					tempArray = myArray[i];
					min.add(tempArray);
					tag = 1;
					break;
				}
			}
			++count;
			if(tag == 0||count > myArray.length)
				break;
		}
		tempArray = myArray[tagX];
		while(true){		  ////向上查找同一列的
			int count = 0, tag = 0;
			for(int i = 0; i< myArray.length; ++i){
				if((Math.abs(tempArray[2]-myArray[i][2])>(aveHeight*minColRate))
						&&(Math.abs(tempArray[2]-myArray[i][2])<(aveHeight*maxColRate))
						&&(Math.abs(tempArray[1]-myArray[i][1])<(aveHeight*colErrorRate))
						&& ((tempArray[2]-myArray[i][2])>0)
						&&((Math.pow((tempArray[1]-myArray[i][1]),2)+(Math.pow((tempArray[2]-myArray[i][2]),2)))>Math.pow(aveHeight/2,2))   //消除重复的框
						&& (tempArray[0] != myArray[i][0])){
					System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
					tempArray = myArray[i];
					min.add(tempArray);
					tag = 1;
					break;
				}
			}
			++count;
			if(tag == 0||count > myArray.length)
				break;
		}


		int[][] temp = min.toArray(new int[min.size()][4]);
		System.out.println("*************************************");
		for(int i=0;i<temp.length;i++)
		{
			for(int j=0;j<temp[i].length;j++)
			{
				System.out.print(temp[i][j]+" ");
			}
			System.out.println();	//换行
		}
		System.out.println("*************************************");

		for(int i = 0; i<temp.length; ++i){
			if((temp[i][1]+temp[i][2])<(minArray[1]+minArray[2])){
				minArray = temp[i];
				tagY = i;
			}

		}
		return temp[tagY];
	}


	public static int[][] findMinColumn(int myArray[][],int leastXY[],int aveHeight){  //找出最左面一列并排序
		int[] tempArray = new int[4];
		tempArray = leastXY;
		ArrayList<int[]> minCol = new ArrayList<>();
		minCol.add(tempArray);
		while(true){		  //向下查找同一列的
			int count = 0, tag = 0;
			for(int i = 0; i< myArray.length; ++i){
				if((Math.abs(tempArray[2]-myArray[i][2])>(aveHeight*minColRate))
						&&(Math.abs(tempArray[2]-myArray[i][2])<(aveHeight*maxColRate))
						&&(Math.abs(tempArray[1]-myArray[i][1])<(aveHeight*colErrorRate))
						&& ((tempArray[2]-myArray[i][2])<0)
						&&((Math.pow((tempArray[1]-myArray[i][1]),2)+(Math.pow((tempArray[2]-myArray[i][2]),2)))>Math.pow(aveHeight/2,2))   //消除重复的框
						&& (tempArray[0] != myArray[i][0])){
					System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
					tempArray = myArray[i];
					minCol.add(tempArray);
					tag = 1;
					break;
				}
			}
			++count;
			if(tag == 0||count > myArray.length)
				break;
		}
		tempArray = leastXY;
		while(true){		  ////向上查找同一列的
			int count = 0, tag = 0;
			for(int i = 0; i< myArray.length; ++i){
				if((Math.abs(tempArray[2]-myArray[i][2])>(aveHeight*minColRate))
						&&(Math.abs(tempArray[2]-myArray[i][2])<(aveHeight*maxColRate))
						&&(Math.abs(tempArray[1]-myArray[i][1])<(aveHeight*colErrorRate))
						&& ((tempArray[2]-myArray[i][2])>0)
						&&((Math.pow((tempArray[1]-myArray[i][1]),2)+(Math.pow((tempArray[2]-myArray[i][2]),2)))>Math.pow(aveHeight/2,2))   //消除重复的框
						&& (tempArray[0] != myArray[i][0])){
					System.out.println("插入bbox: "+i+"   "+myArray[i][0]+"   "+myArray[i][1]+"   "+myArray[i][2]+"   "+myArray[i][3]);
					tempArray = myArray[i];
					minCol.add(tempArray);
					tag = 1;
					break;
				}
			}
			++count;
			if(tag == 0||count > myArray.length)
				break;
		}

		int[][] minXCol = minCol.toArray(new int[minCol.size()][3]);//冒泡排序
		//System.out.println();	//换行
		int[] temp = minXCol[0];

		for(int i = (minXCol.length-1); i>0; i--){
			for(int j = 0; j<i; j++){
				if(minXCol[j][2] > minXCol[j+1][2]){
					temp = minXCol[j+1];
					minXCol[j+1] = minXCol[j];
					minXCol[j] = temp;
				}
			}
		}
		return minXCol;
	}


	public static ArrayList<int[]> findRow(int array[][],int minX[],int aveHeight){ //找到最左面的那个元素对应的一行
		ArrayList<int[]> row = new ArrayList<>();
		int[] tempArray = minX;
		row.add(minX);
		while(true){
			int count = 0, tag = 0;
			for(int i = 0; i< array.length; ++i){
				if((Math.abs(tempArray[1]-array[i][1])>(aveHeight*minRowRate))
						&&(Math.abs(tempArray[1]-array[i][1])<(aveHeight*maxRowRate))
						&& (Math.abs(tempArray[2]-array[i][2])<(aveHeight*rowErrorRate))
						&& ((tempArray[1]-array[i][1])<0)
						&&((Math.pow((tempArray[1]-array[i][1]),2)+(Math.pow((tempArray[2]-array[i][2]),2)))>Math.pow(aveHeight/2,2))    //消除重复的框
						&& (tempArray[0] != array[i][0])){
					tempArray = array[i];
					row.add(tempArray);
					tag = 1;
					break;
				}
			}
			++count;
			if(tag == 0||count > array.length)
				break;
		}
		for(int i = 0; i<(row.size()-1); ++i){
			for(int j = (row.size()-1); j>0; --j){
				int[] a = row.get(j);
				int[] b = row.get(j-1);
				//System.out.println(a[1]);
				if(a[1] < b[1]){
					row.set(j-1,a);
					row.set(j, b);
				}
			}
		}
		return row;
	}

}
