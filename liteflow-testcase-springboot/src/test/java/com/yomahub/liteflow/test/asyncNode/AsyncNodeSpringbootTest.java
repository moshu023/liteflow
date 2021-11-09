package com.yomahub.liteflow.test.asyncNode;

import cn.hutool.core.collection.ListUtil;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.entity.data.DefaultSlot;
import com.yomahub.liteflow.entity.data.LiteflowResponse;
import com.yomahub.liteflow.test.BaseTest;
import com.yomahub.liteflow.test.asyncNode.exception.TestException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 测试隐式调用子流程
 * 单元测试
 *
 * @author ssss
 */
@RunWith(SpringRunner.class)
@TestPropertySource(value = "classpath:/asyncNode/application.properties")
@SpringBootTest(classes = AsyncNodeSpringbootTest.class)
@EnableAutoConfiguration
@ComponentScan({"com.yomahub.liteflow.test.asyncNode.cmp"})
public class AsyncNodeSpringbootTest extends BaseTest {
    @Resource
    private FlowExecutor flowExecutor;

    /*****
     * 标准chain 嵌套选择 嵌套子chain进行执行
     * 验证了when情况下 多个node是并行执行
     * 验证了默认参数情况下 when可以加载执行
     * **/
    @Test
    public void testBaseConditionFlow1() {
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain1", "it's a base request");
        Assert.assertTrue(response.isSuccess());
        System.out.println(response.getSlot().printStep());
    }

    @Test
    public void testBaseConditionFlow2() {
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain2", "it's a base request");
        Assert.assertTrue(ListUtil.toList("b==>j==>g==>f==>h","b==>j==>g==>h==>f",
                "b==>j==>h==>g==>f","b==>j==>h==>f==>g",
                "b==>j==>f==>h==>g","b==>j==>f==>g==>h"
                ).contains(response.getSlot().printStep()));
    }

    //相同group的并行组，会合并，并且errorResume根据第一个when来，这里第一个when配置了不抛错
    @Test
    public void testBaseErrorResumeConditionFlow4() {
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain4", "it's a base request");
        //因为不记录错误，所以最终结果是true
        Assert.assertTrue(response.isSuccess());
        //因为是并行组，所以即便抛错了，其他组件也会执行，i在流程里配置了2遍，i抛错，但是也执行了2遍，这里验证下
        Integer count = response.getSlot().getData("count");
        Assert.assertEquals(new Integer(2), count);
        //因为配置了不抛错，所以response里的cause应该为null
        Assert.assertNull(response.getCause());
    }

    //相同group的并行组，会合并，并且errorResume根据第一个when来，这里第一个when配置了会抛错
    @Test
    public void testBaseErrorResumeConditionFlow5() throws Exception {
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain5", "it's a base request");
        //整个并行组是报错的，所以最终结果是false
        Assert.assertFalse(response.isSuccess());
        //因为是并行组，所以即便抛错了，其他组件也会执行，i在流程里配置了2遍，i抛错，但是也执行了2遍，这里验证下
        Integer count = response.getSlot().getData("count");
        Assert.assertEquals(new Integer(2), count);
        //因为第一个when配置了会报错，所以response里的cause里应该会有TestException
        Assert.assertEquals(TestException.class, response.getCause().getClass());
    }

    //不同group的并行组，不会合并，第一个when的errorResume是false，会抛错，那第二个when就不会执行
    @Test
    public void testBaseErrorResumeConditionFlow6() throws Exception {
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain6", "it's a base request");
        //第一个when会抛错，所以最终结果是false
        Assert.assertFalse(response.isSuccess());
        //因为是不同组并行组，第一组的when里的i就抛错了，所以i就执行了1遍
        Integer count = response.getSlot().getData("count");
        Assert.assertEquals(new Integer(1), count);
        //第一个when会报错，所以最终response的cause里应该会有TestException
        Assert.assertEquals(TestException.class, response.getCause().getClass());
    }

    //不同group的并行组，不会合并，第一个when的errorResume是true，不会报错，那第二个when还会继续执行，但是第二个when的errorResume是false，所以第二个when会报错
    @Test
    public void testBaseErrorResumeConditionFlow7() throws Exception {
        LiteflowResponse<DefaultSlot> response = flowExecutor.execute2Resp("chain7", "it's a base request");
        //第二个when会抛错，所以最终结果是false
        Assert.assertFalse(response.isSuccess());
        //  传递了slotIndex，则set的size==2
        Integer count = response.getSlot().getData("count");
        Assert.assertEquals(new Integer(2), count);
        //第一个when会报错，所以最终response的cause里应该会有TestException
        Assert.assertEquals(TestException.class, response.getCause().getClass());
    }
}
