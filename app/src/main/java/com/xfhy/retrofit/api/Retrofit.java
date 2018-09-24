package com.xfhy.retrofit.api;

import com.xfhy.retrofit.api.util.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by xfhy on 2018/9/24 16:09
 * Description :
 */
public class Retrofit {
    private HttpUrl baseUrl;
    private Call.Factory callFactory;
    private ConcurrentHashMap<Method, ServiceMethod> mServiceMethodCache = new ConcurrentHashMap<>();

    public Retrofit(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        //这里应该判断一下clazz的正确性
        Utils.validateServiceInterface(clazz);
        //动态代理
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //1. 采集数据
                //2. 请求网络
                //拿到数据  然后构建一个OkHttp的Call
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.toCall(args);
            }
        });
    }

    private ServiceMethod loadServiceMethod(Method method) {
        //有缓存取缓存
        ServiceMethod serviceMethod = mServiceMethodCache.get(method);
        if (serviceMethod != null) {
            return serviceMethod;
        }
        //无缓存  则读数据  采集数据
        serviceMethod = new ServiceMethod.Builder(this, method).build();
        mServiceMethodCache.putIfAbsent(method, serviceMethod);
        return serviceMethod;
    }

    public HttpUrl getBaseUrl() {
        return baseUrl;
    }

    public Call.Factory getCallFactory() {
        return callFactory;
    }

    public static final class Builder {
        private HttpUrl baseUrl;
        private Call.Factory callFactory;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder callFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Retrofit build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }
            return new Retrofit(this);
        }

    }

}
