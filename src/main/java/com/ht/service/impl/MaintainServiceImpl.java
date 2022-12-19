package com.ht.service.impl;

import com.ht.entity.*;
import com.ht.mapper.TransactionGroupMapper;
import com.ht.mapper.TransactionMapper;
import com.ht.mapper.TransactionSummaryMapper;
import com.ht.service.MaintainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 丁国钊
 * @date 2022-12-19
 */
public class MaintainServiceImpl implements MaintainService {
    @Autowired
    TransactionMapper transactionMapper;
    @Autowired
    TransactionGroupMapper transactionGroupMapper;
    @Autowired
    TransactionSummaryMapper transactionSummaryMapper;

    /**
     * 根据 UID、过账类型 查过账信息
     *
     * @param uid
     * @param type
     * @return
     */
    @Override
    public List<Transaction> getTransferInfo(String uid, String type) {
        List<Transaction> transactionList;
        TransactionExample transactionExample = new TransactionExample();
        transactionExample.createCriteria().andUIDLike("%" + uid + "%").andTransactionTypeEqualTo(type);
        transactionList = transactionMapper.selectByExample(transactionExample);

        return transactionList;
    }

    /**
     * 根据 transactionHistoryId 查（BatchID + ItemID）
     *
     * @param transactionHistoryIds
     * @return
     */
    @Override
    public List<TransactionGroup> getGroupInfo(List<String> transactionHistoryIds) {
        List<TransactionGroup> transactionGroupList;
        TransactionGroupExample transactionGroupExample = new TransactionGroupExample();
        transactionGroupExample.createCriteria().andTransactionHistoryIdIn(transactionHistoryIds);
        transactionGroupList = transactionGroupMapper.selectByExample(transactionGroupExample);

        return transactionGroupList;
    }

    /**
     * 根据 transactionHistoryId 更新 Transaction 表仓位
     *
     * @param transactionHistoryId
     * @param store
     */
    @Override
    public void modifyTransStore(String transactionHistoryId, String store) {
        Transaction transaction1 = new Transaction();
        transaction1.setERPToStock(store);
        transaction1.setToStock(store);
        TransactionExample transactionExample1 = new TransactionExample();
        transactionExample1.createCriteria().andTransactionHistoryIdEqualTo(transactionHistoryId);
        int isUpdate = transactionMapper.updateByExampleSelective(transaction1, transactionExample1);
        if (isUpdate != 0) {
            System.out.println("更新transaction表仓位成功：" + store);
        }
    }

    /**
     * 根据（BatchID + ItemID）更新 summary 表的仓位
     *
     * @param batchId
     * @param itemId
     * @param store
     */
    @Override
    public void modifySummaryStore(String batchId, Integer itemId, String store) {
        TransactionSummary transactionSummary = new TransactionSummary();
        // 设置仓位
        transactionSummary.setToStock(store);
        TransactionSummaryExample transactionSummaryExample = new TransactionSummaryExample();
        transactionSummaryExample.createCriteria().andBatchIdEqualTo(batchId).andItemIdEqualTo(itemId);
        int isUpdate = transactionSummaryMapper.updateByExampleSelective(transactionSummary, transactionSummaryExample);
        if (isUpdate != 0) {
            System.out.println("更新summary表仓位成功：" + store);
        }
    }
}