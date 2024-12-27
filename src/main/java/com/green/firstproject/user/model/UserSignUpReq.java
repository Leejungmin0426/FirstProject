package com.green.firstproject.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "회원가입 요청")
public class UserSignUpReq {

    @Schema(description = "이메일 주소", example = "minmin0426@naver.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일 주소는 필수입니다.")
    private String email;

    @Schema(description = "유저 ID", example = "쩡미니미니", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "유저 ID는 필수입니다.")
    @Size(min = 5, max = 20, message = "유저 ID는 5자 이상 20자 이하로 입력해야 합니다.")
    private String userId;

    @Schema(description = "비밀번호", example = "Minmin0426!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",

            message = "비밀번호는 8자 이상 16자 미만이며, 영문 소문자, 숫자, 특수문자를 최소 1개 포함해야 합니다."
    )
    private String password;

    @Schema(description = "비밀번호 확인", example = "Minmin0426!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @Schema(description = "닉네임",  example = "미니공쥬", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;
    }