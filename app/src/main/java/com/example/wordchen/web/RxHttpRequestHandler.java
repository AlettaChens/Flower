package com.example.wordchen.web;


import com.example.wordchen.bean.CountDataBean;
import com.example.wordchen.bean.FlowerEntity;
import com.example.wordchen.bean.UserInfoBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RxHttpRequestHandler {


	@POST("user/register")
	Observable<WebResponse> register(@Query("username") String nickname, @Query("password") String password);

	@POST("user/login")
	Observable<WebResponse<UserInfoBean>> login(@Query("username") String nickname, @Query("password") String password);

	@Multipart
	@POST("user/avatarUpdate")
	Observable<WebResponse<String>> uploadUserAvatar(@Query("id") int id, @Part MultipartBody.Part file);


	@POST("user/updateUserInfo")
	Observable<WebResponse> UserInfoUpdate(@Query("id") int id, @Query("username") String username, @Query("pwd") String pwd);

	@Multipart
	@POST("flower/publish")
	Observable<WebResponse> publish(@Query("name") String name, @Query("description") String description, @Part MultipartBody.Part file, @Query("id") String
			id);

	@POST("flower/getInfoByPage")
	Observable<WebResponse<List<FlowerEntity>>> getInfoByPage(@Query("offset") Integer offset, @Query("pageSize") Integer pageSize);


	@POST("flower/getInfoCount")
	Observable<WebResponse<List<CountDataBean>>> getInfoCount();


	@POST("flower/getCollectionById")
	Observable<WebResponse<FlowerEntity>> getCollectionById(@Query("id") String id);

	@POST("flower/deleteInfoById")
	Observable<WebResponse> deleteInfoById(@Query("id") String id);

	@Multipart
	@POST("flower/updateInfo")
	Observable<WebResponse> updateInfo(@Query("name") String name, @Query("description") String description, @Part MultipartBody.Part file, @Query
			("id") String id);
}
