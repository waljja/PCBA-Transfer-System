package com.ht.util;

public class SqlApi {
	 //LOT号OB数据
	public static String SelLotData(String Lot){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("SELECT Handover.FGSN as Batch,Handover.FGQTY as Qty,MO.MOName as Wo,MO.MOQtyRequired as WoQty,MO.FactoryId as Factory,MO.SendStorage as sendLocation,MO.ReceiveStorage as RecLocation,ProductRoot.ProductName as Pn FROM Handover left JOIN "
				+ " MO ON Handover.MoId = MO.MOId left JOIN "
				+ " SpecificationRoot ON Handover.SendSpecificationId = SpecificationRoot.DefaultSpecificationId LEFT OUTER JOIN "
				+ " SpecificationRoot AS SpecificationRoot_1 ON Handover.ReceiveSpecificationId = SpecificationRoot_1.DefaultSpecificationId left JOIN "
				+ " ProductRoot ON MO.ProductId = ProductRoot.DefaultProductId "
				+ " where Handover.FGSN = '" + Lot + "' ");
		return sbsql.toString();
	}
	//PCBA超市库存
	public static String downloadData(String StartTime,String EndTime){
		StringBuilder sbsql = new StringBuilder();
			sbsql.append("select WO,WOQuantity,UID,PartNumber,Factory,Location,AvailableBatch,AvailableQuantity,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant,CreateUser,CreateTime  from DL_PCBAInventory where State =1");
		return sbsql.toString();
	}
	//101入库记录
	public static String download101Data(String StartTime,String EndTime){
		StringBuilder sbsql = new StringBuilder();
		if(StartTime.equals("")||EndTime.equals("")){
			sbsql.append("select WO,WOQuantity,AvailableBatch,AvailableQuantity,PartNumber,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,Location,plant,CreateUser,CreateTime from DL_PCBAInventory where (State = 0 or State =1)");
		}else {
			sbsql.append("select WO,WOQuantity,AvailableBatch,AvailableQuantity,PartNumber,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,Location,plant,CreateUser,CreateTime from DL_PCBAInventory where (State = 0 or State =1) and CreateTime >='"+StartTime+" 00:00:01' and CreateTime<='"+EndTime+" 23:59:59'");	
		}
		return sbsql.toString();
	}
	//313_315收发记录
	public static String download313_315Data(String StartTime,String EndTime){
		StringBuilder sbsql = new StringBuilder();
		if(StartTime.equals("")||EndTime.equals("")){
			sbsql.append("select PS.WO,PS.WOQTY,PS.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PS.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
				" from DL_PCBAInventory PIT right join DL_PCBASMT PS on PIT.UID = PS.UID " +
				" where (PIT.State = 0 or PIT.State =1)" +
				" union all" +
				" select PC.WO,PC.WOQTY,PC.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PC.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
				" from DL_PCBAInventory PIT right join DL_PCBACOB PC on PIT.UID = PC.UID " +
				" where (PIT.State = 0 or PIT.State =1)" +
				" union all" +
				" select PM.WO,PM.WOQTY,PM.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PM.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
				" from DL_PCBAInventory PIT right join DL_PCBAMI PM on PIT.UID = PM.UID " +
				" where (PIT.State = 0 or PIT.State =1)");
		}else {
			sbsql.append("select PS.WO,PS.WOQTY,PS.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PS.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
				" from DL_PCBAInventory PIT right join DL_PCBASMT PS on PIT.UID = PS.UID " +
				" where (PIT.State = 0 or PIT.State =1)and PS.CreateTime >='"+StartTime+" 00:00:01' and PS.CreateTime<='"+EndTime+" 23:59:59'" +
				" union all" +
				" select PC.WO,PC.WOQTY,PC.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PC.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
				" from DL_PCBAInventory PIT right join DL_PCBACOB PC on PIT.UID = PC.UID " +
				" where (PIT.State = 0 or PIT.State =1)and PC.CreateTime >='"+StartTime+" 00:00:01' and PC.CreateTime<='"+EndTime+" 23:59:59'" +
				" union all" +
				" select PM.WO,PM.WOQTY,PM.PartNumber,SendingBatch,SendingBatchQTY,SendingUser,SendingTime,SendLocation,(CASE WHEN PM.Status = '1' THEN '已发送'else '已接收' END)as Status,ReceiveUser,ReceiveTime,RecLocation,(CASE WHEN workcenter = '1' THEN 'SMT'WHEN workcenter = '2' THEN 'COB'WHEN workcenter = '3' THEN 'MI'WHEN workcenter = '4' THEN 'Casing' ELSE '' END)as workcenter,plant " +
				" from DL_PCBAInventory PIT right join DL_PCBAMI PM on PIT.UID = PM.UID " +
				" where (PIT.State = 0 or PIT.State =1)and PM.CreateTime >='"+StartTime+" 00:00:01' and PM.CreateTime<='"+EndTime+" 23:59:59'");
		}
		return sbsql.toString();
	}
	//LOT号SN明细
	public static String SelSmtSnData(String Lot){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select LotSN as SN,SMTLot as Lot from Lot where SMTLot= '"+ Lot + "' ");
		return sbsql.toString();
	}
	//LOT号SN明细
	public static String SelCobSnData(String Lot){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select LotSN as SN,BondingLot as Lot from Lot where BondingLot= '"+ Lot + "' ");
		return sbsql.toString();
	}
	//LOT号SN明细
	public static String SelMiSnData(String Lot){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select LotSN as SN,DIPElot as Lot from Lot where DIPElot = '"+ Lot + "' ");
		return sbsql.toString();
	}
	//LOT号SN明细
	public static String SelCasingSnData(String Lot){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select LotSN as SN,ASSLot as Lot from Lot where ASSLot= '"+ Lot + "' ");
		return sbsql.toString();
	}
	// 根据工单取入库时间最晚的UID（COB）
	public static String CobFifo(String Wo){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("SELECT top 1 UID FROM [HT_InterfaceExchange].[dbo].[DL_PCBAInventory] where WO = '"+Wo+"' order by CreateTime desc");

		return sbsql.toString();
	}
	// COB
	public static String CobObFifo(String Lot,String Wo,String Remark){
		StringBuilder sbsql = new StringBuilder();

		if(Remark.equals("")){
			sbsql.append("select top 1 FGSN from Handover where CreateDate >(select CreateDate from Handover where FGSN = '"+Lot+"') and  FGSN like '"+Wo+"%' and CHARINDEX('-', FGSN) = 0  order by CreateDate ");
		}else {
			sbsql.append("select top 1 FGSN from Handover where CreateDate >(select CreateDate from Handover where FGSN = '"+Lot+"'and Remark = '"+Remark+"') and  FGSN like '"+Wo+"%' and CHARINDEX('-', FGSN) = 0 and Remark = '"+Remark+"' order by CreateDate ");
		}

		return sbsql.toString();
	}
	//OB 第一个 Lot号
	public static String CobObFirst(String Wo,String Remark){
		StringBuilder sbsql = new StringBuilder();

		if(Remark.equals("")){
			sbsql.append("select top 1 FGSN from Handover where FGSN like '"+Wo+"%' and CHARINDEX('-', FGSN) = 0 order by CreateDate ");
		}else {
			sbsql.append("select top 1 FGSN from Handover where FGSN like '"+Wo+"%' and CHARINDEX('-', FGSN) = 0 and Remark = '"+Remark+"' order by CreateDate ");
		}

		return sbsql.toString();
	}
	
