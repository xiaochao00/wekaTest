package shmtu.util;
/*
 * 模式匹配工具类
 */
public class PatternMatchUtil {
	public static final String NUM_REG = "^\\d+[年,月,日,时,分,秒]?$";
	public static boolean patternNumAndDate(String text){
		boolean f ;
		text = text.trim();
		if(text==null||"".equals(text))
			f = false;
		else
			f = text.matches(NUM_REG);
		return f;
	}
	public static void main(String[]args){
		String d = "00 ";
		System.out.println(patternNumAndDate(d));
	}
}
