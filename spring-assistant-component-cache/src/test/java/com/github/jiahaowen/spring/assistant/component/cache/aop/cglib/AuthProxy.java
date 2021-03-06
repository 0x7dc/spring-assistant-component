package com.github.jiahaowen.spring.assistant.component.cache.aop.cglib;

import java.lang.reflect.Method;
import org.assertj.core.internal.cglib.proxy.MethodInterceptor;
import org.assertj.core.internal.cglib.proxy.MethodProxy;

// 方法拦截器

/**
 * @author: jiahaowen
 */
public class AuthProxy implements MethodInterceptor {

    private String userName;

    AuthProxy(String userName) {
        this.userName=userName;
    }

    // 用来增强原有方法
    @Override
    public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
        // 权限判断
        if(!"张三".equals(userName)) {
            System.out.println("你没有权限！");
            return null;
        }
        return arg3.invokeSuper(arg0, arg2);
    }
}
