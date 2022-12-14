package com.ht.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ht.vo.SapClosingTime;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;

@Mapper
public interface PcbaInventoryMapper {
	String specialPn(String pn);
	
	int retryPosting(@Param("BatchId")String BatchId,@Param("ItemId")String ItemId);
	
	List<Map<String,String>> GetBatchId(@Param("Lot")String Lot,@Param("Type")String Type);
	
	List<Map<String,Object>> PCBAInventoryData(@Param("pageIndex")int pageIndex,@Param("pageSize")int pageSize,@Param("plant1")List<String> plant1,@Param("workcenter1")List<String> workcenter1,@Param("wo1")List<String> wo1,@Param("partnumber1")List<String> partnumber1);
	
	//查询Smt工序批次数据
	SendRecDataVo QRYSmtData(String Lot);
	//查询无条码Smt批次数量
	SendRecDataVo QRYSmtDataNo(String Lot);
	//把数据插入PCBA库存表
	int PcbaStorage(SendRecDataVo SendRecData);
	//查询Pcba库存信息
	SendRecDataVo BatchData(@Param("Wo")String Wo,@Param("Lot")String Lot);
	//101插入数据
	int SendSmtplugin101(SendRecDataVo SendRecData);
	//查询批次是否在库存中
	String InventoryState(String Lot);
	//存入313过账表
	int SendSmtplugin313(SendRecDataVo SendRecData);
	//更新批次库存状态
	int InventoryStatus(@Param("Lot")String Lot,@Param("model")String model);
	//查询收发料信息
	SendRecDataVo SelFactory(@Param("Lot")String Lot,@Param("model")String model);
	//先进先出
	SendRecDataVo PcbaFIFO(@Param("Lot")String Lot,@Param("Plant")String Plant);
	//查找下一按顺序应该发料的Lot信息
	SendRecDataVo findNextLot(@Param("Lot")String Lot,@Param("Plant")String Plant);
	//更改库存表收料状态
	int UpStatus(String Lot);
	//获取SN明细
	List<Map<String,Object>>QRYSmtDataMap(String Lot);
	
	//COB 收料
	SendRecDataVo RxCobData(@Param("Lot")String Lot,@Param("Type")String Type);
	SendRecDataVo Off_RxCobData(@Param("Lot")String Lot,@Param("Type")String Type);
	int RxCobplugin315(SendRecDataVo SendRecData);
	int RxSmtInsert315(SendRecDataVo SendRecData);
	int SendCobInsert(SendRecDataVo SendRecData);
	//COB 收料
	
	//Mi收料
	int RxCobInsert315(SendRecDataVo SendRecData);
	int SendMiInsert(SendRecDataVo SendRecData);
	int SendMiInsertSpecial(SendRecDataVo SendRecData);
	int PcbaStorageSpecial(SendRecDataVo SendRecData);
	//Mi收料
	
	//Casing收料
	int RxMiInsert315(SendRecDataVo SendRecData);
	int SendCasingInsert(SendRecDataVo SendRecData);
	//Casing收料
	
	//smt发料库存表
	int SendSmtInsert(SendRecDataVo SendRecData);
	
	//插入SN明细
	int InsertSN(@Param("Sn")String Sn,@Param("Lot")String Lot,@Param("LotQty")String LotQty,@Param("WO")String WO,@Param("WERKS")String WERKS,@Param("CreateUser")String CreateUser);
	
	List<Map<String,Object>> FuzzyPn(@Param("Pn")String Pn,@Param("plant")String plant,@Param("workcenter")String workcenter);
	
	TotalVo Total();
	
	String UidState(String Lot);
	
	SapClosingTime SapSuspended();
	
	

}
