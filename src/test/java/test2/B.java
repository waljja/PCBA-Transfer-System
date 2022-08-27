package test2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;

import com.ht.api.ObSendRecType;
import com.ht.api.ResultCode;
import com.ht.controller.PcbaInventoryConntroller;
import com.ht.util.Con100HR;
import com.ht.util.Con51DB;
import com.ht.util.Con72DB;
import com.ht.util.Con75DB;
import com.ht.util.SAPServiceUtil;
import com.ht.util.SqlApi;

public class B {

	@Test
	public void test() throws ParseException {
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = formatter.parse("2022-06-30 17:00:00.000");
		Date date2 = formatter.parse("2022-07-01 13:00:00.000");
		Con100HR con100hr  = new Con100HR();
		Con75DB con75db = new Con75DB();
		Con51DB con51db = new Con51DB();
		Con72DB con72db = new Con72DB();
		boolean flag = getDate(date1,date2);
		//System.out.println(flag);
	 
		//System.out.println(Pattern.compile("(?i)[a-z]").matcher("000001591231/1").find());
		
//		System.out.println("555:"+PcbaInventoryConntroller.Fifo101("000001594171/000003", "000001594171","","","B2"));
//		
//		String a = "000001590952/A4";
//		String b = "000001590952/A4";
//		
//		System.out.println(a.substring(0,12));
//		System.out.println(Pattern.compile("(?i)[a-z]").matcher(a).find());
//		System.out.println(Pattern.compile("(?i)[a-z]").matcher(b).find());
		
		SAPServiceUtil sap = new SAPServiceUtil();
		try {
			System.out.println("111："+sap.getSAPPNRec("620-TUBE001-00DR3","5000"));
			System.out.println("111："+sap.getSAPPN("620-TT4W002-00R1","1100"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("035-0000220-00DR".indexOf("00DR1")!=-1 || "035-0000220-00DR".indexOf("00DR3")!=-1  ? "5000" :"1100");
		
 
/*		if("620-TT12001-00DR1_SA".indexOf("00DR1")!=-1) {
			System.out.println("存在包含关系");
		}else {
			System.out.println("不存在包含关系");
		}*/
		
	}
	
	public static boolean getDate(Date date1, Date date2){
        Calendar date = Calendar.getInstance();
        boolean flag = true;
        date.setTime(new Date());
        //获取开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(date1);
        //获取结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(date2);
        if ((date.after(begin) && date.before(end)) ||
                (date.getTime() == begin.getTime() || date.getTime() == end.getTime())) {
            System.out.println("当前时间在开始时间与结束时间之间");
            flag = false;
        }
        return flag;
    }
}
