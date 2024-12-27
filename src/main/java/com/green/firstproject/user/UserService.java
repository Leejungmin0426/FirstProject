package com.green.firstproject.user;

import com.green.firstproject.common.exception.ResponseCode;
import com.green.firstproject.common.exception.ResponseResult;
import com.green.firstproject.user.model.*;
import com.green.firstproject.user.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper mapper;

    /**
     * 요청 데이터 유효성 검증 메서드
     */
    private boolean isValidUserRequest(UserInfoGetReq p) {
        // signedUserNo와 targetUserNo가 0보다 큰지 확인
        return p != null && p.getSignedUserNo() > 0 && p.getTargetUserNo() > 0;
    }

    private boolean isUserLoggedIn(long signedUserNo) {
        // PK가 0보다 크면 로그인된 것으로 간주
        return signedUserNo > 0;
    }

    // 1. 사용자 로그인
    public ResponseResult userSignIn(UserSignInReq p) {
        // 요청 데이터 검증
        if (p == null || p.getEmail() == null || p.getEmail().isEmpty()) {
            return ResponseResult.badRequest(ResponseCode.INCORRECT_EMAIL); // 이메일이 없거나 비어 있을 경우
        }

        if (p.getPassword() == null || p.getPassword().isEmpty()) {
            return ResponseResult.badRequest(ResponseCode.INCORRECT_PASSWORD); // 비밀번호가 없거나 비어 있을 경우
        }

        // 매퍼 메서드를 호출하여 사용자 조회
        UserLoginInfo info = mapper.userSignIn(p.getEmail());
        if (info == null) {
            return ResponseResult.badRequest(ResponseCode.NO_EXIST_USER); // 사용자 정보가 없을 경우
        }

        // 비밀번호 검증
        if (!BCrypt.checkpw(p.getPassword(), info.getPassword())) {
            System.out.println("입력된 비밀번호: " + p.getPassword());
            System.out.println("DB 저장된 해시 비밀번호: " + info.getPassword());
            return ResponseResult.badRequest(ResponseCode.INCORRECT_PASSWORD); // 비밀번호 불일치
        }

        // 첫 로그인 여부 확인
        boolean firstLogin = info.isFirstLogin(); // DB에서 firstLogin 여부 가져옴

        // 성공 응답
        return new UserSignInRes(ResponseCode.OK.getCode(), firstLogin);
    }


    @Transactional
    public ResponseResult selUserInfo(UserInfoGetReq p) {
        log.debug("Service Request: {}", p);

        // 1. 로그인 여부 검증
        if (!isUserLoggedIn(p.getSignedUserNo())) {
            return ResponseResult.badRequest(ResponseCode.NO_FORBIDDEN); // 로그인하지 않은 사용자
        }

        // 2. 데이터 유효성 검증
        if (!isValidUserRequest(p)) {
            return ResponseResult.badRequest(ResponseCode.NOT_NULL); // 필수 값 검증 실패
        }

        // 3. 사용자 정보 조회
        UserInfo userInfo = mapper.selUserInfo(p);
        if (userInfo == null) {
            return ResponseResult.badRequest(ResponseCode.NO_EXIST_USER); // 유저 정보 없음
        }

        // 4. 본인 여부 확인
        boolean isMyInfo = (p.getSignedUserNo() == p.getTargetUserNo());

        // 5. UserInfoGetRes 반환
        UserInfoGetRes response = new UserInfoGetRes();
        response.setEmail(userInfo.getEmail());
        response.setNickname(userInfo.getNickname());
        response.setUserStatusMessage(userInfo.getUserStatusMessage());
        response.setProfilePic(userInfo.getProfilePic());

        // 본인 여부를 결과에 포함
        response.setMyInfo(isMyInfo);

        return response;
    }


    public ResponseResult userSignUp(UserSignUpReq p) {
        // 1. 필수 값 검증
        if (p.getEmail() == null || p.getUserId() == null || p.getPassword() == null || p.getPasswordConfirm() == null) {
            return ResponseResult.badRequest(ResponseCode.NOT_NULL); // 필수 값 누락
        }

        // 2. 이메일 형식 검증
        log.info("Validating email format for email: {}", p.getEmail());
        if (!p.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            log.error("Invalid email format: {}", p.getEmail());
            return ResponseResult.badRequest(ResponseCode.EMAIL_FORMAT_ERROR); // 이메일 형식 오류
        }

        // 3. 비밀번호 형식 검증
        log.info("Validating password format for userId: {}", p.getUserId());
        if (!PasswordValidator.isValidPassword(p.getPassword())) {
            return ResponseResult.badRequest(ResponseCode.PASSWORD_FORMAT_ERROR); // 비밀번호 형식 오류
        }

        // 4. 중복 체크
        DuplicateCheckResult duplicateCheck = mapper.checkDuplicates(p); // DTO로 반환
        if (duplicateCheck.getEmailCount() > 0) {
            return ResponseResult.badRequest(ResponseCode.DUPLICATE_EMAIL); // 이메일 중복
        }
        if (duplicateCheck.getUserIdCount() > 0) {
            return ResponseResult.badRequest(ResponseCode.DUPLICATE_ID); // 유저 ID 중복
        }
        if (duplicateCheck.getNicknameCount() > 0) {
            return ResponseResult.badRequest(ResponseCode.DUPLICATE_NICKNAME); // 닉네임 중복
        }

        // 5. 비밀번호 확인 검증
        if (!p.getPassword().equals(p.getPasswordConfirm())) {
            return ResponseResult.badRequest(ResponseCode.PASSWORD_CHECK_ERROR); // 비밀번호 확인 불일치
        }

        // 6. 닉네임이 없을 경우 자동 생성
        if (p.getNickname() == null || p.getNickname().isBlank()) {
            String generatedNickname = NicknameGenerator.generateDefaultNickname();
            p.setNickname(generatedNickname);
        }

        // 7. 비밀번호 암호화
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(p.getPassword());
        p.setPassword(encryptedPassword);

        // 8. 회원가입 로직 (인증된 상태로 삽입 처리)
        int insertResult = mapper.insertUser(p);
        if (insertResult <= 0) {
            return ResponseResult.serverError(); // 회원가입 실패
        }

        // 9. 성공 응답
        return new UserSignUpRes(ResponseCode.OK.getCode());
    }


    public ResponseResult updatePassword(UserFindPasswordReq p) {

        // 1. 사용자 조회
        UserInfo info = mapper.findUserByEmail(p.getEmail());
        if (info == null) {
            return ResponseResult.badRequest(ResponseCode.NO_EXIST_USER); // 사용자 정보가 없을 경우
        }

        // 2. 비밀번호 검증
        if (!PasswordValidator.isValidPassword(p.getPassword())) {
            return ResponseResult.badRequest(ResponseCode.PASSWORD_FORMAT_ERROR); // 비밀번호 형식 오류
        }

        if (!p.getPassword().equals(p.getPasswordConfirm())) {
            return ResponseResult.badRequest(ResponseCode.PASSWORD_CHECK_ERROR); // 비밀번호 확인 불일치
        }

        // 3. 비밀번호 암호화
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(p.getPassword());

        // 4. DB 업데이트
        int updatedPassword = mapper.updatePassword(p.getEmail(), encryptedPassword);
        if (updatedPassword > 0) {
            return new UserFindPasswordRes(ResponseCode.OK.getCode()); // 성공
        }

        // 5. 실패 시 처리
        return ResponseResult.badRequest(ResponseCode.FAIL); // 일반 실패 처리
    }
}