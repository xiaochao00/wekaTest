package shmtu.wordsimilarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.buaa.edu.wordsimilarity.WordSimilarity;
import nlpir.segment.SegmentWordUtil;
import shmtu.util.CommonUtils;

/**
 * 词语相似度计算 工具包
 * 
 * @author HP_xiaochao
 *
 */
public class WordSimilarityUtil {
	private static final double minSimilarityThreshold = 0.97;
	/**
	 * 计算属性列表的 相似度
	 * 
	 * @param attributeNameList
	 */
	public static void attributeSimilarity(List<String> attributeNameList) {
		if (attributeNameList != null && attributeNameList.size() > 1) {
			int num = attributeNameList.size();
			for (int i = 0; i < num - 1; i++) {
				for (int j = i + 1; j < num; j++) {
					String word1 = attributeNameList.get(i);
					String word2 = attributeNameList.get(j);
					double similarity = WordSimilarity.simWord(word1, word2);
					if (similarity > 0.97 && (!(word1.length() == 1 && word2.length() == 1))) {
						System.out.println(word1 + " and " + word2 + " similarity is : " + similarity);
						// System.out.println(
						// WordSimilarity.simWord(word1,word2));
					}
				}
			}
		}
	}

	public static void attributeSimilarityAndPOS(List<String> attributeNameList) throws IOException {
		String content = "";
		if (attributeNameList != null && attributeNameList.size() > 1) {
			int num = attributeNameList.size();
			Map<String, String> posMap = SegmentWordUtil.getPOSMap(attributeNameList);
			int count1 = 0;
			int count2 = 0;
			for (int i = 0; i < num - 1; i++) {
				for (int j = i + 1; j < num; j++) {
					String word1 = attributeNameList.get(i);
					String word2 = attributeNameList.get(j);
					String pos1 = posMap.get(word1);
					String pos2 = posMap.get(word2);
					double similarity = WordSimilarity.simWord(word1, word2);
					if (similarity > 0.97 && (!(word1.length() == 1 && word2.length() == 1))) {
						count1++;
						if (pos1.equals(pos2)) {
							count2++;
							content += word1 + " and " + word2 + " similarity is : " + similarity + ";pos:" + pos2
									+ "\r\n";
							System.out.println(
									word1 + " and " + word2 + " similarity is : " + similarity + ";pos:" + pos2);
						}
					}
				}
			}
			content += "词性筛选前后 " + count1 + " ---;--- " + count2;
			System.out.println("词性筛选前后 " + count1 + " ---;--- " + count2);
			// ReadWriteFileWithEncode.writeByEncodie("语义相似度带词性的计算结果.txt",
			// content, ReadWriteFileWithEncode.DEFAULT_ENCODE);
		}
	}

