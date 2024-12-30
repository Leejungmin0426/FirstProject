package com.green.firstproject.user.model;

import com.green.firstproject.common.exception.ResponseResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "사용자 로그인 응답")
public class UserSignInRes extends ResponseResult {

    @Schema(title = "첫 로그인 여부", description = "첫 로그인 여부를 나타내는 값")
    private boolean firstLogin;

    public UserSignInRes(String code, boolean firstLogin) {
        super(code); // ResponseResult의 생성자 호출
        this.firstLogin = firstLogin;
    }
}