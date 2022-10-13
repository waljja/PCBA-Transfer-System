package com.ht.controller;

import com.ht.api.CommonResult;
import com.ht.api.ObSendRecType;
import com.ht.api.ResultCode;
import com.ht.constants.Constants;
import com.ht.entity.InventoryTakeDownEntity;
import com.ht.entity.PCBAInventoryEntity1;
import com.ht.entity.PCBAInventoryExample1;
import com.ht.mapper.InventoryTakeDownMapper;
import com.ht.mapper.PCBAInventoryMapper1;
import com.ht.service.PcbaInventoryService;
import com.ht.util.*;
import com.ht.vo.SapClosingTime;
import com.ht.vo.SendRecDataVo;
import com.ht.vo.TotalVo;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Api("JavaAPI接口")
@CrossOrigin
// 跨域调用
@RestController
@RequestMapping(value = "/api")
public class PcbaInventoryConntroller {

	@Autowired
	public PcbaInventoryService PcbaService;
	@Autowired
	public PCBAInventoryMapper1 inventoryMapper;
	@Autowired
	public InventoryTakeDownMapper inventoryTakeDownMapper;
	
	/**
	 * 外挂系统重新过账
	 * 
	 * @param <T>
	 * 
	 * @return
	 */
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "Lot号重新过账", notes = "")
	@RequestMapping(value = "/retryPosting", method = RequestMethod.GET)
	public void retryPosting(
			@RequestParam(name = "Lot", required = true, defaultValue = "") @ApiParam("Lot") String Lot,
			@RequestParam(name = "Type", required = true, defaultValue = "") @ApiParam("过账类型") String Type) {
		String BatchId = "", ItemId = "";
		List<Map<String, String>> list = PcbaService.GetBatchId(Lot, Type);

		for (Map<String, String> map : list) {
			BatchId = map.get("BatchId");
			ItemId = String.valueOf(map.get("ItemId"));
		}

		System.out.println(!BatchId.equals(""));

		if(!BatchId.equals("") && !ItemId.equals("")){
			System.out.println("update");
			PcbaService.retryPosting(BatchId, ItemId);
		}
	}

	/**
	 * 下载看板数据
	 * 
	 * @param <T>
	 * 
	 * @return
	 */
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "获取库存报表", notes = "")
	@RequestMapping(value = "/downloadData", method = RequestMethod.GET)
	public CommonResult<String> downloadData(
			HttpServletResponse response,
			@RequestParam(name = "StartTime", required = true, defaultValue = "") @ApiParam("开始时间") String StartTime,
			@RequestParam(name = "EndTime", required = true, defaultValue = "") @ApiParam("结束时间") String EndTime) {
		String information;

		try {
			information = PcbaService.downloadData(response, StartTime, EndTime);

			if (information.equals("success")) {
				System.out.println("下载成功！");
			} else {
				System.out.println("下载失败！");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取库存报表
	 * 
	 * @param Pn
	 * @return
	 */
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "获取库存报表", notes = "")
	@RequestMapping(value = "/fuzzypn", method = RequestMethod.GET)
	public CommonResult<List<Map<String, Object>>> FuzzyPn(
			@RequestParam(name = "Pn", required = true, defaultValue = "") @ApiParam("型号") String Pn,
			@RequestParam(name = "node", required = true, defaultValue = "") @ApiParam("节点") String node,
			@RequestParam(name = "factory", required = true, defaultValue = "") @ApiParam("工厂") String factory) {
		ResultCode result = null;
		String state = (node.equals("smt") ? "1" : node.equals("cob") ? "2"
				: node.equals("mi") ? "3" : node.equals("casing") ? "4" : "0");
		List<Map<String, Object>> list = PcbaService
				.FuzzyPn(Pn, factory, state);

		if (list.size() != 0) {
			return CommonResult.success(list);
		} else {
			return CommonResult.failed("没有查询到这个型号");
		}
	}

	/**
	 * 翻页查询数据
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "获取库存报表", notes = "")
	@RequestMapping(value = "/newdata", method = RequestMethod.GET)
	public CommonResult<List<Map<String, Object>>> PCBAInventoryData(
			@RequestParam(name = "pageIndex", required = true, defaultValue = "") @ApiParam("当前页") int pageIndex,
			@RequestParam(name = "pageSize", required = true, defaultValue = "") @ApiParam("每页条数") int pageSize,
			@RequestParam(name = "plant", required = true, defaultValue = "") @ApiParam("工厂") String plant,
			@RequestParam(name = "workcenter", required = true, defaultValue = "") @ApiParam("工作中心") String workcenter,
			@RequestParam(name = "wo", required = true, defaultValue = "") @ApiParam("工单") String wo,
			@RequestParam(name = "partnumber", required = true, defaultValue = "") @ApiParam("PN") String partnumber) {
		String[] symbol = { "//", "[", "]", "\"" };
		List<String> plant1;
		List<String> workcenter1;
		List<String> wo1;
		List<String> partnumber1;
		plant = getSubString(plant, symbol);
		workcenter = getSubString(workcenter, symbol);
		wo = getSubString(wo, symbol);
		partnumber = getSubString(partnumber, symbol);

		if (plant.equals("null") || plant.equals("")) {
			plant1 = Arrays.asList();
		} else {
			plant1 = Arrays.asList(plant.split(","));
		}

		if (workcenter.equals("null") || workcenter.equals("")) {
			workcenter1 = Arrays.asList();
		} else {
			workcenter1 = Arrays.asList(workcenter.split(","));
		}

		if (wo.equals("null") || wo.equals("")) {
			wo1 = Arrays.asList();
		} else {
			wo1 = Arrays.asList(wo.split(","));
		}

		if (partnumber.equals("null") || partnumber.equals("")) {
			partnumber1 = Arrays.asList();
		} else {
			partnumber1 = Arrays.asList(partnumber.split(","));
		}

		TotalVo totalVo = PcbaService.Total();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		pageIndex = pageIndex == 0 ? 1 : pageIndex;
		List<Map<String, Object>> data = PcbaService.PCBAInventoryData(pageIndex,
				pageSize, plant1, workcenter1, wo1, partnumber1);

		for (Map<String, Object> map : data) {
			String nowMs = format.format(map.get("createtime"));
			map.put("createtime", nowMs);
		}

		return CommonResult.success(totalVo.getTotal(), totalVo.getSmttotal(),
				totalVo.getCobtotal(), totalVo.getMitotal(), data);
	}

	/**
	 * 查询SAP库位信息
	 * 
	 * @param Pn
	 *            物料号
	 * @param Factory
	 *            工厂
	 * @return
	 */
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "查询发出库位", notes = "根据PN在SAP查询发出库位")
	@RequestMapping(value = "/saploc", method = RequestMethod.GET)
	public CommonResult<SendRecDataVo> getSAPPN(
			@RequestParam(name = "user", required = true, defaultValue = "") @ApiParam("用户") String user,
			@RequestParam(name = "Lot", required = true, defaultValue = "") @ApiParam("交接单") String Lot,
			@RequestParam(name = "node", required = true, defaultValue = "") @ApiParam("节点") String node,
			@RequestParam(name = "ProductModel", required = true, defaultValue = "") @ApiParam("型号") String ProductModel,
			@RequestParam(name = "factory", required = true, defaultValue = "") @ApiParam("工厂") String factory) {
		SAPServiceUtil sapUtil = new SAPServiceUtil();
		ResultCode result = null;
		SendRecDataVo sap = null;
		SendRecDataVo earliestWOInfo;
		SendRecDataVo latestBatchInfo;

		if (node.equals("REC")) { // 收料
			sap = PcbaService.SelFactory(Lot, "0");

			try {
				if (checkObjFieldIsNotNull(sap)) {
					sap.setRecLocation(sapUtil.getSAPPN(sap.getPn(), sap.getFactory()));
					sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
				} else {
					return CommonResult.failed("没有查询到该Lot号收料数据！");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return CommonResult.success(sap);
		} else { // 发料
			if(Constants.isNames(user)){ // 特殊员工跳顺序发板权限
				if(!Lot.equals("")){ // Lot不为空则查询Lot数据
					sap = null;
					sap = PcbaService.SelFactory(Lot, "1");

					try {
						if (checkObjFieldIsNotNull(sap)) {
							sap.setRecLocation(sapUtil.getSAPPN(sap.getPn(),
									sap.getFactory()));
							sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
						} else {
							return CommonResult.failed("没有查询到该Lot号发料数据2！");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return CommonResult.success(sap);
			}else { // 普通员工FIFO管控
				if (!ProductModel.equals("") || !ProductModel.equals(null)) {
					sap = null;
					sap = PcbaService.findNextLot(ProductModel, factory); // 根据绑库时间+批次号进行管控，查出应发Lot数据
				}

				if (checkObjFieldIsNotNull(sap)) {
					if (!sap.getUID().equals(Lot)) { // 不符合FIFO规则或者是选中型号后Lot为空
						if (!Lot.equals("") && PcbaService.UidState(Lot).equals("0")) { // Lot已扫描
							return CommonResult.failed("此Lot号已扫描，现在应发:"
									+ sap.getUID()
									+ ",该Lot在:"
									+ sap.getLocation()
									+ "位置！");
						} else { // Lot号为空或者Lot号不为空且未扫描
							return CommonResult.failed(sap.getPn()
									+ "型号应该先发:"
									+ sap.getUID()
									+ ",该批次在:"
									+ sap.getLocation()
									+ "位置！");
						}
					} else { // 符合FIFO规则
						sap = null;
						sap = PcbaService.SelFactory(Lot, "1"); // 查工单信息

						try {
							if (checkObjFieldIsNotNull(sap)) {
								sap.setRecLocation(sapUtil.getSAPPN(sap.getPn(), sap.getFactory()));
								sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
							} else {
								return CommonResult.failed("没有查询到该Lot号发料数据！");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					return CommonResult.success(sap);
				} else { // 此型号不存在或者没有待发的料
					sap = null;
					sap = PcbaService.SelFactory(Lot, "1");

					try {
						if (checkObjFieldIsNotNull(sap)) {
							sap.setRecLocation(sapUtil.getSAPPN(sap.getPn(), sap.getFactory()));
							sap.setBatch(sap.getBatch().replaceAll("^0*", ""));
						} else {
							return CommonResult.failed("没有查询到该Lot号发料数据2！");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return CommonResult.success(sap);
			}
		}
	}

	/**
	 * 插入库存表信息
	 * 
	 * @param Lot
	 * @param Location
	 * @param UserName
	 * @param node
	 * @param Model
	 * @return
	 */
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "将Pcba板信息插入库存表", notes = "根据Lot号查询信息插入")
	@RequestMapping(value = "/insertpcba", method = RequestMethod.GET)
	public CommonResult<String> PcbaStorage(
			@RequestParam(name = "Lot", required = true, defaultValue = "") @ApiParam("批号") String Lot,
			@RequestParam(name = "Location", required = true, defaultValue = "") @ApiParam("位置id") String Location,
			@RequestParam(name = "UserName", required = true, defaultValue = "") @ApiParam("工号") String UserName,
			@RequestParam(name = "node", required = true, defaultValue = "") @ApiParam("节点") String node,
			@RequestParam(name = "Model", required = true, defaultValue = "") @ApiParam("模式") Boolean Model,
			@RequestParam(name = "factory", required = true, defaultValue = "") @ApiParam("工厂") String factory) {
		Model = false;
		ResultCode result = null;
		SendRecDataVo SendRecData = null;
		String lot1 = null;
		int state1 = 0;
		int state3 = 0;

		if (node.equals("smt")) { // 绑库
			try {
				String state = PcbaService.InventoryState(Lot);

				if (state == "" || state == null) { // 无State=1的（下架、已发）按fifo规则绑应该绑的Lot
					if (Constants.isAccount(UserName)) { // 蒋晓琴、秦丹跳工单绑库权限
						return BindingLocation(Lot, Location, UserName, node, Model, factory);
					}else { // 普通用户
						String flag = Fifo101(Lot, Lot.substring(0, 12), "", node, factory); // fifo

						if (flag.equals("true")) { //
							return BindingLocation(Lot, Location, UserName, node, Model, factory);
						} else {
							return CommonResult.failed(flag);
						}
					}
				} else { // 有State=1的重新绑库
					return BindingLocation(Lot, Location, UserName, node, Model, factory);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (node.equals("cob")) { // COB无绑库，直接发料
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended();

				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) { // sap在线期间
					SendRecData = PcbaService.RxCobData(Lot, "313"); // 查询过账信息,是否做过313
				} else { // 离线
					SendRecData = PcbaService.Off_RxCobData(Lot, "313");
				}
			} else {
				SendRecData = PcbaService.Off_RxCobData(Lot, "313");
			}

			if (checkObjFieldIsNotNull(SendRecData)) { // 已经执行过313过账
				if (SendRecData.getRecLocation().equals("BS80")) { // COB只发BS80仓位
					SendRecData.setBatch(
							SendRecData
									.getUID()
									.subSequence(13, SendRecData.getUID().length())
									.toString());
					SendRecData.setUser(UserName);
					state1 = PcbaService.RxCobplugin315(SendRecData); // 插入过账表等待315过账（未做315）
					state3 = PcbaService.RxSmtInsert315(SendRecData); // 设置State=2，转移状态，更新接收仓位、接收人等信息（State=1，SMT表）

					if (state1 > 0 && state3 > 0) { // 插入过账表，更新状态，接收仓位操作成功
						PcbaService.UpStatus(Lot); // 设置接收状态为“已接收”
						InsertOb(Lot,UserName,ObSendRecType.COBREC.getTypeName(),false); // Orbit X发料

						return CommonResult.success("Cob收料成功");
					} else {
						return CommonResult.failed("该Lot号已经收料(COB)！");
					}
				} else {
					return CommonResult.failed("COB账号只能收发往COB的型号");
				}
			} else {
				return CommonResult.failed("该Lot号313发板SAP过账不成功，请在外挂系统检查原因！");
			}
		} else if (node.equals("mi")) {
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended();

				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) {
					SendRecData = PcbaService.RxCobData(Lot, "313");
				} else {
					SendRecData = PcbaService.Off_RxCobData(Lot, "313");
				}
			} else {
				SendRecData = PcbaService.Off_RxCobData(Lot, "313");
			}

			if (checkObjFieldIsNotNull(SendRecData)) {
				if (SendRecData.getRecLocation().equals("BS82")
						|| SendRecData.getRecLocation().equals("BS8G")
						|| SendRecData.getRecLocation().equals("BS5E")) { // MI接收仓位为BS82、BS8G、BS5E
					SendRecData.setBatch(SendRecData.getUID()
							.subSequence(13, SendRecData.getUID().length())
							.toString());
					SendRecData.setUser(UserName);

					if (SendRecData.getSendLocation().equals("BS81")
							|| SendRecData.getSendLocation().equals("BS87")
							|| SendRecData.getSendLocation().equals("BS51")) {
						state3 = PcbaService.RxSmtInsert315(SendRecData);
					} else if (SendRecData.getSendLocation().equals("BS80")) {
						state3 = PcbaService.RxCobInsert315(SendRecData);
					}

					state1 = PcbaService.RxCobplugin315(SendRecData);

					if (state1 > 0 && state3 > 0) {
						PcbaService.UpStatus(Lot);
						InsertOb(Lot, UserName, ObSendRecType.MIREC.getTypeName(), false);

						return CommonResult.success("Mi收料成功");
					} else {
						return CommonResult.failed("该Lot号已经收料(Mi)！");
					}
				} else {
					return CommonResult.failed("MI账号只能收发往MI的型号");
				}
			} else {
				return CommonResult.failed("该Lot号313发板SAP过账不成功，请在外挂系统检查原因！");
			}
		} else if (node.equals("casing")) {
			/*
			  *获取313过账信息
			*/
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended(); //sap运行时间

				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) { //sap在线期间
					SendRecData = PcbaService.RxCobData(Lot, "313"); // 313过账信息
				} else { //离线
					SendRecData = PcbaService.Off_RxCobData(Lot, "313");
				}
			} else { //离线
				SendRecData = PcbaService.Off_RxCobData(Lot, "313");
			}

			if (checkObjFieldIsNotNull(SendRecData)) {
				if (SendRecData.getRecLocation().equals("BS83")
						|| SendRecData.getRecLocation().equals("BS8E")
						|| SendRecData.getRecLocation().equals("BS8A")
						|| SendRecData.getRecLocation().equals("BS8G")
						|| SendRecData.getRecLocation().equals("BS5C")
						|| SendRecData.getRecLocation().equals("BS5D")
						|| SendRecData.getRecLocation().equals("BS5E")
						|| SendRecData.getRecLocation().equals("BS53")) {
					SendRecData.setBatch(SendRecData.getUID()
							.subSequence(13, SendRecData.getUID().length())
							.toString());
					SendRecData.setUser(UserName);

					if (SendRecData.getSendLocation().equals("BS81")
							|| SendRecData.getSendLocation().equals("BS87")
							|| SendRecData.getSendLocation().equals("BS51")) {
						state3 = PcbaService.RxSmtInsert315(SendRecData);
					} else if (SendRecData.getSendLocation().equals("BS80")) {
						state3 = PcbaService.RxCobInsert315(SendRecData);
					} else if (SendRecData.getSendLocation().equals("BS82")
							|| SendRecData.getSendLocation().equals("BS8G")) {
						state3 = PcbaService.RxMiInsert315(SendRecData);
						System.out.println(state3);
					}

					//state1 = PcbaService.RxCobplugin315(SendRecData); // 插入过账表进行315过账

					if ( state3 > 0) { // state1 > 0 &&
						PcbaService.UpStatus(Lot); // 设置过账信息为315已接收
						InsertOb(Lot,UserName,ObSendRecType.CASINGREC.getTypeName(),false);

						return CommonResult.success("Casing收料成功");
					} else {
						return CommonResult.failed("该Lot号已经收料(Casing)！");
					}
				} else {
					return CommonResult.failed("Casing账号只能收发往Casing的型号");
				}
			} else {
				return CommonResult.failed("该Lot号313发板SAP过账不成功，请在外挂系统检查原因！");
			}
		} else if (node.equals("MiCasing")) {
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended(); //获取sap运行时间

				/*
				  *查313过账信息
				*/
				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) { //sap在线期间
					SendRecData = PcbaService.RxCobData(Lot, "313");
				} else { //离线
					SendRecData = PcbaService.Off_RxCobData(Lot, "313");
				}
			} else { //离线
				SendRecData = PcbaService.Off_RxCobData(Lot, "313");
			}

			if (checkObjFieldIsNotNull(SendRecData)) {
				if (SendRecData.getRecLocation().equals("BS82")
						|| SendRecData.getRecLocation().equals("BS8G")) {
					SendRecData.setBatch(SendRecData.getUID()
							.subSequence(13, SendRecData.getUID().length())
							.toString());
					SendRecData.setUser(UserName);

					/*
					  * 插入315过账信息，进行过账
					*/
					if (SendRecData.getSendLocation().equals("BS81")
							|| SendRecData.getSendLocation().equals("BS87")) {
						state3 = PcbaService.RxSmtInsert315(SendRecData);
					} else if (SendRecData.getSendLocation().equals("BS80")) {
						state3 = PcbaService.RxCobInsert315(SendRecData);
					}

					state1 = PcbaService.RxCobplugin315(SendRecData);

					if (state1 > 0 && state3 > 0) { //插入成功
						PcbaService.UpStatus(Lot); //更新315接收信息

						return CommonResult.success("Mi收料成功");
					} else { //已经插入过
						return CommonResult.failed("该Lot号已经收料(Mi)！");
					}
				} else {
					return CommonResult.failed("MI账号只能收发往MI的型号");
				}
			} else {
				return CommonResult.failed("没有查询到该Lot号收料数据(Mi)！");
			}
		}

		return CommonResult.failed("未收料成功！");
	}

	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "将Pcba板发送到下一工序", notes = "根据Lot号查询信息发送")
	@RequestMapping(value = "/sendPcba", method = RequestMethod.GET)
	public CommonResult<String> sendPcba(
			@RequestParam(name = "Lot", required = true, defaultValue = "") @ApiParam("批号") String Lot,
			@RequestParam(name = "user", required = true, defaultValue = "") @ApiParam("用户") String User,
			@RequestParam(name = "node", required = true, defaultValue = "") @ApiParam("节点") String node,
			@RequestParam(name = "factory", required = true, defaultValue = "") @ApiParam("工厂") String factory,
			@RequestParam(name = "Model", required = true, defaultValue = "") @ApiParam("模式") Boolean Model)
			throws Exception {
		ResultCode result = null;
		SendRecDataVo SendRecData = null;
		SAPServiceUtil sapUtil = new SAPServiceUtil();
		int State1 = 0;
		int state2 = 0;
		int state3 = 0;

		if (node.equals("smt")) {
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended(); //查询sap运行开始、结束时间

				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) { //判断是否在SAP运行期间
					SendRecData = PcbaService.RxCobData(Lot, "101"); //SAP在线模式
				} else {
					SendRecData = PcbaService.Off_RxCobData(Lot, "101"); //离线模式
				}
			} else {
				SendRecData = PcbaService.Off_RxCobData(Lot, "101");
			}

			if (checkObjFieldIsNotNull(SendRecData)) {
				State1 = PcbaService.InventoryStatus(Lot, "0");

				if (State1 > 0) {
					SendRecData = PcbaService.BatchData(Lot.substring(0, 12),Lot); // 库存表State更新为0（已发）

					if (checkObjFieldIsNotNull(SendRecData)) {
						SendRecData.setUser(User);
						SendRecData.setRecLocation(
								sapUtil.getSAPPN(
										SendRecData.getPn(),
										SendRecData.getFactory()));
						state2 = PcbaService.SendSmtInsert(SendRecData); // 插入smt发料信息到smt表
						state3 = PcbaService.SendSmtplugin313(SendRecData); // 313过账

						if (state2 > 0 && state3 > 0) {
							InsertOb(Lot, User, ObSendRecType.SMTSEND.getTypeName(), true);

							return CommonResult.success("SMT发板成功!");
						} else {
							return CommonResult.failed("该Lot号已发过板(Smt)！");
						}
					} else {
						return CommonResult.failed("没有查询到该Lot号发料数据(Smt)1！");
					}
				} else {
					return CommonResult.failed("没有查询到该Lot号发料数据(Smt)2！");
				}
			} else {
				return CommonResult.failed("该Lot号101入库SAP过账不成功，请在外挂系统检查原因！");
			}
		} else if (node.equals("cob")) {
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended();

				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) {
					SendRecData = PcbaService.RxCobData(Lot, "101");
				} else {
					SendRecData = PcbaService.Off_RxCobData(Lot, "101");
				}
			} else {
				SendRecData = PcbaService.Off_RxCobData(Lot, "101");
			}
			if (checkObjFieldIsNotNull(SendRecData)) {
				int state = PcbaService.InventoryStatus(Lot, "0");

				if (state > 0) {
					SendRecData = PcbaService.BatchData(Lot.substring(0, 12), Lot);

					if (checkObjFieldIsNotNull(SendRecData)) {
						SendRecData.setUser(User);
						SendRecData.setRecLocation(sapUtil.getSAPPN(
								SendRecData.getPn(), SendRecData.getFactory()));
						State1 = PcbaService.SendCobInsert(SendRecData);
						state2 = PcbaService.SendSmtplugin313(SendRecData);

						if (State1 > 0 && state2 > 0) {
							InsertOb(Lot, User, ObSendRecType.COBSEND.getTypeName(), true);

							return CommonResult.success("COB发料成功");
						} else {
							return CommonResult.failed("该Lot号已发过板(Cob)！");
						}
					} else {
						return CommonResult.failed("没有查询到该Lot号发料数据(Cob)1！");
					}
				} else {
					return CommonResult.failed("没有查询到该Lot号发料数据(Cob)2！");
				}
			} else {
				return CommonResult.failed("该Lot号101入库SAP过账不成功，请在外挂系统检查原因！");
			}
		} else if (node.equals("mi")) {
			if (Model) {
				SapClosingTime sapTime = PcbaService.SapSuspended();

				if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) {
					SendRecData = PcbaService.RxCobData(Lot, "101");
				} else {
					SendRecData = PcbaService.Off_RxCobData(Lot, "101");
				}
			} else {
				SendRecData = PcbaService.Off_RxCobData(Lot, "101");
			}
			if (checkObjFieldIsNotNull(SendRecData)) {
				int state = PcbaService.InventoryStatus(Lot, "0");

				if (state > 0) {
					SendRecData = PcbaService.BatchData(Lot.substring(0, 12), Lot);

					if (checkObjFieldIsNotNull(SendRecData)) {
						SendRecData.setUser(User);
						SendRecData.setRecLocation(sapUtil.getSAPPN(
								SendRecData.getPn(), SendRecData.getFactory()));
						State1 = PcbaService.SendMiInsert(SendRecData);
						state2 = PcbaService.SendSmtplugin313(SendRecData);

						if (State1 > 0 && state2 > 0) {
							InsertOb(Lot,User,ObSendRecType.MISEND.getTypeName(),true);

							return CommonResult.success("MI发料成功");
						} else {
							return CommonResult.failed("该Lot号已发过板(Mi)！");
						}
					} else {
						return CommonResult.failed("没有查询到该Lot号发料数据(Mi)1！");
					}
				} else {
					return CommonResult.failed("没有查询到该Lot号发料数据(Mi)2！");
				}
			} else {
				return CommonResult.failed("该Lot号101入库SAP过账不成功，请在外挂系统检查原因！");
			}
		} else if (node.equals("casing")) {
			int state = PcbaService.InventoryStatus(Lot, "0");

			if (state > 0) {
				SendRecData = PcbaService.BatchData(Lot.substring(0, 12), Lot);

				if (checkObjFieldIsNotNull(SendRecData)) {
					SendRecData.setUser(User);
					SendRecData.setRecLocation(sapUtil.getSAPPN(
							SendRecData.getPn(), SendRecData.getFactory()));
					State1 = PcbaService.SendCasingInsert(SendRecData);
					state2 = PcbaService.SendSmtplugin313(SendRecData);

					if (State1 > 0 && state2 > 0) {
						return CommonResult.success("Casing发料成功");
					} else {
						return CommonResult.failed("该Lot号已发过板(Casing)！");
					}
				} else {
					return CommonResult.failed("没有查询到该Lot号发料数据(Casing)1！");
				}
			} else {
				return CommonResult.failed("没有查询到该Lot号发料数据(Casing)2！");
			}
		}

		return CommonResult.failed("未发板成功！");
	}

	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "101入库", notes = "根据Lot号进行101入库")
	@RequestMapping(value = "/Pcba101", method = RequestMethod.GET)
	public CommonResult<String> Pcba101(
			@RequestParam(name = "Lot", required = true, defaultValue = "") @ApiParam("批号") String Lot,
			@RequestParam(name = "Location", required = true, defaultValue = "") @ApiParam("库位") String Location,
			@RequestParam(name = "user", required = true, defaultValue = "") @ApiParam("用户") String User,
			@RequestParam(name = "node", required = true, defaultValue = "") @ApiParam("节点") String node,
			@RequestParam(name = "factory", required = true, defaultValue = "") @ApiParam("工厂") String factory)
			throws Exception {
		Con72DB con72db = new Con72DB();
		Con75DB con75db = new Con75DB();
		Con51DB con51db = new Con51DB();
		int state1 = 0;
		int state2 = 0;
		int state3 = 0;
		String Pn = "";
		
		System.out.println("Lot:"+Lot+","+"factory:"+factory+","+"user:"+User+","+"node:"+node+","+"Location:"+Location+","+new Date());
		ResultSet rs = null;
		
		ResultCode result = null;
		SendRecDataVo SendRecData = null;
		List list = new ArrayList<>();
		if (node.equals("smt")) {
			if (factory.equals("B1")) {
				if(!Pattern.compile(".*[a-zA-Z]+.*").matcher(Lot).matches()) { //无条码工单
					SendRecData = PcbaService.QRYSmtDataNo(Lot);
				}else { //有条码工单报废批次数量扣减
					SendRecData = PcbaService.QRYSmtData(Lot);
				}

				if (checkObjFieldIsNotNull(SendRecData)) { //有数据
					if (!SendRecData.getPn().startsWith("620")) {
						return CommonResult.failed("请用对应账号做101入库");
					}
					SendRecData.setUser(User);

					if(SendRecData.getPn().indexOf("00DR1")!=-1 || SendRecData.getPn().indexOf("00DR3")!=-1) {
						if(Location.equals("BS51")){
							SendRecData.setSendLocation(Location);
						}else {
							return CommonResult.failed("5000工厂的型号只能入BS51仓");
						}
					}else {
						if(Location.equals("BS81")){
							SendRecData.setSendLocation(Location);
						}else {
							return CommonResult.failed("B1 1100工厂的型号只能入BS81仓");
						}
					}

					SendRecData.setFactory(SendRecData.getPn().indexOf("00DR1")!=-1 || SendRecData.getPn().indexOf("00DR3")!=-1 ? "5000" :"1100");
					System.out.println("aaa:"+SendRecData.getPlant());
					System.out.println("bbb:"+SendRecData.getFactory());
					state1 = PcbaService.SendSmtplugin101(SendRecData); // 101入库

					if (state1 > 0) {
						if (Pattern.compile("(?i)[a-z]").matcher(Lot).find()) {
							List<Map<String, Object>> data = PcbaService
									.QRYSmtDataMap(Lot);

							for (Map<String, Object> map : data) {
								PcbaService.InsertSN(map.get("SN").toString(),
										map.get("UID").toString(),
										map.get("Qty").toString(), map
												.get("Wo").toString(),
										map.get("Factory").toString(), User);
							}
						}

						return CommonResult.success("SMT101入库成功!");
					} else {
						return CommonResult.failed("该Lot号已做过101入库(Smt)！");
					}
				} else { //查不到数据，查OrBitx 72数据库
					rs = con72db.executeQuery(SqlApi.SelLotData(Lot));

					if (rs.next()) {
						if (!rs.getString("Pn").startsWith("620")) {
							return CommonResult.failed("请用对应账号做101入库");
						}

						SendRecData.setUID(rs.getString("Batch"));
						SendRecData.setQty(rs.getString("Qty"));
						SendRecData.setWo(rs.getString("Wo"));
						SendRecData.setWoQty(rs.getString("WoQty"));
						SendRecData.setPn(rs.getString("Pn"));
						SendRecData.setUser(User);
						SendRecData.setBatch(rs.getString("Batch")
								.subSequence(13, rs.getString("Batch").length())
								.toString());

						if(SendRecData.getPn().indexOf("00DR1")!=-1 || SendRecData.getPn().indexOf("00DR3")!=-1) {
							if(Location.equals("BS51")){
								SendRecData.setSendLocation(Location);
							}else {
								return CommonResult.failed("5000工厂的型号只能入BS51仓");
							}
						}else {
							if(Location.equals("BS81")){
								SendRecData.setSendLocation(Location);
							}else {
								return CommonResult.failed("B1 1100工厂的型号只能入BS81仓");
							}
						}

						SendRecData.setFactory(SendRecData.getPn().indexOf("00DR1")!=-1 || SendRecData.getPn().indexOf("00DR3")!=-1 ? "5000" :"1100");
						System.out.println("aaa:"+SendRecData.getPlant());
						System.out.println("bbb:"+SendRecData.getFactory());
						state1 = PcbaService.SendSmtplugin101(SendRecData); //101入库

						if (state1 > 0) {
							if (Pattern.compile("(?i)[a-z]").matcher(Lot).find()) {
								List<Map<String, Object>> data = PcbaService
										.QRYSmtDataMap(Lot);

								for (Map<String, Object> map : data) {
									PcbaService.InsertSN(map.get("SN").toString(),
											map.get("UID").toString(),
											map.get("Qty").toString(), map
													.get("Wo").toString(),
											map.get("Factory").toString(), User);
								}
							}

							return CommonResult.success("SMT101入库成功!");
						} else {
							return CommonResult.failed("该Lot号已做过101入库(Smt)！");
						}
					}else {
						return CommonResult.failed("没有查询到对应Lot号数据");
					}
				}
			} else { //B2
				SendRecDataVo data = new SendRecDataVo();
				try {
					rs = con51db.executeQuery(SqlApi.SelLotData(Lot));

					if (rs.next()) {
						if (!rs.getString("Pn").startsWith("620")) {
							return CommonResult.failed("请用对应账号做101入库");
						}

						data.setUID(rs.getString("Batch"));
						data.setQty(rs.getString("Qty"));
						data.setWo(rs.getString("Wo"));
						data.setWoQty(rs.getString("WoQty"));

						if (rs.getString("Pn").endsWith("00DR3") || rs.getString("Pn").indexOf("00DR1")!=-1) { //以00DR3结尾的只能入BS51仓
							if (Location.equals("BS51")) {
								data.setSendLocation((!Location.equals("")?Location:rs.getString("sendLocation").trim()));
							}else {
								return CommonResult.failed("5000工厂的型号只能入BS51仓");
							}
						}else if(rs.getString("Pn").endsWith("00R3")) {  //以00R3结尾的只能入BS87仓
							if (Location.equals("BS87")) {
								data.setSendLocation((!Location.equals("")?Location:rs.getString("sendLocation").trim()));
							}else {
								return CommonResult.failed("B2 1100工厂的型号只能入BS87仓");
							}
						}else {
							data.setSendLocation((!Location.equals("")?Location:rs.getString("sendLocation").trim()));
						}

						data.setRecLocation(rs.getString("RecLocation").trim());
						data.setPn(rs.getString("Pn"));
						data.setWorkcenter("1");
						data.setUser(User);
						data.setBatch(rs
								.getString("Batch")
								.subSequence(13, rs.getString("Batch").length())
								.toString());
						data.setFactory(data.getPn().indexOf("00DR1")!=-1 || data.getPn().indexOf("00DR3")!=-1 ? "5000" :"1100");
						state1 = PcbaService.SendSmtplugin101(data);

						if (state1 > 0) {
							rs = con51db.executeQuery(SqlApi.SelSmtSnData(Lot));
							int count = rs.getMetaData().getColumnCount();// 获取列数

							while (rs.next()) {
								Map map = new HashMap<>();

								for (int i = 1; i <= count; i++) {
									Object value = rs.getObject(i);
									Object key = rs.getMetaData()
											.getColumnName(i);
									map.put(key, value);
								}
								list.add(map);
							}

							for (int i = 0; i < list.size(); i++) {
								String b = list.get(i).toString();
								PcbaService.InsertSN(
										b.substring(29, b.length() - 1),
										b.substring(5, 24), data.getQty(),
										data.getWo(), data.getFactory(), User);
							}

							con51db.close();

							return CommonResult.success("Cob101入库成功");
						} else {
							con51db.close();

							return CommonResult.failed("该Lot号已做过101入库(Smt)！");
						}
					} else {
						return CommonResult.failed("没有查询到对应Lot号数据");
					}

				} catch (Exception e) {
					con51db.close();
					e.printStackTrace();
				}
			}
		} else if (node.equals("cob")) {
			SendRecDataVo data = new SendRecDataVo();

			try {
				if (factory.equals("B1")) {
					rs = con72db.executeQuery(SqlApi.SelLotData(Lot));
				} else {
					rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
				}

				if (rs.next()) {
					if (!rs.getString("Pn").startsWith("610")) {
						return CommonResult.failed("请用对应账号做101入库");
					}
					data.setUID(rs.getString("Batch"));
					data.setQty(rs.getString("Qty"));
					data.setWo(rs.getString("Wo"));
					data.setWoQty(rs.getString("WoQty"));
					data.setFactory(rs.getString("Factory"));
					data.setSendLocation((!Location.equals("")?Location:rs.getString("sendLocation").trim()));
					data.setRecLocation(rs.getString("RecLocation").trim());
					data.setPn(rs.getString("Pn"));
					data.setWorkcenter("2");
					data.setUser(User);
					data.setPlant(factory);
					data.setBatch(rs.getString("Batch")
							.subSequence(13, rs.getString("Batch").length())
							.toString());
					String flag = Fifo101(Lot, data.getWo(), "PCBA CNC分板下线",
							"", factory);

					if (flag.equals("true")) {
						state1 = PcbaService.SendSmtplugin101(data);
						if (state1 > 0) {
							state2 = PcbaService.PcbaStorage(data);

							if (state2 > 0) {
								rs = con72db.executeQuery(SqlApi
										.SelCobSnData(Lot));
								int count = rs.getMetaData().getColumnCount();// 获取列数

								while (rs.next()) {
									Map map = new HashMap<>();

									for (int i = 1; i <= count; i++) {
										Object value = rs.getObject(i);
										Object key = rs.getMetaData()
												.getColumnName(i);
										map.put(key, value);
									}

									list.add(map);
								}

								for (int i = 0; i < list.size(); i++) {
									String b = list.get(i).toString();
									PcbaService.InsertSN(
											b.substring(29, b.length() - 1),
											b.substring(5, 24), data.getQty(),
											data.getWo(), data.getFactory(),
											User);
								}

								con72db.close();

								return CommonResult.success("Cob101入库成功");
							}
						} else {
							con72db.close();
							con51db.close();

							return CommonResult.failed("该Lot号已做过101入库(Cob)！");
						}
					} else {
						return CommonResult.failed(flag);
					}
				} else {
					return CommonResult.failed("没有查询到对应Lot号数据");
				}
			} catch (Exception e) {
				con72db.close();
				con51db.close();
				e.printStackTrace();
			}
		} else if (node.equals("mi")) {
			SendRecDataVo data = new SendRecDataVo();

			try {
				if (factory.equals("B1")) {
					rs = con72db.executeQuery(SqlApi.SelLotData(Lot));

					if (!rs.isBeforeFirst()) {
						rs = con75db.executeQuery(SqlApi.SelLotData(Lot));
					}
				} else {
					rs = con51db.executeQuery(SqlApi.SelLotData(Lot));

					if (!rs.isBeforeFirst()) {
						rs = con75db.executeQuery(SqlApi.SelLotData(Lot));
					}
				}
				if (rs.next()) {
					if (!rs.getString("Pn").startsWith("64")) {
						return CommonResult.failed("请用对应账号做101入库");
					}

					data.setUID(rs.getString("Batch"));
					data.setQty(rs.getString("Qty"));
					data.setWo(rs.getString("Wo"));
					data.setWoQty(rs.getString("WoQty"));
					data.setFactory(rs.getString("Factory"));
					data.setSendLocation(Location);
					data.setRecLocation(rs.getString("RecLocation").trim());
					data.setPn(rs.getString("Pn"));
					data.setWorkcenter("3");
					data.setUser(User);
					data.setPlant(factory);
					data.setBatch(rs.getString("Batch")
							.subSequence(13, rs.getString("Batch").length())
							.toString());

					String flag = Fifo101(Lot, data.getWo(), "", "", factory);

					if (flag.equals("true")) {
						Pn = PcbaService.specialPn(data.getPn().substring(0, 8)); //查找特殊PN表是否有此型号

						if (Pn != null) { //特殊PN
							System.err.println("特殊PN："+Lot);
							// 做101入库(插入过账表)
							state1 =  PcbaService.SendSmtplugin101(data);

							if (state1 > 0) {
								state2 = PcbaService.SendMiInsertSpecial(data);
								state3 = PcbaService.PcbaStorageSpecial(data);

								if (state2 > 0 && state3 > 0) {
									if (factory.equals("B1")) {
										rs = con72db.executeQuery(SqlApi
												.SelMiSnData(Lot));

										if (!rs.isBeforeFirst()) {
											rs = con75db.executeQuery(SqlApi
													.SelMiSnData(Lot));
										}
									} else {
										rs = con51db.executeQuery(SqlApi
												.SelMiSnData(Lot));
									}

									int count = rs.getMetaData()
											.getColumnCount();// 获取列数

									while (rs.next()) {
										Map map = new HashMap<>();

										for (int i = 1; i <= count; i++) {
											Object value = rs.getObject(i);
											Object key = rs.getMetaData()
													.getColumnName(i);
											map.put(key, value);
										}

										list.add(map);
									}

									for (int i = 0; i < list.size(); i++) {
										String b = list.get(i).toString();
										PcbaService.InsertSN(
														b.substring(29, b.length() - 1),
														b.substring(5, 24),
														data.getQty(),
														data.getWo(),
														data.getFactory(), User);
									}

									con72db.close();
									con51db.close();
									con75db.close();

									return CommonResult.success("MI101入库成功");
								}
							} else {
								return CommonResult.failed("该Lot号已做过101入库(Mi)！");
							}
							
						} else {
							state1 = PcbaService.SendSmtplugin101(data);

							if (state1 > 0) {

								state2 = PcbaService.PcbaStorage(data);

								if (state2 > 0) {
									if (factory.equals("B1")) {
										rs = con72db.executeQuery(SqlApi
												.SelMiSnData(Lot));
									} else {
										rs = con51db.executeQuery(SqlApi
												.SelMiSnData(Lot));
									}

									int count = rs.getMetaData()
											.getColumnCount();// 获取列数

									while (rs.next()) {
										Map map = new HashMap<>();

										for (int i = 1; i <= count; i++) {
											Object value = rs.getObject(i);
											Object key = rs.getMetaData()
													.getColumnName(i);
											map.put(key, value);
										}

										list.add(map);
									}
									for (int i = 0; i < list.size(); i++) {
										String b = list.get(i).toString();
										PcbaService.InsertSN(
														b.substring(29, b.length() - 1),
														b.substring(5, 24),
														data.getQty(),
														data.getWo(),
														data.getFactory(), User);
									}

									con72db.close();
									con51db.close();

									return CommonResult.success("MI101入库成功");
								}
							} else {
								return CommonResult.failed("该Lot号已做过101入库(Mi)！");
							}
						}
					} else {
						return CommonResult.failed(flag);
					}
				} else {
					return CommonResult.failed("没有查询到对应Lot号数据");
				}

			} catch (Exception e) {
				con72db.close();
				con51db.close();
				e.printStackTrace();
			}
		} else if (node.equals("casing")) {
			SendRecDataVo data = new SendRecDataVo();
			try {
				if (factory.equals("B1")) {
					rs = con72db.executeQuery(SqlApi.SelLotData(Lot));
				} else {
					rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
				}
				if (rs.next()) {
					if (!rs.getString("Pn").startsWith("65")) {
						return CommonResult.failed("请用对应账号做101入库");
					}

					data.setUID(rs.getString("Batch"));
					data.setQty(rs.getString("Qty"));
					data.setWo(rs.getString("Wo"));
					data.setWoQty(rs.getString("WoQty"));
					data.setFactory(rs.getString("Factory"));
					data.setSendLocation(Location);
					data.setRecLocation(rs.getString("RecLocation").trim());
					data.setPn(rs.getString("Pn"));
					data.setWorkcenter("4");
					data.setUser(User);
					data.setPlant(factory);
					data.setBatch(rs.getString("Batch")
							.subSequence(13, rs.getString("Batch").length())
							.toString());

					state1 = PcbaService.SendSmtplugin101(data);
					if (state1 > 0) {
						state2 = PcbaService.PcbaStorage(data);

						if (state2 > 0) {
							if (factory.equals("B1")) {
								rs = con72db.executeQuery(SqlApi
										.SelCasingSnData(Lot));
							} else {
								rs = con51db.executeQuery(SqlApi
										.SelCasingSnData(Lot));
							}

							int count = rs.getMetaData().getColumnCount();// 获取列数

							while (rs.next()) {
								Map map = new HashMap<>();

								for (int i = 1; i <= count; i++) {
									Object value = rs.getObject(i);
									Object key = rs.getMetaData()
											.getColumnName(i);
									map.put(key, value);
								}
								list.add(map);
							}
							for (int i = 0; i < list.size(); i++) {
								String b = list.get(i).toString();
								PcbaService.InsertSN(
										b.substring(29, b.length() - 1),
										b.substring(5, 24), data.getQty(),
										data.getWo(), data.getFactory(), User);
							}

							con72db.close();
							con51db.close();

							return CommonResult.success("Casing101入库成功");
						}
					} else {
						return CommonResult.failed("该Lot号已做过101入库(Casing)！");
					}
				} else {
					return CommonResult.failed("没有查询到对应Lot号数据");
				}

			} catch (Exception e) {
				con72db.close();
				con51db.close();
				e.printStackTrace();
			}
		}
		return CommonResult.failed("未入库成功！");
	}

	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没有填好"),
			@ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确") })
	@ApiOperation(value = "获取Sn明细", notes = "根据Lot号获取SN明细")
	@RequestMapping(value = "/NewSn", method = RequestMethod.GET)
	public CommonResult<List<Map<String, Object>>> ObtainSnData(
			@RequestParam(name = "Lot", required = true, defaultValue = "") @ApiParam("批号") String Lot,
			@RequestParam(name = "node", required = true, defaultValue = "") @ApiParam("节点") String node)
			throws Exception {
		List<Map<String, Object>> data = null;
		Con72DB con72db = new Con72DB();
		if (node.equals("smt")) {
			data = PcbaService.QRYSmtDataMap(Lot);
			return CommonResult.success(data);
		} else {
			con72db.executeQuery("");
			data = null;

			return CommonResult.success(data);
		}
	}

	//绑库
	public CommonResult<String> BindingLocation(String Lot, String Location,
			String UserName, String node, Boolean Model, String factory)
			throws SQLException {
		Con51DB con51db = new Con51DB();
		ResultSet rs;
		ResultCode result = null;
		SendRecDataVo SendRecData = null;
		int state1 = 0;
		String state2 = null;
		int state3 = 0;
		if (Model) {
			SapClosingTime sapTime = PcbaService.SapSuspended();

			if (getDate(sapTime.getStartTime(), sapTime.getEndTime())) { // 判断是否在SAP运行期间
				SendRecData = PcbaService.RxCobData(Lot, "101"); // 查询SAP在线数据

				if (checkObjFieldIsNotNull(SendRecData)) {
					SendRecData = null;
				} else {
					result = ResultCode.TRANSFERWASUNSUCCESSFUL101;

					return CommonResult.failed(result);
				}
			} else {
				SendRecData = PcbaService.Off_RxCobData(Lot, "101"); // 离线数据

				if (checkObjFieldIsNotNull(SendRecData)) {
					SendRecData = null;
				} else {
					result = ResultCode.THEREISNOPOSTING101;

					return CommonResult.failed(result);
				}
			}
		} else { // 无关紧要代码
			SendRecData = PcbaService.Off_RxCobData(Lot, "101");

			if (checkObjFieldIsNotNull(SendRecData)) {
				SendRecData = null;
			} else {
				result = ResultCode.THEREISNOPOSTING101;

				return CommonResult.failed(result);
			}
		}

		state2 = PcbaService.InventoryState(Lot);

		if (factory.equals("B1")) {
			if (state2 == "" || state2 == null) { // 无state=1
				//绑库方法加上报废批次数量处理，确保库存表数据和101入库时相同
				if(!Pattern.compile(".*[a-zA-Z]+.*").matcher(Lot).matches()) { //无条码工单
					SendRecData = PcbaService.QRYSmtDataNo(Lot);
				}else { //有条码工单报废批次数量扣减
					SendRecData = PcbaService.QRYSmtData(Lot);
				}

				if (checkObjFieldIsNotNull(SendRecData)) {
					if (!SendRecData.getPn().startsWith("620")) {
						return CommonResult.failed("SMT账号只能绑SMT的型号");
					}

					SendRecData.setUser(UserName);
					SendRecData.setBatch(SendRecData.getBatch());

					if (SendRecData.getPn().endsWith("_SA")) {
						SendRecData.setPn(SendRecData
								.getPn()
								.subSequence(0, SendRecData.getPn().length() - 3)
								.toString());
					} else {
						SendRecData.setPn(SendRecData.getPn());
					}

					SendRecData.setLocation(Location);
					SendRecData.setSendLocation(Location);
					SendRecData.setRecLocation(Location);
					SendRecData.setPlant(factory);
					SendRecData.setWorkcenter("1");
					
					if (SendRecData.getPn().endsWith("00DR1")||SendRecData.getPn().endsWith("00DR3")) {
						SendRecData.setFactory("5000");
					}

					state1 = PcbaService.PcbaStorage(SendRecData); // 绑库会向inventory表插入一条新数据，State=1

					if (state1 > 0) {
						return CommonResult.success("SMT保存到库存表成功!");
					} else {
						return CommonResult.failed("绑库失败！");
					}
				} else {
					return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
				}
			} else { // 有state=1的
				state3 = PcbaService.InventoryStatus(Lot, "2");

				if (state3 > 0) {
					//绑库方法加上报废批次数量处理，确保库存表数据和101入库时相同
					if(!Pattern.compile(".*[a-zA-Z]+.*").matcher(Lot).matches()) { //无条码工单
						SendRecData = PcbaService.QRYSmtDataNo(Lot);
					}else { //有条码工单报废批次数量扣减
						SendRecData = PcbaService.QRYSmtData(Lot);
					}


					if (checkObjFieldIsNotNull(SendRecData)) {
						SendRecData.setUser(UserName);
						SendRecData.setBatch(SendRecData.getBatch());

						if (SendRecData.getPn().endsWith("_SA")) {
							SendRecData.setPn(SendRecData
									.getPn()
									.subSequence(0, SendRecData.getPn().length() - 3)
									.toString());
						} else {
							SendRecData.setPn(SendRecData.getPn());
						}

						SendRecData.setLocation(Location);
						SendRecData.setSendLocation(Location);
						SendRecData.setRecLocation(Location);
						SendRecData.setPlant(factory);
						SendRecData.setWorkcenter("1");

						if (SendRecData.getPn().endsWith("00DR1")||SendRecData.getPn().endsWith("00DR3")) {
							SendRecData.setFactory("5000");
						}

						state1 = PcbaService.PcbaStorage(SendRecData);

						if (state1 > 0) {
							return CommonResult.success("SMT保存到库存表成功!");
						} else {
							return CommonResult.failed("绑库失败！");
						}
					} else {
						return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
					}
				} else {
					return CommonResult.failed("重新绑库失败！");
				}
			}
		} else {
			rs = con51db.executeQuery(SqlApi.SelLotData(Lot));
			SendRecDataVo SendRecData1 = new SendRecDataVo();

			if (rs.next()) {
				if (!rs.getString("Pn").startsWith("620")) {
					return CommonResult.failed("SMT账号只能绑SMT的型号");
				}

				SendRecData1.setWo(rs.getString("Wo"));
				SendRecData1.setWoQty(rs.getString("WoQty"));
				SendRecData1.setPn(rs.getString("Pn"));
				SendRecData1.setQty(rs.getString("Qty"));
				SendRecData1.setBatch(rs.getString("Batch")
						.subSequence(14, rs.getString("Batch").length())
						.toString());
				SendRecData1.setUser(UserName);
				SendRecData1.setFactory(rs.getString("Factory"));
				SendRecData1.setWorkcenter("1");
				SendRecData1.setUID(rs.getString("Batch"));
				SendRecData1.setSendLocation(Location);
				SendRecData1.setPlant(factory);
			}
			if (state2 == "" || state2 == null) {
				if (checkObjFieldIsNotNull(SendRecData1)) {
					SendRecData1.setUser(UserName);
					state1 = PcbaService.PcbaStorage(SendRecData1);

					if (state1 > 0) {
						return CommonResult.success("SMT保存到库存表成功!");
					} else {
						return CommonResult.failed("绑库失败！");
					}
				} else {
					return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
				}
			} else {
				state3 = PcbaService.InventoryStatus(Lot, "2");

				if (state3 > 0) {
					if (checkObjFieldIsNotNull(SendRecData1)) {
						SendRecData1.setUser(UserName);
						state1 = PcbaService.PcbaStorage(SendRecData1);

						if (state1 > 0) {
							return CommonResult.success("SMT保存到库存表成功!");
						} else {
							return CommonResult.failed("绑库失败！");
						}
					} else {
						return CommonResult.failed("未查询到相关批次数据，请确认批次是否有错！");
					}
				} else {
					return CommonResult.failed("重新绑库失败！");
				}
			}
		}
	}
	
	//根据Lot号下架库存
    @ApiResponses({
            @ApiResponse(code = 400, message = "请求参数没有填好"),
            @ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确")})
    @ApiOperation(value = "库存下架", notes = "")
    @RequestMapping(value = "/inventoryTakeDown", method = RequestMethod.GET)
    public CommonResult<String> inventoryTakeDown(@RequestParam(name = "Lot", required = true, defaultValue = "")
                                                  @ApiParam("批次") String Lot,
                                                  @RequestParam(name = "userID", required = true, defaultValue = "")
                                                  @ApiParam("工号") String userID) {
        List<PCBAInventoryEntity1> inventories;

        //根据Lot查询库存信息
        PCBAInventoryExample1 inventoryExample = new PCBAInventoryExample1();
        inventoryExample.createCriteria().andUIDEqualTo(Lot);
        inventories = inventoryMapper.selectByExample(inventoryExample);

        if (!inventories.isEmpty()) {
            if (inventories.get(0).getState() == 1) { //显示在看板上的才能下架
                PCBAInventoryEntity1 inventory1 = new PCBAInventoryEntity1();
                inventory1.setState(3);

                PCBAInventoryExample1 inventoryExample1 = new PCBAInventoryExample1();
                inventoryExample1.createCriteria().andUIDEqualTo(Lot).andStateEqualTo(1);
                int isUpdated = inventoryMapper.updateByExampleSelective(inventory1, inventoryExample1);//下架库存

                if (isUpdated != 0){
                    //查询已下架的Lot（用于新增下架记录）
                    PCBAInventoryExample1 inventoryExample2 = new PCBAInventoryExample1();
                    inventoryExample2.createCriteria().andUIDEqualTo(Lot).andStateEqualTo(3);
                    inventories = inventoryMapper.selectByExample(inventoryExample2);

                    /*
                     *插入下架记录
                    */
                    PCBAInventoryEntity1 inventory = inventories.get(0);
                    InventoryTakeDownEntity takeDown = new InventoryTakeDownEntity();
                    BeanUtils.copyProperties(inventory,takeDown);//把查出来的入库记录直接映射到下架对象中
                    Date date = new Date();
                    date.setTime(System.currentTimeMillis());//获取当前系统时间
                    takeDown.setCreateUser(userID);//设置成当前下架操作人
                    takeDown.setCreateTime(date);//设置下架时间
                    int isInserted = inventoryTakeDownMapper.insertSelective(takeDown);

                    if (isInserted !=0) {
                        return CommonResult.success(ResultCode.TAKEDOWN_SUCCESS);
                    }else {
                        return CommonResult.failed("新增库存下架记录失败！");
                    }
                }else {
                    return CommonResult.failed("库存下架失败！");
                }
            }else {
                return CommonResult.failed("Lot不在库存中！无需下架");
            }
        }else {
            return CommonResult.failed("库存表不存在该Lot");
        }
    }

	public static boolean checkObjFieldIsNotNull(Object obj) { // true 不为空 false
		boolean flag = false;

		try {
			for (Field f : obj.getClass().getDeclaredFields()) {
				f.setAccessible(true);

				if (f.get(obj) == null || f.get(obj) == "") {
				} else {
					flag = true;
				}
			}
		} catch (Exception e) {
			return false;
		}

		return flag;
	}

	public static boolean getDate(Date date1, Date date2) { //判断是否在SAP运行时间内
		Calendar date = Calendar.getInstance();
		boolean flag = true;
		date.setTime(new Date()); //目前时间
		// 获取开始时间
		Calendar begin = Calendar.getInstance();
		begin.setTime(date1);
		// 获取结束时间
		Calendar end = Calendar.getInstance();
		end.setTime(date2); //
		if ((date.after(begin) && date.before(end)) || (date.getTime() == begin.getTime()
				|| date.getTime() == end.getTime())) {
			flag = false;
		}

		return flag;
	}

	public static String getSubString(String str1, String[] str2) {
		StringBuffer sb = new StringBuffer(str1);
		for (int i = 0; i < str2.length; i++) {
			while (true) {
				int index = sb.indexOf(str2[i]);

				if (index == -1) {
					break;
				}

				sb.delete(index, index + 1);
			}
		}

		return sb.toString();
	}

	public static String Fifo101(String Lot, String Wo, String Remark,
			String node, String factory) {
		String flag = "";
		Con72DB con72db = new Con72DB();
		Con75DB con75db = new Con75DB();
		Con51DB con51db = new Con51DB();
		Con100HR con100hr = new Con100HR();
		ResultSet rs1, rs2;

		try {
			if (factory.equals("B1")) {
				if (node.equals("smt")) {
					rs1 = con100hr.executeQuery(SqlApi.SmtFifo(Wo)); // 查询库存表中工单在库批次最大的Lot号（修改）

					if (rs1.next()) { // 已入库（Inventory表存在的数据）
						rs2 = con100hr.executeQuery(SqlApi.SmtObFifo(
								rs1.getString("UID"), Wo)); // Aegis设置的Lot顺序

						if (rs2.next()) {
							if (Lot.equals(rs2.getString("NewSN"))) { // 是下一个要绑定的
								flag = "true";
							} else {
								flag = "根据FIFO管控，请先绑定:"
										+ rs2.getString("NewSN") + "号";
							}
						} else {
							flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
						}
					} else { // 未入库
						rs1 = con100hr.executeQuery(SqlApi.SmtObFirst(Wo)); // 查询工单的第一个SN

						if (rs1.next()) {
							if (rs1.getString("NewSN").equals(Lot)) { // 根据传入的Lot判断是否是第一个SN
								flag = "true";
							} else {
								flag = "根据FIFO管控，请先绑定:"
										+ rs1.getString("NewSN") + "号";
							}
						} else {
							flag = "smt中没有查询到此Lot号!";
						}
					}
				} else {
					rs1 = con100hr.executeQuery(SqlApi.CobFifo(Wo));

					if (rs1.next()) {
						rs2 = con72db.executeQuery(SqlApi.CobObFifo(
								rs1.getString("UID"), Wo, Remark));

						if (!rs2.isBeforeFirst()) {
							rs2 = con75db.executeQuery(SqlApi.CobObFifo(
									rs1.getString("UID"), Wo, Remark));
						}

						if (rs2.next()) {
							if (Lot.equals(rs2.getString("FGSN"))) {
								flag = "true";
							} else {
								flag = "根据FIFO管控，请先入库:" + rs2.getString("FGSN")
										+ "号";
							}
						} else {
							flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
						}
					} else {
						rs1 = con72db.executeQuery(SqlApi
								.CobObFirst(Wo, Remark));

						if (!rs1.isBeforeFirst()) {
							rs1 = con75db.executeQuery(SqlApi
									.CobObFirst(Wo, Remark));
						}

						if (rs1.next()) {
							if (rs1.getString("FGSN").equals(Lot)) {
								flag = "true";
							} else {
								flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN");
								flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN")
										+ "号";
							}
						} else {
							flag = "Ob中没有查询到此Lot号!1";
						}
					}
				}
			} else {
				rs1 = con100hr.executeQuery(SqlApi.CobFifo(Wo));
				if (rs1.next()) { // 判断PCBA库存是否有数据
					rs2 = con51db.executeQuery(SqlApi.CobObFifo(
							rs1.getString("UID"), Wo, Remark));

					if (!rs2.isBeforeFirst()) { // 判断51OB数据是否能查到数据
						rs2 = con75db.executeQuery(SqlApi.CobObFifo(
								rs1.getString("UID"), Wo, Remark));
					}

					if (rs2.next()) {
						if (Lot.equals(rs2.getString("FGSN"))) {
							flag = "true";
						} else {
							flag = "根据FIFO管控，请先入库:" + rs2.getString("FGSN")
									+ "号";
						}
					} else {
						flag = "此Lot已扫描，占时没有查询到需要101入库的Lot号";
					}
				} else {
					rs1 = con51db.executeQuery(SqlApi.CobObFirst(Wo, Remark));

					if (!rs1.isBeforeFirst()) {
						rs1 = con75db.executeQuery(SqlApi.CobObFirst(Wo, Remark));
					}

					if (rs1.next()) {
						if (rs1.getString("FGSN").equals(Lot)) {
							flag = "true";
						} else {
							flag = "根据FIFO管控，请先入库:" + rs1.getString("FGSN")
									+ "号";
						}
					} else {
						flag = "Ob中没有查询到此Lot号!2";
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	public static void InsertOb(String Lot,String User,String ObType,Boolean state) {
		Con75DB con75db = new Con75DB();
		Con51DB con51db = new Con51DB();
		Con72DB con72db = new Con72DB();
		ResultSet rs = null;
		String Sql = state?SqlApi.UpObSend(Lot, User, ObType):SqlApi.UpObRec(Lot, User, ObType);//true：发料 ，false:收料
		try {
			rs = con72db.executeQuery(SqlApi.SelLotData(Lot));

			if (!rs.isBeforeFirst()) {//判断OB是否有数据
				rs = con51db.executeQuery(SqlApi.SelLotData(Lot));

					if (!rs.isBeforeFirst()) {//判断OB是否有数据
						rs = con75db.executeQuery(SqlApi.SelLotData(Lot));

							if (rs.isBeforeFirst()) {//判断OB是否有数据
								con75db.executeUpdate(Sql);
							}
					}else {
						con51db.executeUpdate(Sql);
					}
			}else {
				con72db.executeUpdate(Sql);
			}

			con51db.close();
			con72db.close();
			con75db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