	/**
	 * 根据属性列表 返回 每个词对应的想相似词 组合
	 * 
	 * @param attributs
	 * @return
	 */
	public static List<String> computeSimilarityWordList(List<String> attributeNameList) {
		String content = "";
		System.out.println("start compute attribute between-in similarity.num is : " + attributeNameList.size());
		List<String> newAttributeNameList = new ArrayList<String>();
		List<String> similarityList = new ArrayList<String>();
		Map<String, String> similityMap = new HashMap<String, String>();
		if (attributeNameList != null && attributeNameList.size() > 1) {
			int num = attributeNameList.size();
			Map<String, String> posMap = SegmentWordUtil.getPOSMap(attributeNameList);
			int count1 = 0;
			int count2 = 0;
			for (int i = 0; i < num - 1; i++) {
				String word1 = attributeNameList.get(i);
				for (int j = i + 1; j < num; j++) {
					String word2 = attributeNameList.get(j);
					String pos1 = posMap.get(word1);
					String pos2 = posMap.get(word2);
					double similarity = WordSimilarity.simWord(word1, word2);
					if (similarity > minSimilarityThreshold && (!(word1.length() == 1 && word2.length() == 1))) {
						count1++;
						if (pos1.equals(pos2)) {
							count2++;
							if (!similityMap.containsKey(word1)) {
								content += word1 + " and " + word2 + " similarity is : " + similarity + ";pos:" + pos2 + "\r\n";
								newAttributeNameList.add(word1);
								similityMap.put(word1, word2);
							} else {
								String oldSimilarityList = similityMap.get(word1);
								similityMap.put(word1, oldSimilarityList + " " + word2);
							}
						}
					}
				}
			}
			content += "词性筛选前后 " + count1 + " ---;--- " + count2;
			CommonUtils.writeFile("result/语义相似度带词性的计算结果.txt", content);
		}
		CommonUtils.print("done for compute attributes between-in similarity and filter with POS.similarity attributes num is :" + newAttributeNameList.size());
		//筛选 去重
		similarityList = filterSimilarity(similityMap,newAttributeNameList);
		//
		return similarityList;
	}
	/**
	 * 对计算出来的属性相似列表 去重 筛选
	 * 原则：
	 * 	1.如果词w1的列表中有词w2，那么比较词w2的相似列表中是否全部包含在w1的相似列表中，如果是就在原来的map中删除关于w2的近似。
	 * 如果不包含，就在w1的近似列表中删除对w2的近似。由于比较相似的时候采用顺序的形式，因此 只用从前到后即可，不用多次双重循环
	 * 	2.为了保存顺序的形式，返回列表形式，词w1和它的相似列表组合形成字符串返回。删除没有相似列表的w1.
	 * @param similarityListMap
	 * @param attributelistList
	 * @return
	 */
	private static List<String> filterSimilarity(Map<String, String> similarityListMap,List<String> attributelistList) {
		CommonUtils.print("filter similarity attribute .remove same similarity .bigin num attribute is: " + attributelistList.size());
		List<String> similaritylistFilterList = new ArrayList<String>();
		List<String> newAttributeNameList = new ArrayList<String>();
//		Map<String,String> filterSimilaritylistMap = new HashMap<String,String>();
		for (String k : attributelistList) {
			String similaritylist = similarityListMap.get(k);
			if(similaritylist!=null){
				for (String str : similaritylist.split(" ")) {
					if (!CommonUtils.stringIsEmpty(str)) {
						String similaritylist2 = similarityListMap.get(str);
						if(!newAttributeNameList.contains(k))
								newAttributeNameList.add(k);
						if (compareSimilaritylistContain(similaritylist, similaritylist2)) {
							similarityListMap.remove(str);
						}else{
							similarityListMap.put(k, similaritylist.replace(str, ""));
						}
					}
				}
			}
		}
		//
		String content = "";
		for (String a : newAttributeNameList) {
			String similarity = similarityListMap.get(a);
			if(!CommonUtils.stringIsEmpty(similarity)){
				similaritylistFilterList.add(a + " " + similarity);
				content += a + " similarity attribute list is : " + similarity +"\r\n";
			}
		}
		content+="num : " + similaritylistFilterList.size();
		CommonUtils.writeFile("result/语义相似度带词性的计算结果去重筛选后.txt", content);
		CommonUtils.print("filter similarity and remove same similarity attributes.result attribute num : " + similaritylistFilterList.size());
		return similaritylistFilterList;
	}

	/**
	 * 在两个不同的词的相似列表中比较 是否前一个列表包含后一个
	 * 因为只有包含了，才说明这两个词可以替换。认为近似，否则，在第一个词的近义词列表中去除后面这个词
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	private static boolean compareSimilaritylistContain(String l1, String l2) {
		if (l1 == null || "".equals(l1))
			return false;
		if (l2 == null || "".equals(l2))
			return true;
		boolean flag = true;
		String arr1[] = l1.split(" ");
		List<String> arr1List = CommonUtils.arrayToList(arr1);
		String arr2[] = l2.split(" ");
		for (String a : arr2) {
			if (!arr1List.contains(a))
				return false;
		}
		return flag;
	}
}
