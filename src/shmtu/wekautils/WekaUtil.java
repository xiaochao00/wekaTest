package shmtu.wekautils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import shmtu.extral.attributeSelection.CHIAttributeEval;
import shmtu.extral.attributeSelection.MIAttributeEval;
import shmtu.util.CommonUtils;
import shmtu.util.ReadWriteFileWithEncode;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * weka 工具类
 * 1.在对过滤器操作之前 设置选线操作setOptions必须在 setInputtformat之前
 * 2.转换词向量操作 需要指定类别 类属性的位置。在所有操作之前 都要确保设置好类属性。
 * 3.把数据集对象instances作为参数的时候要小心 不能来回修改使用
 * 4.libsvm 使用需要设置 -S 参数 =1
 * @author HP_xiaochao
 *
 */
public class WekaUtil {
	public static int maxTextCopyNum = 10;//扩展的时候规定最长的扩展长度
	public static int minAttributeLen = 5;//过滤掉过短文本
	/**
	 * 加载方式 1.
	 * 通过加载器加载arff文件
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static Instances loadArffByLoader(String arffFilePath) throws Exception{
		ArffLoader arff = new ArffLoader();
		arff.setSource(new File(arffFilePath));
		Instances instances = arff.getDataSet();
		System.out.println("加载数据文件成功");
		return instances;
	}
	/**
	 * 加载方式 2.
	 * 通过 Data Source 类加载arff文件
	 * @param arffFilePath
	 * @return
	 * @throws Exception
	 */
	public static Instances loadArffByDataSource(String arffFilePath) throws Exception{
		Instances instances = DataSource.read(arffFilePath);
		System.out.println("加载数据文件成功");
		return instances;
	}
	/**
	 * 保存实例数据 方式1.
	 * 通过 DataSink 保存
	 * @param instances
	 * @param saveArffFilePath
	 * @throws Exception
	 */
	public static void saveArffByInstancesByDataSink(Instances instances,String saveArffFilePath) throws Exception{
		DataSink.write(saveArffFilePath, instances);
		System.out.println("保存实例正确： to " + saveArffFilePath);
	}
	/**
	 * 保存数据 方式2.
	 * 通过转换器
	 * @param instances
	 * @param saveArffFilePath
	 * @throws Exception
	 */
	public static void saveArffBySaver(Instances instances,String saveArffFilePath) throws Exception{
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		saver.setFile(new File(saveArffFilePath));
		saver.writeBatch();
		System.out.println("保存实例数据正确：to " + saveArffFilePath);
	}
	/**
	 * 打印 数据实例头信息
	 * @param instances
	 */
	public static void printHeader(Instances instances){
		if(instances!=null){
			System.out.println(new Instances(instances,0));
		}
	}
	/**
	 * 返回实例数据集 的属性列表列表 没有类属性的
	 * @param instances
	 * @return
	 */
	public static List<Attribute> getAttributesByINstances(Instances instances){
		List<Attribute> attributes = new ArrayList<Attribute>();
		if(instances!=null){
			int attributeNum = instances.numAttributes();
			System.out.println("属性数目:"+attributeNum);
			for(int i=0;i<attributeNum;i++){
				if(i!=instances.classIndex())
					attributes.add(instances.attribute(i));
			}
		}
		return attributes;
	}
	/**
	 * 文档实例 转换成词向量实例 通过 StringToVctor
	 * 返回新数据实例
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public static Instances stringToVectorWeightBoolean(Instances instances) throws Exception{
		String methodType = "文档数据转换词向量 权值采用 布尔值";
		CommonUtils.print("----------- " + methodType + " -------------");
		CommonUtils.print("确保设置好 类属性位置");
		StringToWordVector vector = new StringToWordVector();
		vector.setInputFormat(instances);
		Instances newArff = Filter.useFilter(instances, vector);
		CommonUtils.print("-------------- 文档转变词向量成功 特征数目:" + newArff.numAttributes() + "-------------------");
		CommonUtils.print("第一个属性是:" + newArff.attribute(0));
		CommonUtils.print("最后属性是:" + newArff.attribute(newArff.numAttributes()-1));
		return newArff;
	}
	/**
	 * 文档词向量转换 权重tf值
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public static Instances  stringToVerctorWeightIF(Instances instances) throws Exception{
		String methodType = "文档数据转换词向量 权值采用 tf值";
		CommonUtils.print("----------- " + methodType + " -------------");
		CommonUtils.print("确保设置好 类属性位置");
		StringToWordVector vector = new StringToWordVector();
		vector.setTFTransform(true);
		vector.setOutputWordCounts(true);
		
		vector.setInputFormat(instances);
		Instances newArff = Filter.useFilter(instances, vector);
		CommonUtils.print("-------------- 文档转变词向量成功 特征数目:" + newArff.numAttributes() + "-------------------");
		CommonUtils.print("第一个属性是:" + newArff.attribute(0));
		CommonUtils.print("最后属性是:" + newArff.attribute(newArff.numAttributes()-1));
		return newArff;
	}
	/**
	 * 文档词向量转换 权重ifidf
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	public static Instances  stringToVerctorWeightIFIDF(Instances instances) throws Exception{
		String methodType = "文档数据转换词向量 权值采用 tfidf值";
		CommonUtils.print("----------- " + methodType + " -------------");
		CommonUtils.print("确保设置好 类属性位置." + instances.attribute(instances.numAttributes()-1));
		StringToWordVector vector = new StringToWordVector();
		vector.setTFTransform(true);
		vector.setIDFTransform(true);
		vector.setOutputWordCounts(true);
		
		vector.setInputFormat(instances);
		Instances newArff = Filter.useFilter(instances, vector);
		CommonUtils.print("-------------- 文档转变词向量成功 特征数目:" + newArff.numAttributes() + "-------------------");
		CommonUtils.print("第一个属性是:" + newArff.attribute(0));
		CommonUtils.print("最后属性是:" + newArff.attribute(newArff.numAttributes()-1));
		return newArff;
	}
	/**
	 * IG 特征选择方法 Naivebayes分类方法
	 *  使用weka元分类器  AttributeSelectedClassifier
	 * @param instances
	 * @throws Exception
	 */
	public static void catagoryByIGNaiveBayes(Instances instances,int maxNumAttribute) throws Exception{
		String methodType = "IG_特征选择方法_Naivebayes分类方法";
		CommonUtils.print("--------- " + methodType +  ", maxNumAttribute is " + maxNumAttribute + " ----------");
		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		NaiveBayes nBayes = new NaiveBayes();
		classifier.setClassifier(nBayes);
		classifier.setEvaluator(igAttributeEval);
		classifier.setSearch(ranker);
		Evaluation evaluation = new Evaluation(instances);
	    evaluation.crossValidateModel(classifier, instances, 10, new Random(1));
	    //
	    printEvaluation(evaluation,"result/" + methodType + "_" + maxNumAttribute + ".txt");
//	    String content = "";
//	    content += evaluation.toSummaryString() + "\n";
//	    content += evaluation.toClassDetailsString() + "\n";
//	    content += evaluation.toMatrixString() + "\n";
//	    CommonUtils.print(content);
//	    CommonUtils.writeFile("result/" + methodType + "_" + maxNumAttribute + ".txt", content);
	}
	/**
	 * CHI 特征选择  naivebayes 分类器
	 * @param instances
	 * @param maxNumAttribute
	 * @throws Exception
	 */
	public static void catagoryByCHINaiveBayes(Instances instances,int maxNumAttribute) throws Exception{
		String methodType = "CHI_特征选择方法_Naivebayes分类方法";
		CommonUtils.print("--------- " + methodType +  ", maxNumAttribute is " + maxNumAttribute + " ----------");
		
//		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		NaiveBayes nBayes = new NaiveBayes();
		classifier.setClassifier(nBayes);
		classifier.setEvaluator(chiAttributeEval);
		classifier.setSearch(ranker);
		Evaluation evaluation = new Evaluation(instances);
	    evaluation.crossValidateModel(classifier, instances, 10, new Random(1));
	    //
	    printEvaluation(evaluation,"result/" + methodType + "_" + maxNumAttribute + ".txt");
	    //
//	    String content = "";
//	    content += evaluation.toSummaryString() + "\n";
//	    content += evaluation.toClassDetailsString() + "\n";
//	    content += evaluation.toMatrixString() + "\n";
//	    CommonUtils.print(content);
//	    CommonUtils.writeFile("result/" + methodType + "_" + maxNumAttribute + ".txt", content);System.out.println(evaluation.toSummaryString());
//	    System.out.println(evaluation.toMatrixString());
//	    System.out.println(evaluation.toClassDetailsString());
	}
	/**
	 * CHI 特征选择 libsvm分类器 
	 * 同样采用weka 的元分类器
	 * @param instances
	 * @param maxNumAttribute
	 * @throws Exception
	 */
	public static void catagoryByCHILibSVM(Instances instances,int maxNumAttribute) throws Exception{
		
		String methodType = "CHI_特征选择方法_libsvm分类方法";
		CommonUtils.print("--------- " + methodType +  ", maxNumAttribute is " + maxNumAttribute + " ----------");
		
		//		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		int maxAttributeNum = 900;
		CommonUtils.print("classfier method:libsvm ;attribute selection:CHI;attributeNum:"+maxNumAttribute);
		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		LibSVM libsvm = new LibSVM();
		libsvm.setOptions(new String[]{
				"-S","1"
		});//该参数很重要 
		classifier.setClassifier(libsvm);
		classifier.setEvaluator(chiAttributeEval);
		classifier.setSearch(ranker);
		
		Evaluation evaluation = new Evaluation(instances);
	    evaluation.crossValidateModel(classifier, instances, 10, new Random(1));
	   //
	    printEvaluation(evaluation,"result/" + methodType + "_" + maxNumAttribute + ".txt");
//	    String content = "";
//	    content += evaluation.toSummaryString() + "\n";
//	    content += evaluation.toClassDetailsString() + "\n";
//	    content += evaluation.toMatrixString() + "\n";
//	    CommonUtils.print(content);
//	    CommonUtils.writeFile("result/" + methodType + "_" + maxNumAttribute + ".txt", content);System.out.println(evaluation.toSummaryString());
//	    System.out.println(evaluation.toSummaryString());
//	    System.out.println(evaluation.toMatrixString());
//	    System.out.println(evaluation.toClassDetailsString());
	}
	/**
	 * 结合特征选择IG和LIBSVM分类器
	 * 
	 * @param instances
	 * @throws Exception
	 */
	public static void catagoryByIGLibSVM(Instances instances,int maxNumAttribute) throws Exception{
		
		String methodType = "IG_特征选择方法_libsvm分类方法";
		CommonUtils.print("--------- " + methodType +  ", maxNumAttribute is " + maxNumAttribute + " ----------");
		
		//
//		int maxAttributeNum = 1000;
		CommonUtils.print("classfier method:libsvm ;attribute selection:IG;attributeNum:"+maxNumAttribute);
		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelection asFilter = new AttributeSelection();
		asFilter.setEvaluator(igAttributeEval);
		asFilter.setSearch(ranker);
		asFilter.setInputFormat(instances);
		Instances afterAsArrff = Filter.useFilter(instances, asFilter);
		saveArffBySaver(afterAsArrff,"result/texst_ig_1000.arff");
		//分类 并评估
		afterAsArrff.setClassIndex(afterAsArrff.numAttributes()-1);
		Evaluation evaluation = new Evaluation(instances);
		LibSVM libsvm = new LibSVM();
		libsvm.setOptions(new String[]{
				"-S","1"
		});
		
		evaluation.crossValidateModel(libsvm, afterAsArrff, 10, new Random(1));
		//
////		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
//		
//		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
//		
//		for(String o:libsvm.getOptions()){
//			System.out.println(o);
//		}
////		LibSVM.SVMTYPE_ONE_CLASS_SVM
//		classifier.setClassifier(libsvm);
//		classifier.setEvaluator(igAttributeEval);
//		classifier.setSearch(ranker);
		
//	    evaluation.crossValidateModel(classifier, instances, 10, new Random(1));
//	    System.out.println(evaluation.toSummaryString());
//	    System.out.println(evaluation.toMatrixString());
//	    System.out.println(evaluation.toClassDetailsString());
		
		printEvaluation(evaluation,"result/" + methodType + "_" + maxNumAttribute + ".txt");
	}
	/**
	 * 
	 * @param instances
	 * @throws Exception
	 */
	public static void catagoryByMINavidateBayes(Instances instances,int maxNumAttribute) throws Exception{
//		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		int maxAttributeNum = 5000;
		String methodType = "MI_特征选择方法_naivebayes分类方法";
		CommonUtils.print("--------- " + methodType +  ", maxNumAttribute is " + maxNumAttribute + " ----------");
		
		System.out.println("classfier method:libsvm ;attribute selection:MI;attributeNum:"+maxNumAttribute);

		MIAttributeEval miAttributeEval = new MIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		NaiveBayes nBayes = new NaiveBayes();
		classifier.setClassifier(nBayes);
		classifier.setEvaluator(miAttributeEval);
		classifier.setSearch(ranker);
		Evaluation evaluation = new Evaluation(instances);
	    evaluation.crossValidateModel(classifier, instances, 10, new Random(1));
//	    System.out.println(evaluation.toSummaryString());
//	    System.out.println(evaluation.toMatrixString());
//	    System.out.println(evaluation.toClassDetailsString());
	    //
		printEvaluation(evaluation,"result/" + methodType + "_" + maxNumAttribute + ".txt");
	}
	/**
	 * 验证测试集
	 * @param instances
	 * @param testInstances
	 * @param maxNumAttribute
	 * @throws Exception
	 */
	public static void testsetEvalByIGNaiveBayes(Instances instances,Instances testInstances,int maxNumAttribute) throws Exception{
//		int maxAttributeNum = 5000;
		String methodType = "IG_特征选择方法_naivebayes分类方法_批量过滤处理";
		CommonUtils.print("--------- " + methodType +  ", maxNumAttribute is " + maxNumAttribute + " ----------");
		System.out.println("classfier method:naivebayes ;attribute selection:IG;attributeNum:"+maxNumAttribute);
		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		MIAttributeEval miAttributeEval = new MIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		//
		AttributeSelection asFilter = new AttributeSelection();
		asFilter.setEvaluator(igAttributeEval);
		asFilter.setSearch(ranker);
		asFilter.setInputFormat(instances);
		Instances afterTrainAsArrff = Filter.useFilter(instances, asFilter);
		Instances afterTestAsArff = Filter.useFilter(testInstances, asFilter);
		//
		NaiveBayes nBayes = new NaiveBayes();
		nBayes.buildClassifier(afterTrainAsArrff);//
		Evaluation evaluation = new Evaluation(afterTrainAsArrff);
	    evaluation.evaluateModel(nBayes, afterTestAsArff);
//	    System.out.println(evaluation.toSummaryString());
//	    System.out.println(evaluation.toMatrixString());
//	    System.out.println(evaluation.toClassDetailsString());
	    //
		printEvaluation(evaluation,"result/" + methodType + "_" + maxNumAttribute + ".txt");
	}

