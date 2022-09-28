package test2;

import com.ht.util.Con100HR;

import java.util.regex.Pattern;

public class A {

	public void test() {
		Con100HR con100hr = new Con100HR();
		String account = "HM0001469";
		String name = "瞿仕军";
		String node = "MiCasing";
		String factory = "B2";

		String Lot = "000001597750/9";

		if(!Pattern.compile(".*[a-zA-Z]+.*").matcher(Lot).matches()) { //无条码工单
			System.out.println("无字母");
		}else { //有条码工单
			System.out.println("有字母");
		}

		
	}

}
