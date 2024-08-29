package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 用于增强Mapper类中的自动添加功能
 */
@Component
@Aspect
public class AutoFillAspect {

    /**
     * 对所有打上注解的方法做前置增强
     * @param joinPoint
     */
    @Before("@annotation(com.sky.annotation.AutoFill)")
    public void autoFill(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {

        //0。获取方法参数
         Object arg= joinPoint.getArgs()[0];
         //需要用到反射设置属性值，因为无法直接使用"."调用arg的方法，
        // 因为这里不确定arg是什么类（只是Object，没有对应属性的set方法），并且也无法进行强转后调用对应属性的set方法
        //1.获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2。根据方法签名获得method对象
        Method method = signature.getMethod();
        //3。根据方法对象获取注解
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        //4。根据注解获取value
        OperationType value = annotation.value();
        //5。判断是添加还是更新
        if(value == OperationType.INSERT){
            //添加操作，赋4个值
            //获取字节码对象
            Class clazz = arg.getClass();
            //获取属性
            Field f1 = clazz.getDeclaredField("createTime");
            Field f2 = clazz.getDeclaredField("createUser");
            Field f3 = clazz.getDeclaredField("updateTime");
            Field f4 = clazz.getDeclaredField("updateUser");
            //字段都是private方法，使用暴力反射
            f1.setAccessible(true);
            f2.setAccessible(true);
            f3.setAccessible(true);
            f4.setAccessible(true);
            //赋值
            f1.set(arg, LocalDateTime.now());
            f2.set(arg, BaseContext.getCurrentId());
            f3.set(arg, LocalDateTime.now());
            f4.set(arg, BaseContext.getCurrentId());
        }else{

            //添加操作，赋2个值
            Class clazz = arg.getClass();
            Field f3 = clazz.getDeclaredField("updateTime");
            Field f4 = clazz.getDeclaredField("updateUser");
            f3.setAccessible(true);
            f4.setAccessible(true);
            f3.set(arg, LocalDateTime.now());
            f4.set(arg, BaseContext.getCurrentId());
        }

    }
}
