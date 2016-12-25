package shmtu.extral.attributeSelection;

import java.util.Enumeration;

import shmtu.wekautils.WekaComputeUtil;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeEvaluator;
import weka.core.Capabilities;
import weka.core.ContingencyTables;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.NumericToBinary;

/**
 * 卡方检验 特征评估
 * @author HP_xiaochao
 *
 */
public class CHIAttributeEval extends ASEvaluation implements AttributeEvaluator, OptionHandler {
	private double[] m_CHIs;
	/** Treat missing values as a seperate value */
	private boolean m_missing_merge;

	public CHIAttributeEval() {
		resetOptions();
	}

	/** Just binarize numeric attributes */
	private boolean m_Binarize;

	@Override
	public Enumeration<Option> listOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);
		result.enable(Capability.NUMERIC_ATTRIBUTES);
		result.enable(Capability.DATE_ATTRIBUTES);
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		result.enable(Capability.MISSING_CLASS_VALUES);

		return result;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * get whether missing values are being distributed or not
	 * 
	 * @return true if missing values are being distributed.
	 */
	public boolean getMissingMerge() {
		return m_missing_merge;
	}

	/**
	 * get whether numeric attributes are just being binarized.
	 * 
	 * @return true if missing values are being distributed.
	 */
	public boolean getBinarizeNumericAttributes() {
		return m_Binarize;
	}

	@Override
	public String[] getOptions() {
		// TODO Auto-generated method stub
		// return null;
		String[] options = new String[2];
		int current = 0;

		if (!getMissingMerge()) {
			options[current++] = "-M";
		}
		if (getBinarizeNumericAttributes()) {
			options[current++] = "-B";
		}

		while (current < options.length) {
			options[current++] = "";
		}

		return options;

	}

	/**
	 * Reset options to their default values
	 */
	protected void resetOptions() {
		m_CHIs = null;
		m_missing_merge = true;
		m_Binarize = false;
	}

	@Override
	public double evaluateAttribute(int attribute) throws Exception {
		// TODO Auto-generated method stub
		return m_CHIs[attribute];
	}

	@Override
	public void buildEvaluator(Instances data) throws Exception {
		// TODO Auto-generated method stub
		// can evaluator handle data?
		getCapabilities().testWithFail(data);

		int classIndex = data.classIndex();
		int numInstances = data.numInstances();
		int numClasses = data.attribute(classIndex).numValues();
		//
		if (!m_Binarize) {
			Discretize disTransform = new Discretize();
			disTransform.setUseBetterEncoding(true);
			disTransform.setInputFormat(data);
			data = Filter.useFilter(data, disTransform);
		} else {
			NumericToBinary binTransform = new NumericToBinary();
			binTransform.setInputFormat(data);
			data = Filter.useFilter(data, binTransform);
		}
		// Reserve space and initialize counters
		double[][] counts = new double[data.numAttributes()][];
		for (int k = 0; k < data.numAttributes(); k++) {
			if (k != classIndex) {
				// int numValues = data.attribute(k).numValues();
				counts[k] = new double[numClasses];
			}
		}
		// compute counts
		int classN = data.classAttribute().numValues();
		for (int k = 0; k < numInstances; k++) {
			Instance inst = data.instance(k);
			double classValue = inst.classValue();
			int class_index = (int) classValue;
			// int class_index2 = inst.index(0);
			for (int i = 0; i < inst.numValues(); i++) {
				int wordIndex = inst.index(i);
				if (wordIndex != classIndex) {
					counts[wordIndex][class_index] += 1;
				}
			}
		}
		// compute class total num
		double[] classCounts = new double[numClasses];
		for (int j = 0; j < numClasses; j++) {
			double sumColumn = 0.0;
			for (int i = 0; i < counts.length; i++) {
				if (i == classIndex)
					continue;
				sumColumn += counts[i][j];
			}
			classCounts[j] = sumColumn;
		}
		// Compute info gains
		m_CHIs = new double[data.numAttributes()];
		for (int i = 0; i < data.numAttributes(); i++) {
			if (i != classIndex) {
				m_CHIs[i] = WekaComputeUtil.computeMaxCHI(counts[i], classCounts);
				// System.out.println(m_CHIs[i]);
			}
		}
	}

}
