package com.ht.mapper;

import com.ht.entity.TransactionSummary;
import com.ht.entity.TransactionSummaryExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 丁国钊
 */
@Mapper
@Repository
public interface TransactionSummaryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int countByExample(TransactionSummaryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int deleteByExample(TransactionSummaryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int insert(TransactionSummary record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int insertSelective(TransactionSummary record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    List<TransactionSummary> selectByExample(TransactionSummaryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int updateByExampleSelective(@Param("record") TransactionSummary record, @Param("example") TransactionSummaryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsSummary
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int updateByExample(@Param("record") TransactionSummary record, @Param("example") TransactionSummaryExample example);
}