package com.socct.mylibrary.util;

/**
 * 此类是参考的RxJava类．
 *
 * @author WJ
 * @date 19-4-19
 */
public class ObjectHelper {

    /**
     * Utility class.
     */
    private ObjectHelper() {
        throw new IllegalStateException("No instances!");
    }


    /**
     * @param object 　判断对象
     * @param <T>    　　　判断对象类型
     * @return 判断对象
     */
    public static <T> T requireNonNull(T object) {
        return requireNonNull(object, null);
    }

    /**
     * @param object  判断对象
     * @param message 　提示信息
     * @param <T>     　判断对象类型
     * @retun 判断对象
     */
    public static <T> T requireNonNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }


    /**
     * @param o1 别比较对象１
     * @param o2 　被比较对象２
     * @return true 相同的对象　　false 不同的对象
     */
    public static boolean equals(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }


    /**
     * @param o 　　需要获取hashCode 对象
     * @return 对象为空时返回的值为０
     */
    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }


    /**
     * @param v1 　　比较对象１
     * @param v2 　　比较对象２
     * @return 对象１小于对象2返回　-1 反之则为0
     */
    public static int compare(int v1, int v2) {
        return v1 < v2 ? -1 : (v1 > v2 ? 1 : 0);
    }


    /**
     * @param v1 　　比较对象１
     * @param v2 　　比较对象２
     * @return 对象１小于对象2返回　-1 反之则为0
     */
    public static int compare(long v1, long v2) {
        return v1 < v2 ? -1 : (v1 > v2 ? 1 : 0);
    }


}
