package com.green.firstproject.user;

import com.green.firstproject.user.model.*;
import com.green.firstproject.user.model.dto.UserInfo;
import com.green.firstproject.user.model.dto.UserLoginInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserLoginInfo userSignIn(String userId);
    // 첫 로그인 여부 업데이트
    void updateFirstLogin(String userId);



    UserInfo selUserInfo (UserInfoGetReq p);

    int checkNicknameDuplicate (UserSignUpReq p); //닉네임 중복 체크
    int checkEmailDuplicate (UserSignUpReq p); //이메일 중복 체크
    int checkUserIdDuplicate (UserSignUpReq p); // 중복 체크


    int insertUser(UserSignUpReq p); // 유저 등록


    UserInfo findUserByEmail(String email); // 가입된 유저인지 이메일로 검증
    int updatePassword (String email, String password); // 새로운 비밀번호 등록


    String findUserIdByEmail (String email); // 사용자 Id를 email로 조회

    // 사용자 프로필 업데이트
    int updUserProfile(UserInfo userInfo);

    // 사용자 정보 조회
    UserInfo getUserInfo(long signedUserNo);

    // 닉네임 중복 체크
    Boolean checkNicknameExists(String nickname);
}
