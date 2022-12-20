package com.ht.service;

import com.ht.entity.Transaction;
import com.ht.entity.TransactionGroup;
import com.ht.entity.TransactionLog;

import java.util.List;

/**
 * @author 丁国钊
 * @date 2022-12-19
 */
public interface MaintainService {
    /**
     * 根据 UID、过账类型 查过账信息
     *
     * @param uid
     * @param type
     * @return
     */
    List<Transaction> getTransferInfo(String uid, String type);

    /**
     * 根据 transactionHistoryId 查（BatchID + ItemID）
     *
     * @param transactionHistoryIds
     * @return
     */
    List<TransactionGroup> getGroupInfo(List<String> transactionHistoryIds);

    /**
     * 根据 batchId,itemId 获取过账日志
     *
     * @param batchId
     * @param itemId
     * @return
     */
    List<TransactionLog> getTransferLogInfo(String batchId, Integer itemId);

    /**
     * 根据 uid,type 获取 TransactionGroupList
     *
     * @param uid
     * @param type
     * @return
     */
    List<TransactionGroup> getGroupInfo(String uid, String type);

    /**
     * 根据 transactionHistoryId 更新 Transaction 表仓位
     *
     * @param transactionHistoryId
     * @param store
     */
    void modifyTransStore(String transactionHistoryId, String store);

    /**
     * 根据（BatchID + ItemID）更新 summary 表的仓位
     *
     * @param batchId
     * @param itemId
     * @param store
     */
    void modifySummaryStore(String batchId, Integer itemId, String store);
}
