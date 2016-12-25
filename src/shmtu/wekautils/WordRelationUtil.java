package shmtu.wekautils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shmtu.bean.CompareBeanCompare;
import shmtu.bean.UnionEntropyBean;
import shmtu.util.CommonUtils;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 词相关计算工具类
 * 
 * @author HP_xiaochao 2016年12月20日
 *
 */
public class WordRelationUtil {
	private static final int maxNumRelation = 2;//扩展策词数目
	/**
	 * 关联计算
	 * @param datafile
	 * @param maxAttributeNum
	 * @return
	 * @throws Exception
	 */
	public static List<String> relationCompute(String datafile, int maxNumAttribute) throws Exception {
		Instances data = WekaUtil.loadArffByDataSource(datafile);
		data.setClassIndex(data.numAttributes() - 1);
		WekaUtil.printHeader(data);
		// 2.词向量转换
		Instances dataStringToVector = WekaUtil.stringToVectorWeightBoolean(data);
		dataStringToVector.setClassIndex(0);
		// 3.特征选择
//		int maxNumAttribute = 800;
		Instances dataAs = WekaAttributeEvalUtil.igEvaluationAndReturn(dataStringToVector, maxNumAttribute);
		// 4.计算 把两个词的组合作为一个特征
		List<String> relationList = WordRelationUtil.computeWordCombineListByIG(dataAs);
		return relationList;
	}

