package com.green.firstproject.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSignInReq {

        @Schema(description = "유저 아이디용 이메일", example = "miniming0426@naver.com", requiredMode = Schema.RequiredMode.REQUIRED)
        private String email;

        @Schema(description = "유저 비밀번호", example = "mini0426!", requiredMode = Schema.RequiredMode.REQUIRED)
        private String password;
}
