package com.xfhy.retrofit;

import com.xfhy.retrofit.api.Retrofit;
import com.xfhy.retrofit.api.http.GET;
import com.xfhy.retrofit.api.http.POST;
import com.xfhy.retrofit.api.http.Query;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RetrofitUnitTest {

    /**
     * 金山词霸 api
     */
    interface Word {
        @GET("dsapi/")
        Call get(@Query("data") String date);
    }

    interface Weather {
        @GET("v3/weather/weatherInfo")
        Call get(@Query("city") String city, @Query("key") String key);

        @POST("v3/weather/weatherInfo")
        Call post(@Query("city") String city, @Query("key") String key);
    }

    @Test
    public void addition_isCorrect() throws IOException {
        Retrofit.Builder builder = new Retrofit.Builder();
//        Retrofit retrofit = builder.baseUrl("http://open.iciba.com/").build();
        Retrofit retrofit = builder.baseUrl("http://restapi.amap.com/").build();

        Word word = retrofit.create(Word.class);
        Weather weather = retrofit.create(Weather.class);

//        Call call = word.get("2018-05-03");
        Call call = weather.get("成都", "13cb58f5884f9749287abbead9c658f2");

        Response response = call.execute();
        System.out.println(response.body().string());
    }
}