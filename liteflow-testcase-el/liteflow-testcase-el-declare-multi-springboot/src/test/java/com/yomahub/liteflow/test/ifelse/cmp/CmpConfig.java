package com.yomahub.liteflow.test.ifelse.cmp;

import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.AnnotionNodeTypeEnum;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import org.springframework.stereotype.Component;

@Component
public class CmpConfig {

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS,nodeId = "a")
    public void processA(NodeComponent bindCmp) {
        System.out.println("ACmp executed!");
    }
    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS,nodeId = "b")
    public void processB(NodeComponent bindCmp) {


        System.out.println("BCmp executed!");
    }

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS,nodeId = "c")
    public void processC(NodeComponent bindCmp) {
        System.out.println("CCmp executed!");
    }


    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS,nodeId = "d")
    public void processD(NodeComponent bindCmp) {
        System.out.println("DCmp executed!");
    }



    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS_IF,nodeId = "x1",nodeType = AnnotionNodeTypeEnum.IF)
    public boolean processIfX1(NodeComponent bindCmp) throws Exception {
        return Boolean.parseBoolean(bindCmp.getTag());
    }

}
