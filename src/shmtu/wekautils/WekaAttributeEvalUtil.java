package shmtu.wekautils;

import java.util.List;

import shmtu.extral.attributeSelection.CHIAttributeEval;
import shmtu.util.CommonUtils;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

/**
 * 特征选择验证
 * 
 * @author HP_xiaochao
 *
 */
public class WekaAttributeEvalUtil {
	/**
	 * 
	 * @param data
	 * @throws Exception
	 */
	public static void igEval(Instances data,int maxNumAttribute) throws Exception {
		String methodAbstract = "attributes_selection_ig_" + maxNumAttribute;
		CommonUtils.print("--------------- " + methodAbstract + " ---------------");
		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelection asFilter = new AttributeSelection();
		asFilter.setEvaluator(igAttributeEval);
		asFilter.setSearch(ranker);
		asFilter.setInputFormat(data);
		Instances afterAsArrff = Filter.useFilter(data, asFilter);
		List<Attribute> afterAsAttributes = WekaUtil.getAttributesByINstances(afterAsArrff);
		WekaUtil.writeAttributeToFile("result/" + methodAbstract + ".txt", afterAsAttributes);
	}
	/**
	 * IG 特征选择并 返回选择的属性
	 * @param data
	 * @param maxNumAttribute
	 * @return
	 * @throws Exception
	 */
	public static List<String> igEvaluation(Instances data,int maxNumAttribute) throws Exception {
		String methodAbstract = "attributes_selection_ig_" + maxNumAttribute;
		CommonUtils.print("--------------- " + methodAbstract + " ---------------");
		CommonUtils.print("class attribute : " + data.attribute(data.classIndex()));
		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelection asFilter = new AttributeSelection();
		asFilter.setEvaluator(igAttributeEval);
		asFilter.setSearch(ranker);
		asFilter.setInputFormat(data);
		Instances afterAsArrff = Filter.useFilter(data, asFilter);
		List<Attribute> afterAsAttributes = WekaUtil.getAttributesByINstances(afterAsArrff);
		WekaUtil.writeAttributeToFile("result/" + methodAbstract + ".txt", afterAsAttributes);
		List<String> attributesName = WekaUtil.attributesToNames(afterAsAttributes);
		return attributesName;
	}
	/**
	 * 对实例IG特征选择 并返回 选择后的实例
	 * @param data
	 * @param maxNumAttribute
	 * @return
	 * @throws Exception
	 */
	public static Instances igEvaluationAndReturn(Instances data,int maxNumAttribute) throws Exception{
		String methodAbstract = "attributes_selection_ig_" + maxNumAttribute;
		CommonUtils.print("--------------- " + methodAbstract + " ---------------");
		CommonUtils.print("class attribute : " + data.attribute(data.classIndex()));
		InfoGainAttributeEval igAttributeEval = new InfoGainAttributeEval();
//		CHIAttributeEval chiAttributeEval = new CHIAttributeEval();
		Ranker ranker = new Ranker();
		ranker.setNumToSelect(maxNumAttribute);
		AttributeSelection asFilter = new AttributeSelection();
		asFilter.setEvaluator(igAttributeEval);
		asFilter.setSearch(ranker);
		asFilter.setInputFormat(data);
		Instances afterAsArrff = Filter.useFilter(data, asFilter);
		return afterAsArrff;
	}

	
}
