package com.yomahub.liteflow.core;

import com.yomahub.liteflow.script.ScriptExecuteWrap;
import com.yomahub.liteflow.script.ScriptExecutorFactory;

/**
 * 脚本条件节点
 * @author Bryan.Zhang
 * @since 2.6.0
 */
public class ScriptSwitchComponent extends NodeSwitchComponent implements ScriptComponent{

    @Override
    public String processSwitch() throws Exception {
        ScriptExecuteWrap wrap = new ScriptExecuteWrap();
        wrap.setCurrChainName(this.getCurrChainName());
        wrap.setNodeId(this.getNodeId());
        wrap.setSlotIndex(this.getSlotIndex());
        wrap.setCmpData(this.getCmpData(Object.class));
        return (String)ScriptExecutorFactory.loadInstance().getScriptExecutor().execute(wrap);
    }

    @Override
    public void loadScript(String script) {
        ScriptExecutorFactory.loadInstance().getScriptExecutor().load(getNodeId(), script);
    }
}
