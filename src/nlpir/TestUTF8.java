package nlpir;

import kevin.zhang.NLPIR;

public class TestUTF8 {  
  
    public static void main(String[] args) {  
        try {  
        	testUTF8Nominal();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    static void testUTF8() throws Exception {  
        // 创建接口实例  
        NLPIR nlpir = new NLPIR();  
        // NLPIR_Init方法第二个参数设置0表示编码为GBK, 1表示UTF8编码(此处结论不够权威)  
        if (!NLPIR.NLPIR_Init("".getBytes("utf-8"), 1)) {  
            System.out.println("NLPIR初始化失败...");  
            return;  
        }  
        String temp = "每天的日报都记得要发送, 以配合经理掌握项目的进度情况.";  
        // 要统一编码, 否则分词结果会产生乱码  
        byte [] resBytes = nlpir.NLPIR_ParagraphProcess(temp.getBytes("UTF-8"), 1);  
        System.out.println("分词结果: " + new String(resBytes, "UTF-8"));  
          
        String utf8File = "./test/test-utf8.TXT";  
        String utf8FileResult = "./test/test-utf8_result.TXT";  
        nlpir.NLPIR_FileProcess(utf8File.getBytes("utf-8"), utf8FileResult.getBytes("utf-8"), 1);  
          
        // 退出, 释放资源  
        NLPIR.NLPIR_Exit();  
    }  
    static void testUTF8Nominal() throws Exception {  
        // 创建接口实例  
        NLPIR nlpir = new NLPIR();  
        // NLPIR_Init方法第二个参数设置0表示编码为GBK, 1表示UTF8编码(此处结论不够权威)  
        if (!NLPIR.NLPIR_Init("".getBytes("utf-8"), 1)) {  
            System.out.println("NLPIR初始化失败...");  
            return;  
        }  
        String temp = "麻婆豆腐";  
        // 要统一编码, 否则分词结果会产生乱码  
        //上面例子中不论输出到控制台还是文本文件中的分词结果都带有词性，如果我想输出不带词性的分词结果，需要将NLPIR_ParagraphProcess方法和NLPIR_FileProcess方法中的第二个参数设置为0：
        byte [] resBytes = nlpir.NLPIR_ParagraphProcess(temp.getBytes("UTF-8"), 1);  
        System.out.println("分词结果: " + new String(resBytes, "UTF-8"));  
          
        String utf8File = "./test/test-utf8.dat";  
        String utf8FileResult = "./test/test-utf8_result.dat";  
        nlpir.NLPIR_FileProcess(utf8File.getBytes("utf-8"), utf8FileResult.getBytes("utf-8"), 1);  
          
        // 退出, 释放资源  
        NLPIR.NLPIR_Exit();  
    }  
}  
