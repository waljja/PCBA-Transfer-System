package com.ht.controller;

import com.ht.api.CommonResult;
import com.ht.api.ResultCode;
import com.ht.entity.*;
import com.ht.mapper.TransactionGroupMapper;
import com.ht.mapper.TransactionMapper;
import com.ht.mapper.TransactionSummaryMapper;
import com.ht.service.MaintainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * PCB 移交系统日常维护
 *
 * @author 丁国钊
 * @date 2022-12-19
 */
@Controller
@RequestMapping(value = "maintain")
public class MaintainController {
    @Autowired
    TransactionSummaryMapper transactionSummaryMapper;
    @Autowired
    MaintainService maintainService;

    /**
     * 根据 UID、过账类型 更新 过账表、summary表 的库位
     *
     * @param uid
     *        物料 ID
     * @param store
     *        仓库
     */
    @GetMapping("/modifyStore/{uid}/{store}/{type}")
    public CommonResult<String> modifyStoreController(@PathVariable("uid") String uid,
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
            maintainService.modifyTransStore(transactionHistoryId, store);
            transactionHistoryIds.add(transactionHistoryId);
            System.out.println("TransactionHistoryId: " + transactionHistoryId);
        }

        // 查（BatchID + ItemID） 并更新 summary 表
        transactionGroupList = maintainService.getGroupInfo(transactionHistoryIds);
        for (TransactionGroup transactionGroup : transactionGroupList) {
            String batchId = transactionGroup.getBatchId();
            Integer itemId = transactionGroup.getItemId();
            // 更新 summary 表仓位
            maintainService.modifySummaryStore(batchId, itemId, store);
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
    @GetMapping("/transferAgain/{uid}/{type}")
    public void transferAgainController(@PathVariable("uid") String uid, @PathVariable("type") String type) {
        List<Transaction> transactionList;
        List<TransactionGroup> transactionGroupList;
        List<String> transactionHistoryIds = new ArrayList<>();

        // 根据 transactionHistoryIds 查 （BatchID + ItemID）
        transactionList = maintainService.getTransferInfo(uid, type);
        for (Transaction transaction : transactionList) {
            String transactionHistoryId = transaction.getTransactionHistoryId();
            transactionHistoryIds.add(transactionHistoryId);
            System.out.println("TransactionHistoryId: " + transactionHistoryId);
        }
        transactionGroupList = maintainService.getGroupInfo(transactionHistoryIds);

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
}
