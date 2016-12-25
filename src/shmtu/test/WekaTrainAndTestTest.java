package shmtu.test;

import java.util.List;

import shmtu.util.CommonUtils;
import shmtu.wekautils.WekaAttributeEvalUtil;
import shmtu.wekautils.WekaTrainAndTestUtil;
import shmtu.wekautils.WekaUtil;
import shmtu.wekautils.WordRelationUtil;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * 训练集测试集
 * @author HP_xiaochao
 *2016年12月21日
 *
 */
public class WekaTrainAndTestTest {
	
	public static void trainAndTrainStringToVector() throws Exception{
		String trainfile = "wekafiles/texts_pre.arff";
		String testfile = "wekafiles/shortText.arff";
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
		WekaUtil.testsetEvalByIGNaiveBayes(vsmTrainInstances, vsmTestInstances, 1000);
	}
	
	public static void testsetRelationExtral() throws Exception{
		String trainfile = "wekafiles/texts_pre.arff";
		String testfile = "wekafiles/shortText.arff";
		//String trainnewfile = "wekafiles/texts_pre_relation.arff";
		String testnewfile = "wekafiles/shortText_relation.arff";
		//1.先训练 未扩展的分类结果
		WekaTrainAndTestUtil.evalTrainTestByIGNaiveBayes(trainfile, testfile, 1000);
		// 2.计算 把两个词的组合作为一个特征 计算每个属性的相关相关
		List<String> relationList = WordRelationUtil.relationCompute(trainfile, 800);
		//3.扩展测试文件，暂不需要扩展原数据文件
		WekaUtil.exchangeDataInstancesByRelationwordList(testfile, testnewfile, relationList);
		//4.再次训练 并测试 得到扩展的分类结果
		WekaTrainAndTestUtil.evalTrainTestByIGNaiveBayes(trainfile, testnewfile, 1000);
	}
	public static void main(String[]args) throws Exception{
//		trainAndTrainStringToVector();
		testsetRelationExtral();//结论对短文本效果有提高
	}
}
