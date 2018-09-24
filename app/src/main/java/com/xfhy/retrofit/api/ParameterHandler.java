package com.xfhy.retrofit.api;

/**
 * Created by xfhy on 2018/9/24 19:27
 * Description : 处理参数的注解的
 */
public abstract class ParameterHandler {

    abstract void apply(ServiceMethod serviceMethod, String value);

    public static class Query extends ParameterHandler {
        /**
         * 记录参数的注解的值
         * http://api.a3.p?age=2
         * 这里的paramName就是age
         */
        String paramName;

        public Query(String paramName) {
            this.paramName = paramName;
        }

        /**
         * 处理GET 的请求  参数的添加
         *
         * @param serviceMethod 方法的一些信息
         * @param value         传入的参数的值
         */
        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addQueryParameter(paramName, value);
        }
    }

    public static class Field extends ParameterHandler {
        String paramName;

        public Field(String paramName) {
            this.paramName = paramName;
        }

        /**
         * 处理POST 的请求  参数的添加
         *
         * @param serviceMethod 方法的一些信息
         * @param value         传入的参数的值
         */
        @Override
        void apply(ServiceMethod serviceMethod, String value) {
            serviceMethod.addFormFiled(paramName, value);
        }
    }

}
