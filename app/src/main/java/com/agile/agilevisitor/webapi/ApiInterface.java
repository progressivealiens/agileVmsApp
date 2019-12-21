package com.agile.agilevisitor.webapi;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    /*Visitor APIs*/

    @FormUrlEncoded
    @POST("verifyInvitationPasscode")
    Call<ApiResponse> verifyInvitationPasscode(
            @Field("passcode") String passcode
    );

    @FormUrlEncoded
    @POST("panelLogin")
    Call<ApiResponse> panelLogin(
            @Field("companyEmail") String companyEmail,
            @Field("unitCode") String unitCode,
            @Field("unitPassword") String unitPassword,
            @Field("panelCode") String panelCode,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("registerVisitor")
    Call<ApiResponse> registerVisitor(
            @Field("mobileNumber") String mobile,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("getAllSubUnit")
    Call<ApiResponse> getAllSubUnit(
            @Field("token") String token
    );


    @FormUrlEncoded
    @POST("getAllSubUnitPerson")
    Call<ApiResponse> getAllSubUnitPerson(
            @Field("subUnitId") String subUnitCode
    );


    @FormUrlEncoded
    @POST("panelLogout")
    Call<ApiResponse> panelLogout(
            @Field("token") String token
    );

    @POST("SMS/{mobileNumber}/AUTOGEN/agile")
    Call<OTPResponse> sendOTP(
            @Path("mobileNumber") String mobileNumber
    );

    @GET("SMS/VERIFY/{details}/{OTP}")
    Call<OTPResponse> verifyOTP(
            @Path("details") String details,
            @Path("OTP") String OTP
    );

    @Multipart
    @POST("completeProfileDetails")
    Call<ApiResponse> completeProfileDetails(
            @Part("token") RequestBody token,
            @Part("visitorId") RequestBody visitorId,
            @Part("mobileNumber") RequestBody mobileNumber,
            @Part("name") RequestBody name,
            @Part("companyName") RequestBody companyName,
            @Part("email") RequestBody email,
            @Part("category") RequestBody category,
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part adhaarCardPhoto,
            @Part("meetToId") RequestBody meetToId,
            @Part("subUnitId") RequestBody subUnitId,
            @Part("type") RequestBody type,
            @Part("invitationId") RequestBody invitationId
    );


    @FormUrlEncoded
    @POST("previouslyVisitedNoEdit")
    Call<ApiResponse> previouslyVisitedNoEdit(
            @Field("token") String token,
            @Field("visitorId") String visitorId,
            @Field("isWTMEnabled") boolean isWTMEnabled,
            @Field("subUnitId") String subUnitId,
            @Field("meetToId") String meetToId,
            @Field("type") String type,
            @Field("invitationId") String invitationId
    );

    @Multipart
    @POST("previouslyVisitedEdit")
    Call<ApiResponse> previouslyVisitedEdit(
            @Part("token") RequestBody token,
            @Part("visitorId") RequestBody visitorId,
            @Part("mobileNumber") RequestBody mobileNumber,
            @Part("name") RequestBody name,
            @Part("companyName") RequestBody companyName,
            @Part("email") RequestBody email,
            @Part("category") RequestBody category,
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part adhaarCardPhoto,
            @Part("meetToId") RequestBody meetToId,
            @Part("subUnitId") RequestBody subUnitId,
            @Part("isSelfieSend") RequestBody isSelfieSend,
            @Part("isAdhaarCardPhotoSend") RequestBody isAdhaarCardPhotoSend,
            @Part("type") RequestBody type,
            @Part("invitationId") RequestBody invitationId
    );



    @Multipart
    @POST("panelVisitorCheckOut")
    Call<ApiResponse> panelVisitorCheckOut(
            @Part("token") RequestBody token,
            @Part("mobileNumber") RequestBody mobileNumber,
            @Part MultipartBody.Part checkOutPhoto
    );


    /*User APIs*/


    @FormUrlEncoded
    @POST("userLogin")
    Call<ApiResponse> userLogin(
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("userProfile")
    Call<ApiResponse> userProfile(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("sendInvitation")
    Call<ApiResponse> sendInvitation(
            @Field("token") String token,
            @Field("name") String name,
            @Field("mobile") String mobile,
            @Field("email") String email,
            @Field("date") String date,
            @Field("time") String time,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST("ADDON_SERVICES/SEND/TSMS")
    Call<OTPResponse> sendInvitationOnMobile(
            @Field("From") String From,
            @Field("To") String To,
            @Field("TemplateName") String TemplateName,
            @Field("VAR1") String VAR1,
            @Field("VAR2") String VAR2,
            @Field("VAR3") String VAR3,
            @Field("VAR4") String VAR4,
            @Field("VAR5") String VAR5,
            @Field("VAR6") String VAR6,
            @Field("VAR7") String VAR7
    );




    @FormUrlEncoded
    @POST("userLogout")
    Call<ApiResponse> userLogout(
            @Field("token") String token
    );





}
