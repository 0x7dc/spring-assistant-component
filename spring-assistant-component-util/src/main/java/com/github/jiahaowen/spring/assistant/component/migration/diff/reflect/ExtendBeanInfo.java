package com.github.jiahaowen.spring.assistant.component.migration.diff.reflect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义bean类型,包含对应处理方法
 *
 * @author jiahaowen.jhw
 * @version $Id: ExtendBeanInfo.java, v 0.1 2016-10-30 下午9:49 jiahaowen.jhw Exp $
 */
public final class ExtendBeanInfo implements BeanInfo {

    // set方法前缀
    private static final String SETTER_PREFIX = "set";
    // get方法前缀
    private static final String GETTER_PREFIX = "get";
    // is方法前缀
    private static final String BOOLEAN_GETTER_PREFIX = "is";

    /** 对象信息 */
    private final BeanInfo beanInfo;

    /** 构造函数 */
    private ExtendBeanInfo(final BeanInfo beanInfo) {
        this.beanInfo = beanInfo;
    }

    /** 根据给定的对象信息,构造实例 */
    public static BeanInfo of(final BeanInfo beanInfo) {
        checkNotNull(beanInfo, "给定的beanInfo不能为空.");
        checkArgument(!(beanInfo instanceof ExtendBeanInfo), "给定的对象必须类GenericTypeAwareBeanInfo类型.");

        return new ExtendBeanInfo(beanInfo);
    }

    protected BeanInfo delegate() {
        return beanInfo;
    }

    /**
     * 得到给定对象的属性信息
     *
     * @return
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        // 对象的全部属性信息
        final PropertyDescriptor[] propertyDescriptors = delegate().getPropertyDescriptors();
        final Class<?> type = delegate().getBeanDescriptor().getBeanClass();
        final Set<PropertyDescriptor> matchingProperties = new HashSet<PropertyDescriptor>();

        for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            final PropertyDescriptor genericTypeAware =
                    getPropertyDescriptor(type, propertyDescriptor);
            matchingProperties.add(genericTypeAware);
        }

        return matchingProperties.toArray(new PropertyDescriptor[0]);
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return delegate().getBeanDescriptor();
    }

    /** 得到给定类型的属性描述器 */
    private PropertyDescriptor getPropertyDescriptor(
            final Class<?> type, final PropertyDescriptor propertyDescriptor) {

        final Method writeMethod = propertyDescriptor.getWriteMethod();
        final Method readMethod = propertyDescriptor.getReadMethod();

        if (!(readMethod != null && (writeMethod == null || writeMethod.isBridge()))) {
            return propertyDescriptor;
        }

        final String propertyName = propertyDescriptor.getName();
        final String readMethodName = readMethod.getName();
        final String writeMethodName = determineWriteMethodNameForReadMethod(readMethod);

        // 过滤得到非桥接set方法
        final Method potentialWriteMethod = getPublicNonBridgedMethod(writeMethodName, type);

        // 没有set方法时
        if (potentialWriteMethod == null) {
            return propertyDescriptor;
        }

        try {
            // 过滤得到非桥接的get方法
            final Method potentialReadMethod = getPublicNonBridgedMethod(readMethodName, type);

            return new PropertyDescriptor(propertyName, potentialReadMethod, potentialWriteMethod);

        } catch (final IntrospectionException e) {
            throw new IllegalArgumentException(
                    "不能处理的类型:" + propertyName + "'. 错误原因为: " + e.getMessage() + ".", e);
        }
    }

    /** 根据get方法名称,确定set方法名称 */
    private String determineWriteMethodNameForReadMethod(final Method readMethod) {
        final String readMethodName = readMethod.getName();
        if (readMethodName.contains(BOOLEAN_GETTER_PREFIX)) {
            return readMethodName.replace(BOOLEAN_GETTER_PREFIX, SETTER_PREFIX);
        }
        return readMethodName.replace(GETTER_PREFIX, SETTER_PREFIX);
    }

    /** 根据方法名称及对象类型,判断是否为桥接方法.非桥接方法时,返回方法信息 */
    private Method getPublicNonBridgedMethod(final String methodName, final Class<?> type) {
        for (final Method method : type.getMethods()) {
            final boolean isMatchingName = method.getName().equals(methodName);
            final boolean isNotBridge = !method.isBridge();

            if (isNotBridge && isMatchingName) {
                return method;
            }
        }

        return null;
    }

    /**
     * *************************************************************************************************************************
     */
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }

    @Override
    public int getDefaultEventIndex() {
        return 0;
    }

    @Override
    public int getDefaultPropertyIndex() {
        return 0;
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return null;
    }

    @Override
    public Image getIcon(int iconKind) {
        return null;
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return null;
    }

    @Override
    public String toString() {
        return delegate().toString();
    }
}