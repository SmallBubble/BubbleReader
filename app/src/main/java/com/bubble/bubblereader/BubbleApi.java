package com.bubble.bubblereader;

import com.bubble.common.net.JsonResult;

import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 * @author WeiJiaxiang
 * Date：2020/7/8
 * Desc：
 */
public interface BubbleApi {
    @POST("")
    Observable<JsonResult<BookBean>> get();
}
