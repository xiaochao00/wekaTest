package nlpir.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 特征 分词 工具类
 * @author HP_xiaochao
 *
 */
public class SegmentWordUtil {
	
	/**
	 * 返回 对应的属性列表的 
	 * 						属性-词性映射
	 * @param tremList
	 * @return
	 * @throws Exception 
	 */
	public static Map<String,String> getPOSMap(List<String> tremList) {
		Map<String,String> posMap = new HashMap<String,String>();
		if(tremList!=null&&tremList.size()>0){
			for(String word:tremList){
				String pos = SegmentWordUtil.getPOS(word);
//				System.out.println(word + " 词性是 " + pos);
				posMap.put(word, pos);
			}
		}
		return posMap;
	}
	/**
	 * 返回对应属性列表的 词性列表
	 * @param tremList
	 * @return
	 * @throws Exception
	 */
	public static List<String> getPOSList(List<String> tremList){
		List<String> posList = new ArrayList<String>();
		if(tremList!=null&&tremList.size()>0){
			for(String word:tremList){
				String pos = SegmentWordUtil.getPOS(word);
//				System.out.println(word + " 词性是 " + pos);
				posList.add( pos);
			}
		}
		return posList;
	}
	/**
	 * 返回单个词的词性 
	 * @param signalWord
	 * @return
	 * @throws Exception
	 */
	public static String getPOS(String signalWord){
		String segments = NLPIRUtil.segmentHasPOSs(signalWord);
//		if(segments.split(" ").length>1){
//			System.out.println("该方法是单个词语的带词性返回。"+segments);
//			throw new Exception("该方法是单个词语的带词性返回。");
//		}
		String pos = segments.split("/")[1];
		return pos;
	}
}
