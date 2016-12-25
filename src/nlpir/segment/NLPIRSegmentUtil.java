package nlpir.segment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Logger;

import kevin.zhang.NLPIR;
import shmtu.util.LangUtils;
import shmtu.util.PatternMatchUtil;

public class NLPIRSegmentUtil {
	private static Logger LOG = Logger.getAnonymousLogger();
	private static Set<String> stopwordsSet = null;
	static {
		// NLPIR_Init方法第二个参数设置0表示编码为GBK, 1表示UTF8编码(此处结论不够权威)
		try {
			if (!NLPIR.NLPIR_Init("".getBytes("utf-8"), 1)) {
				System.out.println("NLPIR初始化失败...");
			} else {
				stopwordsSet = loadStopwords();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String segment(String text) {
		//
		text = LangUtils.removeEmptyLines(text);
		text = LangUtils.removeExtraSpaces(text);
		text = LangUtils.removeWbNoise(text);
		//
		byte[] resBytes;
		String segment = text;
		try {
			//第二个参数 指示分割的结果是否带词性标识
			resBytes = NLPIRUtil.nlpir.NLPIR_ParagraphProcess(text.getBytes("UTF-8"), 0);
			segment = new String(resBytes, "utf-8");
			segment = removeStopword(segment);
			if(segment!=null&&(!"".equals(segment))){
				String newSegment = "";
				for(String str:segment.split(" ")){
					if(!PatternMatchUtil.patternNumAndDate(str))
						newSegment = newSegment + " " + str;
					else
						System.out.println("remove num " + str);
				}
				segment = newSegment;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return segment;
	}

	/**
	 * 使用porterStemmer和停用词表来切分处理英文
	 */
	public static String removeStopword(String segment) {
		StringBuffer segmentText = new StringBuffer("");
		String[] tokens = segment.split("[^0-9\\p{L}]+");
		LinkedList<String> results = new LinkedList<String>();
		for (int i = 0; i < tokens.length; i++) {
			if (!stopwordsSet.contains(tokens[i])&&(!tokens[i].equals(" "))) {
				results.add(tokens[i]);
			}
		}
		if(results!=null&&results.size()>0){
			for(String str:results){
				segmentText.append(str).append(" ");
			}
		}
		return segmentText.toString();
	}

	private static Set<String> addStopwords(String type) {
		Set<String> stopwords = new HashSet<String>();
		InputStream input = null;
		String filepath = "edu/shmtu/nlap/weibo/catagory/segment/" + type + ".txt";
		input = NLPIRSegmentUtil.class.getClassLoader().getResourceAsStream(filepath);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				stopwords.add(line.trim());
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
			LOG.warning("Cannot load stopwords, ignore stopwords.");
		}
		return stopwords;
	}

	private static Set<String> loadStopwords() {
		Set<String> stopwords = new HashSet<String>();
		stopwords = addStopwords("chinese_stopword");
		stopwords.addAll(addStopwords("english_stopword"));
		stopwords.addAll(addStopwords("html_parse_stopword"));
		stopwords.addAll(addStopwords("weibo_stopword"));
		
		return stopwords;
	}
	public static void main(String []args){
		String text = "1080p 湖南卫视 2017 重点 走势 官方 版 周 播 时间 固定 每周 周三 晚 00 王牌 节目 快乐大本营 每周 晚 天天向上 每周 晚 00 回归 节目 百 变 咖 秀 花儿 少年 周四 综艺 挡 婚恋 节目 贯穿 季度 季 播 节目 数量 增至 档 贯穿 周四 00 周五 周六 00 ',综艺娱乐";
		System.out.println(segment(text));
	}
	
}
