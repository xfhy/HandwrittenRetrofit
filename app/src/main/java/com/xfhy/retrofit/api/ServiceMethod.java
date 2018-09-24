package com.xfhy.retrofit.api;

import com.xfhy.retrofit.api.http.Field;
import com.xfhy.retrofit.api.http.GET;
import com.xfhy.retrofit.api.http.POST;
import com.xfhy.retrofit.api.http.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by xfhy on 2018/9/24 19:09
 * Description :
 */
public class ServiceMethod {
    private Method method;
    /**
     * 方法注解
     */
    private final Annotation[] methodAnnotations;
    /**
     * 参数注解
     */
    private final Annotation[][] parameterAnnotations;
    /**
     * http 方法  GET,POST,DELETE,PUT等等
     */
    private String httpMethod;
    private final HttpUrl baseUrl;
    private final Call.Factory callFactory;
    /**
     * 方法注解的值 eg:@GET("v3/xx")  就是其中的v3/xx
     */
    private String relativeUrl;
    //存储参数的注解值
    private ParameterHandler[] parameterHandlers;
    /**
     * 拼接baseUrl和relativeUrl的结果
     */
    private HttpUrl.Builder urlBuilder;
    /**
     * form表单请求体
     */
    private FormBody.Builder formBuilder;

    public ServiceMethod(Builder builder) {
        this.baseUrl = builder.retrofit.getBaseUrl();
        this.callFactory = builder.retrofit.getCallFactory();
        this.method = builder.method;
        this.methodAnnotations = builder.methodAnnotations;
        this.parameterAnnotations = builder.parameterAnnotations;
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.parameterHandlers = builder.parameterHandlers;
    }

    /**
     * 没有单独去封装Call  直接使用的OkHttp的Call Retrofit的Call是包装过的
     */
    public Call toCall(Object[] args) {
        //1. 创建okhttp的Request
        Request.Builder requestBuilder = new Request.Builder();
        //1.1 地址 -> 其实就是隔壁Retrofit类中的baseUrl+这里的relativeUrl
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        //1.2  将GET或者POST的数据封装进请求
        // 如果是GET请求 将参数拼接上
        for (int i = 0; i < parameterHandlers.length; i++) {
            parameterHandlers[i].apply(this, String.valueOf(args[i]));
        }
        FormBody formBody = null;
        if (formBuilder != null) {
            formBody = formBuilder.build();
        }
        //1.3 将数据放进Request中
        requestBuilder.url(urlBuilder.build());
        requestBuilder.method(httpMethod, formBody);
        Request request = requestBuilder.build();
        //2. 创建Call
        return callFactory.newCall(request);
    }

    /**
     * GET 时  添加请求参数
     *
     * @param paramName 参数的name
     * @param value     参数的值
     */
    public void addQueryParameter(String paramName, String value) {
        urlBuilder.addQueryParameter(paramName, value);
    }

    /**
     * POST 时  添加请求参数 在body里面
     *
     * @param paramName 参数的name
     * @param value     参数的值
     */
    public void addFormFiled(String paramName, String value) {
        if (formBuilder == null) {
            formBuilder = new FormBody.Builder();
        }
        formBuilder.add(paramName, value);
    }

    public static final class Builder {

        private Method method;
        private final Annotation[] methodAnnotations;
        private final Annotation[][] parameterAnnotations;
        /**
         * http 方法  GET,POST,DELETE,PUT等等
         */
        private String httpMethod;
        private String relativeUrl;
        //存储参数的注解值
        private ParameterHandler[] parameterHandlers;
        private Retrofit retrofit;

        public Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            methodAnnotations = method.getAnnotations();
            parameterAnnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            //1. 处理方法注解
            for (Annotation methodAnnotation : methodAnnotations) {
                if (methodAnnotation instanceof GET) {
                    //eg: @GET  @POST
                    parseMethodAnnotation(methodAnnotation);
                }
            }

            //简单判断一下方法使用有Http 的method
            if (httpMethod == null) {
                throw new IllegalArgumentException("httpMethod must not be null");
            }

            //2. 处理参数的注解
            parameterHandlers = new ParameterHandler[parameterAnnotations.length];
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] parameterAnnotation = parameterAnnotations[i];
                //2.1 遍历一个参数上的所有注解
                for (Annotation annotation : parameterAnnotation) {
                    if (annotation instanceof Query) {
                        //2.2 QUERY 只能和GET一起
                        if (!httpMethod.equals("GET")) {
                            throw new IllegalArgumentException("GET and Query must be together");
                        }
                        //2.3 取出参数的注解中定义的值
                        Query query = (Query) annotation;
                        String value = query.value();
                        //2.4 存储参数上的注解值
                        parameterHandlers[i] = new ParameterHandler.Query(value);
                    } else if (annotation instanceof Field) {
                        if (!httpMethod.equals("POST")) {
                            throw new IllegalArgumentException("POST and Field must be together");
                        }
                        Field field = (Field) annotation;
                        String value = field.value();
                        parameterHandlers[i] = new ParameterHandler.Field(value);
                    }
                }
            }
            return new ServiceMethod(this);
        }

        /**
         * 解析方法注解
         */
        private void parseMethodAnnotation(Annotation methodAnnotation) {
            //1. 判断是什么http 方法
            if (methodAnnotation instanceof GET) {
                //2. 记录方法和相对的url
                httpMethod = "GET";
                GET get = (GET) methodAnnotation;
                relativeUrl = get.value();
            } else if (methodAnnotation instanceof POST) {
                httpMethod = "POST";
                POST post = (POST) methodAnnotation;
                relativeUrl = post.value();
            }
        }
    }
}
