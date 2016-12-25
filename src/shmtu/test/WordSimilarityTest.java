package shmtu.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.buaa.edu.wordsimilarity.WordSimilarity;
import shmtu.util.ReadWriteFileWithEncode;
import shmtu.wordsimilarity.WordSimilarityUtil;

public class WordSimilarityTest {
	public static void main(String[]args) throws IOException{
		double s = WordSimilarity.simWord("红旗", "紧急");
//		System.out.println("similaity is :" + s);
		doSome();
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public static void doSome() throws IOException{
		List<String> atributenames = ReadWriteFileWithEncode.readlinesByEncode(new File("attributes_ig_1000.txt"), ReadWriteFileWithEncode.DEFAULT_ENCODE);
//		WordSimilarityUtil.attributeSimilarity(atributenames);
//		WordSimilarityUtil.attributeSimilarityAndPOS(atributenames);
		List<String> simialrityList = WordSimilarityUtil.computeSimilarityWordList(atributenames);
		for(int i=0;i<simialrityList.size();i++){
			System.out.println( " similarity list is:" + simialrityList.get(i));
		}
	}
}