	/**
	 * 根据 把两个特征当成一个特征计算 信息增益，返回每个单词特征对应得 列表
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static List<String> computeWordCombineListByIG(Instances data) throws Exception {
		double[][] relationEntropyMatrix = computeCombineIG(data);
		List<Attribute> attributes = WekaUtil.getAttributesByINstances(data);
		List<String> relationList = new ArrayList<String>();
		//
		for (int i = 0; i < attributes.size(); i++) {
			List<UnionEntropyBean> unionEntropyList = new ArrayList<UnionEntropyBean>();
			//
			for (int j = 0; j < attributes.size(); j++) {
				if (i < j) {
					UnionEntropyBean unionEntropy = new UnionEntropyBean();
					unionEntropy.setIndex(j);
					unionEntropy.setValue(relationEntropyMatrix[i][j]);
					unionEntropyList.add(unionEntropy);
				} else if (j < i) {
					UnionEntropyBean unionEntropy = new UnionEntropyBean();
					unionEntropy.setIndex(j);
					unionEntropy.setValue(relationEntropyMatrix[j][i]);
					unionEntropyList.add(unionEntropy);
				}
			}
			UnionEntropyBean unionEntropyArr[] = new UnionEntropyBean[unionEntropyList.size()];
			for (int k = 0; k < unionEntropyList.size(); k++) {
				unionEntropyArr[k] = unionEntropyList.get(k);
			}
			// 排序
			Arrays.sort(unionEntropyArr, new CompareBeanCompare());
			//
			String relation = attributes.get(i).name() + " ";
			int num = 0;
			for (int k = unionEntropyArr.length - 1; k >= 0; k--) {
				// CommonUtils.print("--------记录前五个最关联的词------");
				num++;
				if (num <= maxNumRelation) {
					int aIndex = unionEntropyArr[k].getIndex();
					double aEntropy = unionEntropyArr[k].getValue();
					// relation +=
					// attributes.get(aIndex).name()+":"+aEntropy+";";
					relation += attributes.get(aIndex).name() + " ";
				} else {
					break;
				}
			}
			relationList.add(relation);
		}
		//
		return relationList;
	}

	public static double[][] computeCombineIG(Instances data) throws Exception {
		int classIndex = data.classIndex();
		int numInstances = data.numInstances();
		// //离散化
		// Discretize disTransform = new Discretize();
		// disTransform.setUseBetterEncoding(true);
		// disTransform.setInputFormat(data);
		// data = Filter.useFilter(data, disTransform);
		// //
		int numClasses = data.attribute(classIndex).numValues();
		int attributesNum = data.numAttributes() - 1;// 除去类属性
		// 初始化
		// 每个词的类分布数组，数据的类分布数组，词与词共现的类分布数组 的稀疏存储
		// 类分布多一个 是存放丢失的类属性的实例数目
		// 实例中存在这个属性 那么它的numValues 保存所有属性的个数
		// 包括类属性，但是存在丢失的类属性，index(i)获取实例中第i个属性在全局属性的位置信息
		// i，j
		int[][] termClass = new int[data.numAttributes() - 1][numClasses + 1];
		int[] classSum = new int[numClasses + 1];
		int[][] termAndTermClass = new int[(attributesNum) * (attributesNum - 1) / 2][numClasses + 1];
		//
		for (int k = 0; k < numInstances; k++) {
			Instance inst = data.instance(k);
			int attributeClassValue = 0;// 类属性的值 一定要区分 属性的索引和类属性的值
			// 总的类分布
			if (inst.classIsMissing()) {
				attributeClassValue = numClasses;
			} else {
				attributeClassValue = (int) inst.classValue();
			}
			classSum[attributeClassValue] += 1;
			//

			int numValues = inst.numValues();
			for (int i = 0; i < numValues; i++) {
				int attributeIndex = inst.index(i);
				if (attributeIndex != classIndex) {
					numValues -= 1;
					break;
				}
			}
			int[] attributeIndexArr = new int[numValues];// 该实例的属性数组
			// if(inst.classIsMissing())
			// attributeIndexArr = new int[numValues];
			// 构造该实例的所有属性 索引数组
			for (int i = 0; i < numValues; i++) {
				int attributeIndex = inst.index(i);
				if (attributeIndex != classIndex) {
					attributeIndexArr[i] = attributeIndex;
				}
			}
			//
			attributeIndexArr = CommonUtils.sortIntArr(attributeIndexArr);
			// System.out.println(attributeIndexArr);
			// 遍历开始
			// if(attributeIndexArr.length==1){
			// termClass[attributeIndexArr[0]][attributeClassValue] += 1;
			// }
			//
			for (int i = 0; i < attributeIndexArr.length; i++) {
				int indexI = attributeIndexArr[i];
				termClass[indexI][attributeClassValue] += 1;
				for (int j = i + 1; j < attributeIndexArr.length; j++) {
					int indexJ = attributeIndexArr[j];
					int sparseIndex = CommonUtils.computeSparesIndex(indexI, indexJ, attributesNum);
					// attributeClassValue
					termAndTermClass[sparseIndex][attributeClassValue] += 1;
				}
			}
			// termClass[attributeIndexArr.length-1][attributeClassValue]+=1;
			//
		}
		// relation(x,y) = 2*(H(x)-H(x|y))/(H(x)+H(y))的绝对值 ==>
		// H(x)-H(x|y)=H(x)+H(y)-H(x,y)
		// 该矩阵仅仅是上三角存储得有值
		double[][] relationEntropyMatrix = new double[attributesNum][attributesNum];
		double entropyC = WekaComputeUtil.computeEntropy(classSum, classSum);//
		for (int i = 0; i < attributesNum; i++) {
			// double entropyI = WekaComputeUtil.computeEntropy(termClass[i],
			// classSum);
			for (int j = i + 1; j < attributesNum; j++) {
				// double entropyJ =
				// WekaComputeUtil.computeEntropy(termClass[j],classSum);
				int sparseIndex = CommonUtils.computeSparesIndex(i, j, attributesNum);
				int[] N11 = termAndTermClass[sparseIndex];// 两个词都出现
				// int[]N01 = WekaComputeUtil.substractIntArr(termClass[j],
				// N11);//第一个不出现
				// int[]N10 = WekaComputeUtil.substractIntArr(termClass[i],
				// N11);//第二个不出现
				// int[]N00 =
				// WekaComputeUtil.substractIntArr(classSum,termClass[i]);
				// N00 = WekaComputeUtil.substractIntArr(N00,termClass[j]);
				// N00 = WekaComputeUtil.plusIntArr(N00,
				// N11);//classSum-N1-N2+N11
				double unionEntropy = WekaComputeUtil.computeEntropy(N11, classSum);
				// relationEntropyMatrix[i][j] =
				// 2*(entropyI+entropyJ-unionEntropy)/(entropyI+entropyJ);
				relationEntropyMatrix[i][j] = entropyC - unionEntropy;
			}
		}
		//
		return relationEntropyMatrix;
	}
}
