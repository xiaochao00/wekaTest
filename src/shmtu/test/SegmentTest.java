package shmtu.test;

import java.io.File;
import java.util.List;

import nlpir.segment.SegmentWordUtil;
import shmtu.util.ReadWriteFileWithEncode;

public class SegmentTest {
	public static void main(String[]ars) throws Exception{
		List<String> atributenames = ReadWriteFileWithEncode.readlinesByEncode(new File("attributes_ig_1000.txt"), ReadWriteFileWithEncode.DEFAULT_ENCODE);
		if(atributenames!=null&&atributenames.size()>0){
			for(String word:atributenames){
				String pos = SegmentWordUtil.getPOS(word);
				System.out.println(word + " 词性是 " + pos);
			}
		}
	}
}