	//PCBA SMT
	public static String SmtFifo(String Wo){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select top 1 UID from HT_InterfaceExchange.[dbo].[DL_PCBAInventory] where WO = '"+Wo+"' order by cast(AvailableBatch as int) desc");

		return sbsql.toString();
	}
	//找到下一个按顺序应该入库的SN号
	public static String SmtObFifo(String NewSN, String Wo){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select top 1 NewSN from [HT_GenericTester].[dbo].[PP_SMTFIFOPrint] where PrintTime >(select top 1 PrintTime from [HT_GenericTester].[dbo].[PP_SMTFIFOPrint] where NewSN = '"+NewSN+"' order by PrintTime desc) and  NewSN like '"+Wo+"%' order by PrintTime");

		return sbsql.toString();
	}
	//找到工单的第一个SN号
	public static String SmtObFirst(String Wo){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("select top 1 NewSN from [HT_GenericTester].[dbo].[PP_SMTFIFOPrint] where NewSN like '"+Wo+"%' order by PrintTime ");

		return sbsql.toString();
	}

	//修改OB发料
	public static String UpObSend(String Lot,String User,String ObType){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("update Handover set SendUserId = '"+User+"',SendSpecificationId = '"+ObType+"',FGStatus = '0',SendTime = GETDATE() where FGSN = '"+Lot+"'");
		return sbsql.toString();
	}

	//修改OB收料
	public static String UpObRec(String Lot,String User,String ObType){
		StringBuilder sbsql = new StringBuilder();
		sbsql.append("update Handover set ReceiveUserId = '"+User+"',ReceiveSpecificationId = '"+ObType+"',FGStatus = '1',ReceiveTime = GETDATE() where FGSN='"+Lot+"'");
		return sbsql.toString();
	}
}
