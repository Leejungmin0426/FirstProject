package com.green.firstproject.user;

import com.green.firstproject.common.exception.MyFileUtils;
import com.green.firstproject.common.exception.ResponseCode;
import com.green.firstproject.common.exception.ResponseResult;
import com.green.firstproject.user.model.*;
import com.green.firstproject.user.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;



@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;

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

        // 매퍼 메서드를 호출하여 사용자 조회
        UserLoginInfo info = mapper.userSignIn(p.getUserId());
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
        boolean isFirstLogin = info.isFirstLogin();
        if (info.isFirstLogin()) {
            // 첫 로그인인 경우 처리
            mapper.updateFirstLogin(p.getUserId()); // 첫 로그인 완료 처리
        } else {
            // 이미 로그인한 사용자
        }

        // 응답 생성
        return new UserSignInRes(ResponseCode.OK.getCode(), isFirstLogin);
    }



    @Transactional
    public ResponseResult findUserIdByEmail (String email){

        // 데이터베이스에서 이메일로 사용자 조회
        String userId = mapper.findUserIdByEmail(email);
        if (userId == null) {
            return ResponseResult.badRequest(ResponseCode.NO_EXIST_USER); // 사용자 없음
        }
        // 성공 응답 생성
        FindUserIdRes res = new FindUserIdRes(ResponseCode.OK.getCode(), userId);
        return res;
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
        response.setUserStatusMessage(userInfo.getStatusMessage());
        response.setProfilePic(userInfo.getProfilePic());

        // 본인 여부를 결과에 포함
        response.setMyInfo(isMyInfo);

        return response;
    }


    public ResponseResult userSignUp(UserSignUpReq p) {


        // 1. 이메일 형식 검증
        log.info("Validating email format for email: {}", p.getEmail());
        if (!p.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            log.error("Invalid email format: {}", p.getEmail());
            return ResponseResult.badRequest(ResponseCode.EMAIL_FORMAT_ERROR); // 이메일 형식 오류
        }

        //2. 이메일 중복 체크
        int checkEmailDuplicate = mapper.checkEmailDuplicate(p);
        if (checkEmailDuplicate > 0) {
            return ResponseResult.badRequest(ResponseCode.DUPLICATE_EMAIL); // 이메일 중복
        }

        // 3. 비밀번호 형식 검증
        log.info("Validating password format for userId: {}", p.getUserId());
        if (!PasswordValidator.isValidPassword(p.getPassword())) {
            return ResponseResult.badRequest(ResponseCode.PASSWORD_FORMAT_ERROR); // 비밀번호 형식 오류
        }

        // 4. 중복 체크 (닉네임, 유저 아이디)
        // 4-1. 닉네임 중복 체크
        int checkNicknameDuplicate = mapper.checkNicknameDuplicate(p);
        if (checkNicknameDuplicate > 0) {
            return ResponseResult.badRequest(ResponseCode.DUPLICATE_NICKNAME); // 닉네임 중복
        }
        // 4-2. 유저 아이디 중복 체크
        int checkUserIdDuplicate = mapper.checkUserIdDuplicate(p);
        if (checkUserIdDuplicate > 0) {
            return ResponseResult.badRequest(ResponseCode.DUPLICATE_ID); // 유저 아이디 중복
        }

        // 5. 비밀번호 확인 검증
        if (!p.getPassword().equals(p.getPasswordConfirm())) {
            return ResponseResult.badRequest(ResponseCode.PASSWORD_CHECK_ERROR); // 비밀번호 확인 불일치
        }

        // 6. 닉네임이 없을 경우 자동 생성
            String generatedNickname = NicknameGenerator.generateDefaultNickname();
            p.setNickname(generatedNickname);


        // 7. 비밀번호 암호화
        String password = BCrypt.hashpw(p.getPassword(), BCrypt.gensalt());
        p.setPassword(password);


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
        String password = BCrypt.hashpw(p.getPassword(), BCrypt.gensalt());
        p.setPassword(password);

        // 4. DB 업데이트
        int updatedPassword = mapper.updatePassword(p.getEmail(), password);
        if (updatedPassword > 0) {
            return new UserFindPasswordRes(ResponseCode.OK.getCode()); // 성공
        }

        // 5. 실패 시 처리
        return ResponseResult.badRequest(ResponseCode.FAIL); // 일반 실패 처리
    }





    @Transactional
    public ResponseResult updateUser(UserUpdateProfileReq p, MultipartFile pic) {
        // 1. 현재 사용자 정보 조회
        UserInfo currentInfo = mapper.getUserInfo(p.getTargetUserNo());
        if (currentInfo == null) {
            log.warn("User not found for targetUserNo: {}", p.getTargetUserNo());
            return ResponseResult.badRequest(ResponseCode.NOT_NULL); // 사용자 없음 (입력 오류)
        }

        currentInfo.setUserNo(p.getTargetUserNo());

        // 2. 권한 검증
        if (p.getSignedUserNo() != p.getTargetUserNo()) {
            log.error("Unauthorized profile update attempt. signedUserNo: {}, targetUserNo: {}", p.getSignedUserNo(), p.getTargetUserNo());
            return ResponseResult.badRequest(ResponseCode.NO_FORBIDDEN); // 권한 없음
        }

        // 3. 닉네임 중복 체크
        if (p.getNickname() != null && !p.getNickname().isEmpty()) {
            if (mapper.checkNicknameExists(p.getNickname())) {
                log.warn("Duplicate nickname found: {}", p.getNickname());
                return ResponseResult.badRequest(ResponseCode.DUPLICATE_NICKNAME); // 닉네임 중복
            }
            currentInfo.setNickname(p.getNickname());
        }

        // 4. 상태 메시지 업데이트
        if (p.getStatusMessage() != null && !p.getStatusMessage().isEmpty()) {
            currentInfo.setStatusMessage(p.getStatusMessage());
        }

        // 5. 프로필 사진 업데이트
        if (pic != null && !pic.isEmpty()) {
            try {
                String userFolder = String.format("user/%d", p.getTargetUserNo());
                myFileUtils.makeFolders(userFolder);

                // 기존 파일 이름 유지
                String originalFileName = currentInfo.getProfilePic();
                String filePath = String.format("%s/%s", userFolder, originalFileName);

                // 기존 파일 이름으로 덮어쓰기
                myFileUtils.transferTo(pic, filePath);

                // 기존 파일 이름 변경 없음
            } catch (IOException e) {
                log.error("Failed to overwrite profile picture for targetUserNo: {}", p.getTargetUserNo(), e);
                return ResponseResult.serverError(); // 파일 처리 실패
            }
        }


        // 6. 데이터베이스 업데이트
        int updatedRows = mapper.updUserProfile(currentInfo);
        if (updatedRows <= 0) {
            log.error("Failed to update user profile for targetUserNo: {}", p.getTargetUserNo());
            return ResponseResult.serverError(); // 업데이트 실패
        }

        // 7. 성공 응답
        log.info("User profile updated successfully for targetUserNo: {}", p.getTargetUserNo());
        return ResponseResult.success();
    }
}