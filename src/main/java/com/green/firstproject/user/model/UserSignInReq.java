package com.green.firstproject.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSignInReq {

        @Schema(description = "유저 ID", example = "쩡미니미니", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "유저 ID는 필수입니다.")
        @Size(min = 5, max = 20, message = "유저 ID는 5자 이상 20자 이하로 입력해야 합니다.")
        private String userId;

        @Schema(description = "유저 비밀번호", example = "mini0426!", requiredMode = Schema.RequiredMode.REQUIRED)
        private String password;
}