	/**
	 * 打印出相关 过滤器的参数列表
	 * @param filter
	 */
	public static void pintFilterOption(StringToWordVector filter){
		String options[] = filter.getOptions();
		String filterStr = filter.toString();
		String printStr = filterStr+"\n";
		for(String o:options){
			printStr += o + " ";
		}
		System.out.println(printStr);
	}
	/**
	 * 把属性列表写入文件
	 * @param fileName
	 * @param attributes
	 * @throws IOException
	 */
	public static void writeAttributeToFile(String fileName,List<Attribute> attributes) throws IOException{
		CommonUtils.print("------- write attributes list to files:" + fileName + "  ---------------");
		if(attributes!=null&&attributes.size()>0){
			String content = "";
			for(Attribute a:attributes){
				content += a.name()+"\n";
			}
			ReadWriteFileWithEncode.writeByEncodie(fileName, content, ReadWriteFileWithEncode.DEFAULT_ENCODE);
		}
	}
	/**
	 * 打印评估器 函数
	 * @param evaluation
	 * @param fileName
	 * @throws Exception
	 */
	public static void printEvaluation(Evaluation evaluation,String fileName) throws Exception{
		 //
	    String content = "";
	    content += evaluation.toSummaryString() + "\n";
	    content += evaluation.toClassDetailsString() + "\n";
	    content += evaluation.toMatrixString() + "\n";
	    CommonUtils.print(content);
	    if(fileName!=null)
	    	CommonUtils.writeFile(fileName, content);System.out.println(evaluation.toSummaryString());

	}
	
