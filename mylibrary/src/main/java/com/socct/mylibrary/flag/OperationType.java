package com.socct.mylibrary.flag;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 主要是标记事件类型
 *
 * @author WJ
 * @date 19-3-5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public  @interface OperationType {


    /**
     * View的点击事件
     */
    int EVENT_CLICK = 1;

    /**
     * 页面浏览
     */
    int EVENT_VIEW = 2;

}
