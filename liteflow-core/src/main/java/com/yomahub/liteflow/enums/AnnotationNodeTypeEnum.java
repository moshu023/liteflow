package com.yomahub.liteflow.enums;

import com.yomahub.liteflow.core.*;

/**
 * 注解节点类型枚举
 *
 * @author Sorghum
 * @since 2.9.0
 */
public enum AnnotationNodeTypeEnum {
    /**
     * 普通节点
     */
    COMMON("普通", NodeComponent.class),
    /**
     * 选择节点
     */
    SWITCH("选择", NodeSwitchComponent.class),
    /**
     * 条件节点
     */
    IF("条件", NodeIfComponent.class),

    FOR("计数循环",NodeForComponent.class),

    WHILE("条件循环", NodeWhileComponent.class),

    BREAK("跳出循环", NodeBreakComponent.class)
    ;

    /**
     * 描述
     */
    final String desc;
    /**
     * cmp类
     */
    final Class<? extends NodeComponent> cmpClass;

    AnnotationNodeTypeEnum(String desc, Class<? extends NodeComponent> cmpClass) {
        this.desc = desc;
        this.cmpClass = cmpClass;
    }

    /**
     * 得到Node定义类
     * @return node定义类
     */
    public Class<? extends NodeComponent> getCmpClass() {
        return cmpClass;
    }
}
