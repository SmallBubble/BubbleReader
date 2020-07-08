package com.bubble.common.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Bubble
 * @date 2020/7/8
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class NetManager {
    private static Retrofit sRetrofit;

    public static void init(String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(RequestLogInterceptor.create())
                .callTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        sRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 创建Api
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T createApi(Class<T> clazz) {
        if (sRetrofit == null) {
            throw new RuntimeException("未初始化网络请求框架");
        }
        return sRetrofit.create(clazz);
    }
}