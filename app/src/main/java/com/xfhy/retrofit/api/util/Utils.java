package com.xfhy.retrofit.api.util;

/**
 * Created by xfhy on 2018/9/24 19:05
 * Description :
 */
public class Utils {

    /**
     * 判断接口的正确性
     */
    public static <T> void validateServiceInterface(Class<T> service) {
        //必须是接口
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

}
