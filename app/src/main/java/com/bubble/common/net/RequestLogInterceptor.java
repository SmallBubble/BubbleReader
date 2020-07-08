package com.bubble.common.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author Bubble
 * @date 2020/7/8
 * @email 1337986595@qq.com
 * @GitHub https://github.com/SmallBubble
 * @Gitte https://gitee.com/SmallCatBubble
 * @Desc
 */
public class RequestLogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//        chain.request(request);


        return null;
    }


    public static RequestLogInterceptor create() {
        return new RequestLogInterceptor();
    }
}
