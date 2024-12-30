package com.green.firstproject.user;

import com.green.firstproject.user.model.*;
import com.green.firstproject.user.model.dto.DuplicateCheckResult;
import com.green.firstproject.user.model.dto.UserInfo;
import com.green.firstproject.user.model.dto.UserLoginInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;


@Mapper
public interface UserMapper {

    UserLoginInfo userSignIn(String userId);
    UserInfo selUserInfo (UserInfoGetReq p);

    DuplicateCheckResult checkDuplicates(UserSignUpReq p); // 중복 체크, BigDecimal 대응
    int insertUser(UserSignUpReq p); // 유저 등록


    UserInfo findUserByEmail(String email); // 가입된 유저인지 이메일로 검증
    int updatePassword (String email, String password); // 새로운 비밀번호 등록



    // 사용자 프로필 업데이트
    int updUserProfile(UserInfo userInfo);

    // 사용자 정보 조회
    UserInfo getUserInfo(long signedUserNo);

    // 닉네임 중복 체크
    Boolean checkNicknameExists(String nickname);
}
