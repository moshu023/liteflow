package com.yomahub.liteflow.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.annotation.*;
import com.yomahub.liteflow.core.*;
import com.yomahub.liteflow.core.proxy.ComponentProxy;
import com.yomahub.liteflow.exception.ComponentProxyErrorException;
import com.yomahub.liteflow.exception.LiteFlowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 组件代理类通用方法
 * 主要用于声明式组件
 * @author Bryan.Zhang
 * @since 2.6.14
 */
public class LiteFlowProxyUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LiteFlowProxyUtil.class);

    //判断一个bean是否是声明式组件
    public static boolean isDeclareCmp(Class<?> clazz){
        //查看bean里的method是否有方法标记了@LiteflowMethod标注
        //这里的bean有可能是cglib加强过的class，所以要先进行个判断
        Class<?> targetClass;
        if (isCglibProxyClass(clazz)){
            targetClass = getUserClass(clazz);
        }else{
            targetClass = clazz;
        }
        // 判断是否有方法标记了@LiteflowMethod标注，有则为声明式组件
        return Arrays.stream(targetClass.getMethods()).anyMatch(
                method -> method.getAnnotation(LiteflowMethod.class) != null
        );
    }

    //对一个满足声明式的bean进行代理,生成代理类数组
    public static List<NodeComponent> proxy2NodeComponent(Object bean, String nodeId){
        try{
            LiteflowCmpDefine liteflowCmpDefine = bean.getClass().getAnnotation(LiteflowCmpDefine.class);
            LiteflowSwitchCmpDefine liteflowSwitchCmpDefine = bean.getClass().getAnnotation(LiteflowSwitchCmpDefine.class);
            LiteflowIfCmpDefine liteflowIfCmpDefine = bean.getClass().getAnnotation(LiteflowIfCmpDefine.class);
            LiteflowForCmpDefine liteflowForCmpDefine = bean.getClass().getAnnotation(LiteflowForCmpDefine.class);
            LiteflowWhileCmpDefine liteflowWhileCmpDefine = bean.getClass().getAnnotation(LiteflowWhileCmpDefine.class);
            LiteflowBreakCmpDefine liteflowBreakCmpDefine = bean.getClass().getAnnotation(LiteflowBreakCmpDefine.class);

            ComponentProxy proxy;
            if (ObjectUtil.isNotNull(liteflowCmpDefine)){
                proxy = new ComponentProxy(nodeId, bean, NodeComponent.class);
                return proxy.getProxyList();
            }

            if (ObjectUtil.isNotNull(liteflowSwitchCmpDefine)){
                proxy = new ComponentProxy(nodeId, bean, NodeSwitchComponent.class);
                return proxy.getProxyList();
            }

            if (ObjectUtil.isNotNull(liteflowIfCmpDefine)){
                proxy = new ComponentProxy(nodeId, bean, NodeIfComponent.class);
                return proxy.getProxyList();
            }

            if (ObjectUtil.isNotNull(liteflowForCmpDefine)){
                proxy = new ComponentProxy(nodeId, bean, NodeForComponent.class);
                return proxy.getProxyList();
            }

            if (ObjectUtil.isNotNull(liteflowWhileCmpDefine)){
                proxy = new ComponentProxy(nodeId, bean, NodeWhileComponent.class);
                return proxy.getProxyList();
            }

            if (ObjectUtil.isNotNull(liteflowBreakCmpDefine)){
                proxy = new ComponentProxy(nodeId, bean, NodeBreakComponent.class);
                return proxy.getProxyList();
            }
            return new ComponentProxy(nodeId, bean, NodeComponent.class).getProxyList();
        }catch (LiteFlowException liteFlowException){
            throw liteFlowException;
        }
        catch (Exception e){
            String errMsg = StrUtil.format("Error while proxying bean[{}]",bean.getClass().getName());
            LOG.error(errMsg);
            throw new ComponentProxyErrorException(errMsg);
        }
    }

    public static boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null && isCglibProxyClassName(clazz.getName()));
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains("$$")) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }

    private static boolean isCglibProxyClassName(String className) {
        return (className != null && className.contains("$$"));
    }
}
