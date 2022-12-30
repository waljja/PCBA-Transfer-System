package com.ht.controller;

import com.ht.api.CommonResult;
import com.ht.api.ResultCode;
import com.ht.entity.*;
import com.ht.mapper.TransactionLogMapper;
import com.ht.mapper.TransactionSummaryMapper;
import com.ht.service.MaintainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * PCB 移交系统日常维护
 *
 * @author 丁国钊
 * @date 2022-12-19
 */
@Controller
@Api("运维接口")
@RequestMapping(value = "maintain")
public class MaintainController {
    @Autowired
    MaintainService maintainService;
    @Autowired
    TransactionSummaryMapper transactionSummaryMapper;
    @Autowired
    TransactionLogMapper transactionLogMapper;

    /**
     * 根据 UID、过账类型 更新 过账表、summary表 的 ”来源“ 库位
     *
     * @param uid
     *        物料 ID
     * @param store
     *        仓库
     */
    @ResponseBody
    @ApiOperation(value = "更新来源库位")
    @GetMapping("/modifyFromStore/{uid}/{store}/{type}")
    public CommonResult<String> modifyFromStoreController(@PathVariable("uid") String uid,
                                                          @PathVariable("store") String store,
                                                          @PathVariable("type") String type) {
        List<Transaction> transactionList;
        List<TransactionGroup> transactionGroupList;
        List<String> transactionHistoryIds = new ArrayList<>();

        // 查 TransactionHistoryId 存入集合并更新 Transaction 表仓位
        transactionList = maintainService.getTransferInfo(uid, type);
        for (Transaction transaction : transactionList) {
            String transactionHistoryId = transaction.getTransactionHistoryId();
            // 更新 Transaction 表仓位
            maintainService.modifyTransFromStore(transactionHistoryId, store);
            transactionHistoryIds.add(transactionHistoryId);
            System.out.println("TransactionHistoryId: " + transactionHistoryId);
        }

        // 查（BatchID + ItemID） 并更新 summary 表
        transactionGroupList = maintainService.getGroupInfo(transactionHistoryIds);
        for (TransactionGroup transactionGroup : transactionGroupList) {
            String batchId = transactionGroup.getBatchId();
            Integer itemId = transactionGroup.getItemId();
            // 更新 summary 表仓位
            maintainService.modifySummaryFromStore(batchId, itemId, store);
            System.out.println("BatchId: " + batchId + "    ItemId: " + itemId);
        }

        return CommonResult.success(ResultCode.MODIFY_STORE_SUCCESS);
    }

    /**
     * 根据 UID、过账类型 更新 过账表、summary表 的 ”目标“ 库位
     *
     * @param uid
     *        物料 ID
     * @param store
     *        仓库
     */
    @ResponseBody
    @ApiOperation(value = "更新目标库位")
    @GetMapping("/modifyToStore/{uid}/{store}/{type}")
    public CommonResult<String> modifyToStoreController(@PathVariable("uid") String uid,
                                                      @PathVariable("store") String store,
                                                      @PathVariable("type") String type) {
        List<Transaction> transactionList;
        List<TransactionGroup> transactionGroupList;
        List<String> transactionHistoryIds = new ArrayList<>();

        // 查 TransactionHistoryId 存入集合并更新 Transaction 表仓位
        transactionList = maintainService.getTransferInfo(uid, type);
        for (Transaction transaction : transactionList) {
            String transactionHistoryId = transaction.getTransactionHistoryId();
            // 更新 Transaction 表仓位
            maintainService.modifyTransToStore(transactionHistoryId, store);
            transactionHistoryIds.add(transactionHistoryId);
            System.out.println("TransactionHistoryId: " + transactionHistoryId);
        }

        // 查（BatchID + ItemID） 并更新 summary 表
        transactionGroupList = maintainService.getGroupInfo(transactionHistoryIds);
        for (TransactionGroup transactionGroup : transactionGroupList) {
            String batchId = transactionGroup.getBatchId();
            Integer itemId = transactionGroup.getItemId();
            // 更新 summary 表仓位
            maintainService.modifySummaryToStore(batchId, itemId, store);
            System.out.println("BatchId: " + batchId + "    ItemId: " + itemId);
        }

        return CommonResult.success(ResultCode.MODIFY_STORE_SUCCESS);
    }

    /**
     * 重新过账
     *
     * @param uid
     *        物料 ID
     * @param type
     *        过账类型
     */
    @ApiOperation(value = "重新过账")
    @GetMapping("/transferAgain/{uid}/{type}")
    public void transferAgainController(@PathVariable("uid") String uid, @PathVariable("type") String type) {
        // 获取 batchId itemId
        List<TransactionGroup> transactionGroupList = maintainService.getGroupInfo(uid, type);

        // 更新 summary 表 TransactionResult 为 0 ，重新过账
        for (TransactionGroup transactionGroup : transactionGroupList) {
            TransactionSummary transactionSummary = new TransactionSummary();
            String batchId = transactionGroup.getBatchId();
            Integer itemId = transactionGroup.getItemId();
            // 设置 TransactionResult
            transactionSummary.setTransactionResult(0);
            TransactionSummaryExample transactionSummaryExample = new TransactionSummaryExample();
            transactionSummaryExample.createCriteria().andBatchIdEqualTo(batchId).andItemIdEqualTo(itemId);
            transactionSummaryMapper.updateByExampleSelective(transactionSummary, transactionSummaryExample);
            System.out.println("BatchId: " + batchId + "    ItemId: " + itemId);
        }
    }

    /**
     * 获取过账日志信息
     *
     * @param uid
     *        物料 ID
     * @param type
     *        过账类型
     * @return
     */
    @ResponseBody
    @ApiOperation(value = "获取过账日志")
    @GetMapping("/getTransferLog/{uid}/{type}")
    public CommonResult<List<TransactionLog>> getTransferLog(@PathVariable("uid") String uid, @PathVariable("type") String type) {
        List<TransactionGroup> transactionGroupList;
        List<TransactionLog> transactionLogList =  new ArrayList<>();

        // 获取 batchId itemId
        transactionGroupList = maintainService.getGroupInfo(uid, type);

        // 获取过账日志
        for (TransactionGroup transactionGroup : transactionGroupList) {
            String batchId = transactionGroup.getBatchId();
            Integer itemId = transactionGroup.getItemId();
            transactionLogList.addAll(maintainService.getTransferLogInfo(batchId.toLowerCase(Locale.ROOT), itemId));
            System.out.println("BatchId: " + batchId + "    ItemId: " + itemId);
        }

        return CommonResult.success(transactionLogList);
    }

    /**
     * 删除过账日志
     *
     * @param uid
     *        物料 ID
     * @param type
     *        过账类型
     */
    @ApiOperation(value = "删除过账日志")
    @GetMapping("/deleteTransferLog/{uid}/{type}")
    public void deleteTransferLog(@PathVariable("uid") String uid, @PathVariable("type") String type) {
        List<TransactionGroup> transactionGroupList;

        // 获取 batchId itemId
        transactionGroupList = maintainService.getGroupInfo(uid, type);

        // 删除过账日志
        for (TransactionGroup transactionGroup : transactionGroupList) {
            String batchId = transactionGroup.getBatchId();
            Integer itemId = transactionGroup.getItemId();
            TransactionLogExample transactionLogExample = new TransactionLogExample();
            transactionLogExample.createCriteria().andBatchIdEqualTo(batchId).andItemIdEqualTo(itemId);
            transactionLogMapper.deleteByExample(transactionLogExample);
            System.out.println("BatchId: " + batchId + "    ItemId: " + itemId);
        }
    }
}
