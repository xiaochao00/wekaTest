package shmtu.wekautils;
/**
 * 计算 工具类
 * @author HP_xiaochao
 *
 */
public class WekaComputeUtil {
	/**
	 * 根据属性的 每类分布和各类总分布 计算卡方值
	 * 属性对于整个集合的卡方值是对于所有类的卡方值的最大值
	 * @param rowArr
	 * @param columnSum
	 * @return
	 */
	public static double computeMaxCHI(double[]rowArr,double[]columnSum){
		double maxChi = 0.0;
		double rowSum = 0.0;
		double N = 0.0;
		
		for(int i=0;i<rowArr.length;i++){
			rowSum+=rowArr[i];
			N+=columnSum[i];
		}
		//N(AD-BC)/(A+C)(A+B)(B+D)(D+C)
		for(int i=0;i<rowArr.length;i++){
			double A = rowArr[i];
			double B = rowSum - A;
			double C = columnSum[i] - A;
			double D = N-A-B-C;
			double chi = N*(A*D-B*C)*(A*D-B*C)/((A+C)*(A+B)*(B+D)*(C+D));
			if(chi>maxChi)
				maxChi = chi;
		}
		return maxChi;
	}
	public static double computeUnionEntropy(int[]N11,int[]N01,int[]N10,int[]N00,int []classSum){
		int N = sumIntArr(classSum);
		int n11 = sumIntArr(N11);
		int n01 = sumIntArr(N01);
		int n10 = sumIntArr(N10);
		int n00 = sumIntArr(N00);
		double unionEntropy = 0.0;
		unionEntropy = (n11*entropy(N11)+n01*entropy(N01)+n10*entropy(N10)+n00*entropy(N00))/N;
		return unionEntropy;
	}
	public static double computeUnionEntropy2(int[]N11,int []classSum){
		int N = sumIntArr(classSum);
		int[]N00 = substractIntArr(classSum,N11);
		int n11 = sumIntArr(N11);
		int n00 = sumIntArr(N00);
		double unionEntropy = 0.0;
		unionEntropy = (n11*entropy(N11)+n00*entropy(N00))/N;
		return unionEntropy;
	}
	/*
	 * 根据属性的类分布 和每类的分布和 计算 互信息
	 * 返回最大的
	 */
	public static double computeMaxMI(double[]rowArr,double[]columnSum){
		double maxMI = 0.0;
		double rowSum = 0.0;
		double N = 0.0;
		
		for(int i=0;i<rowArr.length;i++){
			rowSum+=rowArr[i];
			N+=columnSum[i];
		}
		//N(AD-BC)/(A+C)(A+B)(B+D)(D+C)
		for(int i=0;i<rowArr.length;i++){
			double A = rowArr[i];
			double B = rowSum - A;
			double C = columnSum[i] - A;
//			double D = N-A-B-C;
			double mi = Math.log(A*N/(A+C)*(A+B));
			if(mi>maxMI)
				maxMI = mi;
		}
		return maxMI;
	}
	public static int sumIntArr(int[]intArr){
		if(intArr==null)
			return 0;
		int sum = 0;
		for(int i:intArr){
			sum+=i;
		}
		return sum;
	}
	public static double computeEntropy(int[]termClass,int []classSum){
		double entropy = 0.0;
		int[]noTermClass = new int[termClass.length];
		for(int i=0;i<termClass.length;i++){
			noTermClass[i] = classSum[i] - termClass[i];
		}
		int sumHasTerm = sumIntArr(termClass);
		int sumNoTerm = sumIntArr(noTermClass);
		int N = sumIntArr(classSum);
		if(sumHasTerm+sumNoTerm!=N){
			System.out.println("熵计算出错 请检查");
			System.exit(0);
		}
		double entropyHasTerm = entropy(termClass);
		double entropyNoTerm = entropy(noTermClass);
		entropy = sumHasTerm*1.0*entropyHasTerm/N+sumNoTerm*entropyNoTerm/N;
		return entropy;
	}
	public static double entropy(int[]arr){
		int sum = sumIntArr(arr);
		if(sum==0)
			return 0;
		double entropy = 0.0;
		if(arr==null)
			return 0.0;
		for(int i:arr){
			double p = i*1.0/sum;
			if(p!=0)
				entropy+=p*Math.log(p);
		}
		return -entropy;
	}
	public static double computeEntropyN(int n11,int n01,int n10,int n00,int N){
		int sum = n11+n01+n10+n00;
		double entropyN11 = entropyByN(n11,N);
		double entropyN01 = entropyByN(n01,N);
		double entropyN10 = entropyByN(n10,N);
		double entropyN00 = entropyByN(n00,N);
		double entropy = n11*1.0/N*entropyN11+n10*1.0/N*entropyN10+n01*1.0/N*entropyN01+n00*1.0/N*entropyN00;
		return entropy;
	}
	public static double entropyByN(int ashNum,int N){
		double p1 = ashNum*1.0/N;
		double p2 =	(N-ashNum)*1.0/N;
		double entropy = (p1==0?0:p1*Math.log(p1))+(p2==0?0:p2*Math.log(p2));
		return -entropy;
	}
	public static int[] substractIntArr(int[]a1,int[]a2){
		int[]a3 = new int[a1.length];
		for(int i=0;i<a1.length;i++){
			a3[i] = a1[i] - a2[i];
		}
		return a3;
	}
	public static int[]plusIntArr(int[]a1,int[]a2){
		int[]a3 = new int[a1.length];
		for(int i=0;i<a1.length;i++){
			a3[i] = a1[i] + a2[i];
		}
		return a3;
	}
}
