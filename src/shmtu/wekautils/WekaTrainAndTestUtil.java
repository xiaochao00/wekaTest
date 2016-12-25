package shmtu.wekautils;

import shmtu.util.CommonUtils;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class WekaTrainAndTestUtil {
	public static void evalTrainTestByIGNaiveBayes(String trainfile,String testfile,int maxAttributeNum) throws Exception{
		String methodType = "Ig_naiveBayes_train_test";
		CommonUtils.print("------------ "+methodType+" ---------------");
		Instances srcTrainInstances = WekaUtil.loadArffByDataSource(trainfile);
		Instances srcTestInstances = WekaUtil.loadArffByDataSource(testfile);
		srcTrainInstances.setClassIndex(srcTrainInstances.numAttributes()-1);
		srcTestInstances.setClassIndex(srcTestInstances.numAttributes()-1);
		//
		StringToWordVector vector = new StringToWordVector();
		vector.setTFTransform(true);
		vector.setIDFTransform(true);
		vector.setOutputWordCounts(true);
		vector.setInputFormat(srcTrainInstances);
		Instances vsmTrainInstances = Filter.useFilter(srcTrainInstances, vector);
		Instances vsmTestInstances = Filter.useFilter(srcTestInstances, vector);
		//
		vsmTrainInstances.setClassIndex(0);
		vsmTestInstances.setClassIndex(0);
		CommonUtils.print("训练集 属性数目："+vsmTrainInstances.numAttributes());
		CommonUtils.print("测试集 属性数目："+vsmTestInstances.numAttributes());
		//
		WekaUtil.testsetEvalByIGNaiveBayes(vsmTrainInstances, vsmTestInstances, maxAttributeNum);
	}
}
