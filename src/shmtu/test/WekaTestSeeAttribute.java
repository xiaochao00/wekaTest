package shmtu.test;

import java.util.List;

import shmtu.wekautils.WekaAttributeEvalUtil;
import shmtu.wekautils.WekaUtil;
import shmtu.wekautils.WordRelationUtil;
import shmtu.wordsimilarity.WordSimilarityUtil;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * weka试验读 属性
 * @author HP_xiaochao
 *
 */
public class WekaTestSeeAttribute {
	public static void doMain() throws Exception{
		//首先通过 wekaCLI 将数据文件生成词向量 arff 文件
		//两种加载 arff文件的方法
		//1,加载text文档
		String arffFilePath = "wekafiles/relationText.arff";
//		String arffFilePath = "wekafiles/texts.arff";
		Instances instances = WekaUtil.loadArffByDataSource(arffFilePath);
		instances.setClassIndex(instances.numAttributes()-1);
		WekaUtil.printHeader(instances);
		// 词向量
		//2.生成词向量 示例文件
		StringToWordVector vector = new StringToWordVector();
		
		String []options = Utils.splitOptions("-R first-last -W 1000 -prune-rate -1.0 -C -T -I -N 0 ");
		vector.setOptions(options);
		vector.setInputFormat(instances);
		Instances newArff = Filter.useFilter(instances, vector);
//		WekaUtil.printHeader(newArff);
		//
		WekaUtil.pintFilterOption(vector);
		//
//		List<Attribute> attributes = WekaUtil.getAttributesByINstances(newArff);
//		WekaUtil.writeAttributeToFile("attributes.txt", attributes);
//		System.out.println(attributes.get(0).name());
		//3.特征选择
		newArff.setClassIndex(0);//设置类别索引
		//特征选择 IG
//		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
////		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
//		Ranker ranker = new Ranker();
//		ranker.setNumToSelect(5000);
//		AttributeSelection asFilter = new AttributeSelection();
//		asFilter.setEvaluator(igAttributeEval);
//		asFilter.setSearch(ranker);
//		asFilter.setInputFormat(newArff);
//		Instances afterAsArrff = Filter.useFilter(newArff, asFilter);
//		List<Attribute> afterAsAttributes = WekaUtil.getAttributesByINstances(afterAsArrff);
//		for(Attribute at:afterAsAttributes){
//			System.out.println(at);
//		}
		//验证特征选择好坏
		
		WekaUtil.catagoryByCHILibSVM(newArff,1000);
//		List<String> attributenames = WekaAttributeEvalUtil.igEvaluation(newArff,1000);
//		WekaUtil.catagoryByIGLibSVM(newArff.c,);
//		List<String> attribtuteAfSimilarity = WordSimilarityUtil.computeSimilarityWordList(attributenames);
		
	}
	public static void doSimilarit() throws Exception{
		String oldfilename = "wekafiles/texts.arff";
		//1.评估源数据集分类效果
		loadAndEval(oldfilename,1000);
		//2.相似度替换
		String newFilename = "wekafiles/similarityText.arff";
		similarityInstance(oldfilename,newFilename);
		//3.评估 替换后分类效果
		loadAndEval(newFilename,900);
	}
	public static void doRelation() throws Exception{
		String oldfilename = "wekafiles/texts_pre.arff";
		//1.评估源数据集分类效果
		loadAndEval(oldfilename,1500);
		//2.相似度替换
		String newFilename = "wekafiles/relationText.arff";
		relationInstance(oldfilename,newFilename);
		//3.评估 替换后分类效果
		loadAndEval(newFilename,1500);
	}
	public static void relationInstance(String oldFilename,String newFilename)throws Exception{
		// 1.加载数据
//		String arffFilePath = "wekafiles/texts.arff";
		Instances data = WekaUtil.loadArffByDataSource(oldFilename);
		data.setClassIndex(data.numAttributes() - 1);
		WekaUtil.printHeader(data);
		// 2.词向量转换
		Instances dataStringToVector = WekaUtil.stringToVectorWeightBoolean(data);
		dataStringToVector.setClassIndex(0);
		// 3.特征选择
		int maxNumAttribute = 800;
		Instances dataAs = WekaAttributeEvalUtil.igEvaluationAndReturn(dataStringToVector, maxNumAttribute);
		// 4.计算 把两个词的组合作为一个特征
		List<String> relationList = WordRelationUtil.computeWordCombineListByIG(dataAs);
		//
		// 5.重写文件
		WekaUtil.exchangeDataInstancesByRelationwordList(oldFilename,newFilename,relationList);
//				"wekafiles/texts.arff", "wekafiles/texts.arff", relationList);
	}
	public static void similarityInstance(String oldFilename,String newFilename) throws Exception {
		// 1.加载text文档
		Instances instances = WekaUtil.loadArffByDataSource(oldFilename);
		WekaUtil.printHeader(instances);
		instances.setClassIndex(instances.numAttributes() - 1);
		// 2.词向量
		Instances weightDate = WekaUtil.stringToVerctorWeightIFIDF(instances);
		weightDate.setClassIndex(0);// 设置类别索引
		//3.特征选择
		List<String> attributenames = WekaAttributeEvalUtil.igEvaluation(weightDate,1000);
//		WekaUtil.catagoryByIGLibSVM(newArff.c,);
		List<String> attribtuteAfSimilarity = WordSimilarityUtil.computeSimilarityWordList(attributenames);
		//4.重写文件
		WekaUtil.exchangeDataFileBySimilaritylists(oldFilename,newFilename,attribtuteAfSimilarity);
		//
//		Instances newData = WekaUtil.loadArffByDataSource(newFilename)
	}
	public static void loadAndEval(String filename,int maxnum) throws Exception {
		// 1.加载text文档
		Instances instances = WekaUtil.loadArffByDataSource(filename);
		WekaUtil.printHeader(instances);
		instances.setClassIndex(instances.numAttributes()-1);
		//2.词向量
		Instances weightDate = WekaUtil.stringToVerctorWeightIFIDF(instances);
		weightDate.setClassIndex(0);//设置类别索引
		//3.分类评估
		WekaUtil.catagoryByIGLibSVM(weightDate, maxnum);
	}
	public static void main(String[]args) throws Exception{
//		doMain();
		doSimilarit();
//		loadAndEval("wekafiles/texts.arff",900);
//		doRelation();
//		WekaUtil.preProcessARFF("wekafiles/texts.arff", "wekafiles/texts_pre.arff");
	}
}
