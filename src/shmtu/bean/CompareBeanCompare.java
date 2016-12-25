package shmtu.bean;

import java.util.Comparator;

/**
 * 
 * @author HP_xiaochao
 *2016年12月21日
 *
 */
public class CompareBeanCompare implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		CompareBean c1 = (CompareBean)o1;
		CompareBean c2 = (CompareBean)o2;
		if(c1.getValue()-c2.getValue()>0.0)
			return 1;
		else 
			if(c1.getValue()-c2.getValue()==0)
				return 0;
		else
			return -1;
	}

}
