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
         */
        String value;

        public Query(String value) {
            this.value = value;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {

        }
    }

    public static class Field extends ParameterHandler {
        String value;

        public Field(String value) {
            this.value = value;
        }

        @Override
        void apply(ServiceMethod serviceMethod, String value) {

        }
    }

}
