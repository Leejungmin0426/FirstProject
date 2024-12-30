package com.green.firstproject.user;

import com.green.firstproject.common.exception.ResponseCode;
import com.green.firstproject.common.exception.ResponseResult;
import com.green.firstproject.user.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("user")
public class UserController {

    private final UserService service;


    // 1. 유저 회원가입 (POST)
    @PostMapping("/sign-up")
    public ResponseResult userSignUp(@RequestBody UserSignUpReq p) {
        log.info("Received Request: {}", p);
        return service.userSignUp(p);
    }

    // 2. 유저 로그인 (POST)
    @PostMapping("/sign-in")
    public ResponseResult userSignIn(@RequestBody UserSignInReq p) {
        System.out.println("요청된 아이디: " + p.getUserId());
        System.out.println("요청된 비밀번호: " + p.getPassword());
        return service.userSignIn(p);
    }


    // 3. 사용자 정보 조회 (GET)
    @GetMapping("/{targetUserNo}")
    public ResponseResult selUserInfo(
            @PathVariable long targetUserNo,
            @RequestHeader long signedUserNo
    ) {
        UserInfoGetReq req = new UserInfoGetReq(targetUserNo, signedUserNo);
        return service.selUserInfo(req);
    }

    // 4. 비밀번호 재설정
    @PostMapping("/find-pw")
    public ResponseResult updatePassword(@RequestBody UserFindPasswordReq p) {
        return service.updatePassword(p);
    }



    @PatchMapping
    public ResponseResult updateProfile(
            @RequestPart UserUpdateProfileReq p, // JSON 데이터
            @RequestPart(required = false) MultipartFile pic // 파일 데이터
    ) {
        return service.updateUser(p, pic);
    }
}