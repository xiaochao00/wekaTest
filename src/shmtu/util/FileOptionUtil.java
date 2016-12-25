package shmtu.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
/**
 * 文件操作 工具类
 * @author HP_xiaochao
 *
 */
public class FileOptionUtil {
	public static boolean hasFile(String path){
		boolean flag = false;
		File file = new File(path);
		flag = file.exists();
		return flag;
	}
	public static void createFile(String path){
		if(!hasFile(path)){
			File file = new File(path);
			file.mkdir();
		}
	}
	/**
	 * 返回一个目录下的所有txt文件
	 * @param directory
	 * @return 文件列表
	 */
	public static List<File> getTXTFilesFromDirector(String directory){
		List<File> fileList = new ArrayList<File>();
		//
		File file = new File(directory);
		File[] fileArr = file.listFiles(new TxtFileAccept());
		if(fileArr!=null&&fileArr.length>0){
			for(File f:fileArr){
				fileList.add(f);
			}
		}
		//
		return fileList;
	}
	/**
	 * 得到目录下的所有文件，包括多级目录 返回文件的绝对路径名列表
	 * @param directory
	 * @return
	 */
	public static List<String> getFileFromDorectory(File directory){
		List<String> fileList = new LinkedList<String>();
		if(directory.isDirectory()){
			for(File file:directory.listFiles()){
				fileList.addAll(getFileFromDorectory(file));
			}
		}else{
			String filepath = directory.getAbsolutePath();
			fileList.add(filepath);
		}
		return fileList;
	}
	public static Set<String> getFileFromDirectory(File directory){
		Set<String> fileList = new HashSet<String>();
		if(directory.isDirectory()){
			for(File file:directory.listFiles()){
				fileList.addAll(getFileFromDorectory(file));
			}
		}else{
			String filepath = directory.getAbsolutePath();
			fileList.add(filepath);
		}
		return fileList;
	}
	public static void main(String[]args){
		String directory = "d:/weibo/catagory/thuctc_classifier/dataset";
		List<String> filePathList = getFileFromDorectory(new File(directory));
		Set<String> filePathSet = getFileFromDirectory(new File(directory));
		System.out.println(filePathList.size());
		System.out.println(filePathSet.size());
		System.out.println(filePathList.get(0));
	}
}
/**
 * 给定 afternoon morning night 获取上下午晚上的微博文件，分类主题
 *
 */
class TxtFileAccept implements  FilenameFilter{
	String suffix = ".txt";
	public boolean accept(File dir,String name){
		return name.endsWith(suffix);
	}
}
