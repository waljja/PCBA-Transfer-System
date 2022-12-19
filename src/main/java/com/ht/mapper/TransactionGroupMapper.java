package com.ht.mapper;

import com.ht.entity.TransactionGroup;
import com.ht.entity.TransactionGroupExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 丁国钊
 */
@Mapper
@Repository
public interface TransactionGroupMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int countByExample(TransactionGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int deleteByExample(TransactionGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int insert(TransactionGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int insertSelective(TransactionGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    List<TransactionGroup> selectByExample(TransactionGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int updateByExampleSelective(@Param("record") TransactionGroup record, @Param("example") TransactionGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table xTend_MaterialTransactionsGroup
     *
     * @mbggenerated Mon Dec 19 14:31:30 CST 2022
     */
    int updateByExample(@Param("record") TransactionGroup record, @Param("example") TransactionGroupExample example);
}