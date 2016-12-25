package nlpir.segment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import kevin.zhang.NLPIR;
import shmtu.util.FileOptionUtil;
import shmtu.util.ReadWriteFileWithEncode;

public class NLPIRUtil {
	public static NLPIR nlpir = new NLPIR();
	static {
		// NLPIR_Init方法第二个参数设置0表示编码为GBK, 1表示UTF8编码(此处结论不够权威)
		try {
			if (!NLPIR.NLPIR_Init("".getBytes("utf-8"), 1)) {
				System.out.println("NLPIR初始化失败...");
			} 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void closeNLPIR(){
		NLPIR.NLPIR_Exit(); 
	}
	//新词识别 新词加入字典 识别目录下的所有新词
	public static void NewWordIdentification(String corpusDirectory) throws Exception{
		//获取语料库的 目录
		Set<String> fileSet = FileOptionUtil.getFileFromDirectory(new File(corpusDirectory));
		//开始新词识别
		nlpir.NLPIR_NWI_Start();
		if(fileSet!=null&&fileSet.size()>0){
			for(String filepath:fileSet){
				////批量增加输入文件，可以不断循环调用NLPIR_NWI_AddFile或者NLPIR_NWI_AddMem
				nlpir.NLPIR_NWI_AddFile(filepath.getBytes("utf-8"));
			}
		}
		//结束
		nlpir.NLPIR_NWI_Complete();
		byte[] newwordBytes = nlpir.NLPIR_NWI_GetResult(true);
		String newwordStr = new String(newwordBytes,"utf-8");
		ReadWriteFileWithEncode.writeByEncodie("newword.txt", newwordStr, "utf-8");
//		System.out.println("新词识别结果"+newwordStr);
		//添加到字典中
		nlpir.NLPIR_NWI_Result2UserDict();
	}
	private static String segmentSimpleTest(String text){
		try {
			byte [] resBytes = nlpir.NLPIR_ParagraphProcess(text.getBytes("UTF-8"), 1);
			text = new String(resBytes,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}
	/**
	 * 带词性的返回
	 * @param text
	 * @return
	 */
	public static String segmentHasPOSs(String text){
		try {
			byte [] resBytes = nlpir.NLPIR_ParagraphProcess(text.getBytes("UTF-8"), 1);
			text = new String(resBytes,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}
	public static void main(String []args) throws Exception{
//		"爱斯科	n"
//		"iThinker	n"
		String text = "@zhang a123@qq.com http://www.baidu.com";
		System.out.println("添加新词识别前的分词结果：");
		System.out.println(segmentSimpleTest(text));
//		String srcCorpusDirectory = WeiboConfig.getValue("extractWeiboTxtToDirectory");
//		NewWordIdentification(srcCorpusDirectory);
//		//
//		System.out.println("添加识别新词后的分词结果：");
//		System.out.println(segmentSimpleTest(text));
		//
		closeNLPIR();
	}
}