	public static List<String> attributesToNames(List<Attribute> attributes){
		List<String> names = new ArrayList<String>();
		if(!CommonUtils.listIsEmpty(attributes)){
			for(Attribute a:attributes){
				names.add(a.name());
			}
		}
		return names;
	}
	public static void exchangeDataInstancesByRelationwordList(String oldfilename,String newfilename,List<String> relationlists){
		CommonUtils.print("根据相关列表  修改源文件");
		Map<String,String> relationlistMap = new HashMap<String,String>();
		List<String> atributenameList = new ArrayList<String>();
		if(CommonUtils.listIsEmpty(relationlists))
			return ;
		for(String a:relationlists){
			String []arr = a.split(" ");
			String key = arr[0];
			String relations = a.substring(a.indexOf(key)+key.length()+1,a.length());
			//
			atributenameList.add(key);
			relationlistMap.put(key, relations);
		}
		//开始扩展
		List<String> lines = CommonUtils.readFileLines(oldfilename);
		List<String> newlines = new ArrayList<String>();
		if(!CommonUtils.listIsEmpty(lines)){
			for(String line:lines){
				if(line.startsWith("'")){
					String text = line.split(",")[0];
					String preText = text;
					String catagory = line.split(",")[1];
					for(String key:atributenameList){
						String relationArr[] = relationlistMap.get(key).split(" ");
						for(String str:relationArr){
							if(!"".equals(str)&&text.split(" ").length<maxTextCopyNum){
								if(WekaUtil.checkTextContainStr(text, key)){
//									System.out.println(text);
									text = WekaUtil.copySrtCombinToText(text, key, str);
//									System.out.println(text);
								}
							}
						}
					}
					if(!preText.equals(text)){
						System.out.println(preText);
						System.out.println(text);
					}
					newlines.add(text + "," + catagory);
				}else{
					newlines.add(line);
				}
			}
		}
		CommonUtils.writeFileByList(newfilename,newlines);
	}
	public static void exchangeDataFileBySimilaritylists(String oldfilename,String newfilename,List<String> similaritylists){
		System.out.println("根据相似度列表 修改源文件");
		Map<String,String> similaritylistMap = new HashMap<String,String>();
		List<String> attribueList = new ArrayList<String>();
		if(CommonUtils.listIsEmpty(similaritylists))
			return ;
		for(String a:similaritylists){
			String[]arr = a.split(" ");
			String key = arr[0];
			String similaritys = a.substring(a.indexOf(key)+key.length()+1,a.length()); 
			//
			attribueList.add(key);
			similaritylistMap.put(key, similaritys);
		}
		//开始替换
		List<String> lines = CommonUtils.readFileLines(oldfilename);
		List<String> newlines = new ArrayList<String>();
		if(!CommonUtils.listIsEmpty(lines)){
			for(String line:lines){
//				String text = line;
				if(line.startsWith("'")){
					String text = line.split(",")[0];
					String catagory = line.split(",")[1];
					for(String key:attribueList){
						String similarityArr[] = similaritylistMap.get(key).split(" ");
						for(String str:similarityArr){
							if(!"".equals(str))
								text = text.replace(str, key);
						}
					}
					newlines.add(text + "," + catagory);
				}else{
					newlines.add(line);
				}
			}
		}
		//
//		StringBuffer content = new StringBuffer();
//		for(String l:newlines){
//			content.append(l+"\r\n");
//		}
//		CommonUtils.writeFile(newfilename, content.toString());
		CommonUtils.writeFileByList(newfilename,newlines);
//		Common
	}
	public static boolean checkTextContainStr(String text,String str){
		String arr[] = text.split(" ");
		for(int i=0;i<arr.length;i++){
			if(arr[i].equals(str))
				return true;
		}
		return false;
	}
	public static String copySrtCombinToText(String text,String str,String addStr){
		//如果长度太长不扩展
		String newText = "";
		String arr[] = text.split(" ");
		int count = 0;
		for(int i=0;i<arr.length;i++){
			if(arr[i].equals(str)&&count==0){
				arr[i] = str + " " + addStr;
				count = 1;
			}
			newText+=arr[i]+" ";
		}
		return newText;
	}
	/**
	 * 过滤掉arff文件过短的记录
	 * @param oldfilename
	 * @param newfilename
	 */
	public static void preProcessARFF(String oldfilename,String newfilename){
		List<String> lines = CommonUtils.readFileLines(oldfilename);
		List<String> newlines = new ArrayList<String>();
		List<String> shortlines = new ArrayList<String>();//把短文本收集起来测试用
		if(!CommonUtils.listIsEmpty(lines)){
			for(String line:lines){
				if(line.startsWith("'")){
					String text = line.split(",")[0];
					if(text.split(" ").length<minAttributeLen){
						shortlines.add(line);
						continue;
					}
				}else{
					shortlines.add(line);
				}
				newlines.add(line);
			}
		}
		
		CommonUtils.writeFileByList(newfilename, newlines);
		CommonUtils.writeFileByList("wekafiles/shortText.arff",shortlines );
		CommonUtils.print("-------- 筛选掉："+(lines.size()-newlines.size())+"条记录 ----------");
	}
}
