package com.ht.service;

import com.ht.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 丁国钊
 * @date 2022-12-19
 */
@Component
public interface MaintainService {
    /**
     * 根据 UID、过账类型 查过账信息
     *
     * @param uid
     * @param type
     * @return
     */
    public List<Transaction> getTransferInfo(String uid, String type);

    /**
     * 根据 transactionHistoryId 查（BatchID + ItemID）
     *
     * @param transactionHistoryIds
     * @return
     */
    public List<TransactionGroup> getGroupInfo(List<String> transactionHistoryIds);

    /**
     * 根据 transactionHistoryId 更新 Transaction 表仓位
     *
     * @param transactionHistoryId
     * @param store
     */
    public void modifyTransStore(String transactionHistoryId, String store);

    /**
     * 根据（BatchID + ItemID）更新 summary 表的仓位
     *
     * @param batchId
     * @param itemId
     * @param store
     */
    public void modifySummaryStore(String batchId, Integer itemId, String store);
}
