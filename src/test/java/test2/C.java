package test2;

import static org.junit.Assert.*;

import java.sql.ResultSet;

import org.junit.Test;

import com.ht.util.Con100HR;

public class C {

	@Test
	public void test() {
		Con100HR con100hr = new Con100HR();
		ResultSet rs,rs1;
		int num = 0;
		
		try {
			//rs = con100hr.executeQuery("select  TransactionHistoryId from xTend_MaterialTransactions where PO_NUMBER = '000001592341' and TransactionType = '101'");
			//if (rs.next()) {
				rs1 = con100hr.executeQuery("select BatchId,ItemId,TransactionHistoryId from xTend_MaterialTransactionsGroup where TransactionHistoryId in(select  TransactionHistoryId from xTend_MaterialTransactions where  PO_NUMBER like '000001593290' and TO_Name = '569A08ACC7F573163D3F08DA74ED3538' )");	
				while (rs1.next()) {
					con100hr.executeUpdate("update xTend_MaterialTransactions set FromStock = 'RH80' where TransactionHistoryId = '"+rs1.getString("TransactionHistoryId")+"'");
					con100hr.executeUpdate("update xTend_MaterialTransactionsSummary set FromStock = 'RH80' where BatchId = '"+rs1.getString("BatchId")+"'and ItemId = '"+rs1.getString("ItemId")+"'");
					num+=1;//
				}
				
			//}
			System.out.println("已执行:"+num);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
	}

}
