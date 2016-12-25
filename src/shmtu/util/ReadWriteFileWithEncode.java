package shmtu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ReadWriteFileWithEncode {
	public static final String DEFAULT_ENCODE = "UTF-8";
	/**
	 * 按照给定的字符集 写入文件
	 * 先删除再写入
	 * @param fileName
	 * @param content
	 * @param encode
	 * @throws IOException
	 */
	public static void writeByEncodie(String filePath,String content,String encode)throws IOException{
		if(encode==null||"".equals(encode))
			encode = DEFAULT_ENCODE;
		File file = new File(filePath);
		file.delete();
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),encode));
		writer.write(content);
		writer.close();
	}
	/**
	 * 按照指定编码集读文件
	 */
	public static String readFromEncode(String filePath,String encode) throws IOException{
		if(encode==null||"".equals(encode))
			encode = DEFAULT_ENCODE;
		File file = new File(filePath);
		String content = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encode));
		String line = null;
		while((line=reader.readLine())!=null){
			content += line+"/n";
		}
		return content;
	}
	/**
	 * 按照指定的编码 按行读文件 返回每行数组
	 * @param file
	 * @param charset
	 * @return
	 */
	public static List<String> readlinesByEncode(File file,String charset){
		List<String> lineList = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
			String line = null;
			while((line=reader.readLine())!=null){
				lineList.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lineList;
	}
	/**
	 * 按照文件本身的编码读文件
	 * 按行读取
	 * 返回文本每行的内容
	 */
//	public static List<String> readByCharset(String filePath){
//		List<String> lineList = new ArrayList<String>();
//		File file = new File(filePath);
//		String charset = FindCharsetName.getCharsetName(file);
//		BufferedReader reader;
//		try {
//			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charset));
//			String line = null;
//			while((line=reader.readLine())!=null){
//				lineList.add(line);
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return lineList;
//	}
}
