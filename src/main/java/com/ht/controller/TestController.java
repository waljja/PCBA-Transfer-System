package com.ht.controller;

import com.ht.entity.PCBAMI;
import com.ht.entity.PCBASMT;
import com.ht.entity.PCBASMTExample;
import com.ht.mapper.PCBAMIMapper;
import com.ht.mapper.PCBASMTMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("JavaAPI接口")
@CrossOrigin
@RestController // 跨域调用
@RequestMapping(value = "/api")
public class TestController {
    @Autowired
    PCBASMTMapper pcbasmtMapper;
    @Autowired
    PCBAMIMapper pcbamiMapper;

    @ApiResponses({@ApiResponse(code = 400, message = "请求参数没有填好"),
            @ApiResponse(code = 404, message = "请求路径没有或者页面跳转路径不正确")})
    @ApiOperation(value="测试", notes="测试接口")
    @RequestMapping(value="/test", method= RequestMethod.POST)
    public void testController() {
        List<PCBASMT> smtList;
        List<PCBAMI> miList;
        PCBASMT smt = new PCBASMT();
        PCBAMI mi = new PCBAMI();

        // 根据工单查出所有SMT发料数据
        PCBASMTExample pcbasmtExample = new PCBASMTExample();
        pcbasmtExample.createCriteria().andWoEqualTo("000001600872");
        smtList = pcbasmtMapper.selectByExample(pcbasmtExample);

        System.out.println("发料表数据行数："+smtList.size());

        for (int i = 0; i < smtList.size(); i++) { // 循环写入PCBAMI表
            smt = smtList.get(i);
            BeanUtils.copyProperties(smt, mi); // 获取SMT每行的数据，直接映射到MI对象

            int isInserted = pcbamiMapper.insertSelective(mi); // 插入PCBAMI表

            if (isInserted !=0) {
                System.out.println("插入成功!下标为：" + i);
            }else {
                System.out.println("插入失败！下标为：" + i);
            }
        }
    }
}
