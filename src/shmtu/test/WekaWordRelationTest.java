package shmtu.test;

import java.util.List;

import shmtu.util.CommonUtils;
import shmtu.wekautils.WekaAttributeEvalUtil;
import shmtu.wekautils.WekaUtil;
import shmtu.wekautils.WordRelationUtil;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToBinary;

/**
 * 词相关 计算
 * 
 * @author HP_xiaochao 2016年12月20日
 *
 */
public class WekaWordRelationTest {
	public static void doSomething() throws Exception {
		//1.加载数据
		String arffFilePath = "wekafiles/texts.arff";
		Instances data = WekaUtil.loadArffByDataSource(arffFilePath);
		data.setClassIndex(data.numAttributes()-1);
		WekaUtil.printHeader(data);
		//2.词向量转换
		Instances dataStringToVector = WekaUtil.stringToVectorWeightBoolean(data);
		dataStringToVector.setClassIndex(0);
		//3.特征选择
		int maxNumAttribute = 100;
		Instances dataAs = WekaAttributeEvalUtil.igEvaluationAndReturn(dataStringToVector, maxNumAttribute);
		//4.计算 把两个词的组合作为一个特征
		List<String> relationList = WordRelationUtil.computeWordCombineListByIG(dataAs);
		//
		//5.重写文件
		WekaUtil.exchangeDataInstancesByRelationwordList("wekafiles/texts.arff", "wekafiles/texts.arff", relationList);
		String content = "";
		for(String str:relationList){
			content+=str+"\n";
			System.out.println(str);
		}
		CommonUtils.writeFile("result/relation.txt", content);
	}
	public static void main(String[]args) throws Exception{
		doSomething();
	}
}
