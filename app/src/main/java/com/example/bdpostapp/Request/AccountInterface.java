package com.example.bdpostapp.Request;


import com.example.bdpostapp.Entity.GsonBean.Receiving.Account;
import com.example.bdpostapp.Entity.GsonBean.Receiving.BaseRequest;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Chat;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Group;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MyMarker;
import com.example.bdpostapp.Entity.GsonBean.Receiving.MessageInfo;
import com.example.bdpostapp.Entity.GsonBean.Receiving.Normal;
import com.example.bdpostapp.Entity.GsonBean.Receiving.TerminalDetail;
import com.example.bdpostapp.Global.Constant;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AccountInterface {
    // 注解说明：参考 Postman
    // 传入 Headers 参数：@Header("Authorization") String token：键-"Authorization"，值-token
    // 传入 Params 参数：@Query("mode") String mode, @Query("to") String phone：键-"mode"，值-mode、键-"to"，值-phone
    // 传入 Params 参数：也可以 @QueryMap HashMap<String,String> params：创建多个键值对的 params HashMap
    // 传入 Body 的 Raw 参数：@Body HashMap<String, String> hashMap：创建 一个 HashMap 作为 JSON 格式数据

    // 传入 Body 的 x-www-form-urlencoded 参数：定义请求方式前先 @Multipart 例如：
    // @FormUrlEncoded
    // @POST(Constant.CREATE_ROOMS)
    // Observable<BaseRequest<Rooms>> createRooms(@Field("name") String name, @Field("age") int age);
    // 传入 键-"name"，值-name 的 x-www-form-urlencoded 参数，键-"age"，值-age 的 x-www-form-urlencoded 参数

    // 传入 Body 的 form-data 参数（文本/文件）：定义请求方式前先 @Multipart 例如：
    // @Multipart
    // @POST(Constant.CREATE_ROOMS)
    // Observable<BaseRequest<Rooms>> createRooms(@Header("Authorization") String token, @Part("name") RequestBody nickname , @Part MultipartBody.Part image);
    // 传入 键-"Authorization"，值-token 的 Headers 参数，键-"name"，值-nickname 的 form-data 文本参数，image 的 form-data 文件参数

    @POST(Constant.ACCOUNT_PWD_LOGIN)
    Observable<BaseRequest<Account>> pwdLogin(@Query("account") String account, @Query("password") String password);
    @GET(Constant.CHAT_LIST)
    Observable<BaseRequest<List<Chat>>> getChatList(@Header("Authorization") String token);
    @GET
    Observable<BaseRequest<MessageInfo>> getMessageList(@Url String url, @Header("Authorization") String token, @Query("page") String page, @Query("pageSize") String pageSize);
    @PUT
    Observable<BaseRequest<String>> clearUnreadMessages(@Url String url, @Header("Authorization") String token);
    @GET(Constant.GET_MARKER)
    Observable<BaseRequest<List<MyMarker>>> getMarker(@Header("Authorization") String token, @Query("leftTopLat") String leftTopLat, @Query("leftTopLng") String leftTopLng, @Query("rightBottomLat") String rightBottomLat, @Query("rightBottomLng") String rightBottomLng, @Query("pixelInMeter") String pixelInMeter, @Query("zoom") String zoom);
    @POST(Constant.GET_TERMINAL_DETAIL)
    Observable<BaseRequest<List<TerminalDetail>>> getTerminalDetail(@Header("Authorization") String token,@Body HashMap<String, String> addr);
    @GET(Constant.TERMINAL_LIST)
    Observable<BaseRequest<List<Group>>> getTerminalList(@Header("Authorization") String token, @Query("include") boolean include);

    // 枚举类型接口
    @GET(Constant.DEVICE_TYPES)
    Observable<BaseRequest<List<Normal>>> getDeviceTypes(@Header("Authorization") String token);
    @GET(Constant.LOCATION_TYPES)
    Observable<BaseRequest<List<Normal>>> getLocationTypes(@Header("Authorization") String token);
    @GET(Constant.LOCATION_STATUS)
    Observable<BaseRequest<List<Normal>>> getLocationStatus(@Header("Authorization") String token);
}
