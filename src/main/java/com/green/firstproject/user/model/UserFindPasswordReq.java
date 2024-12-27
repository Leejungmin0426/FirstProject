package com.green.firstproject.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(title = "비밀번호 재설정 요청")
@ToString
public class UserFindPasswordReq {
    @Schema(description = "이메일 주소", example = "minmin0426@naver.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일 주소는 필수입니다.")
    private String email;


    @Schema(description = "유저 비밀번호", example = "lovelymin0426!", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",

            message = "비밀번호는 8자 이상 16자 미만이며, 영문 소문자, 숫자, 특수문자를 최소 1개 포함해야 합니다."
    )
    private String password;

    @Schema(description = "유저 비밀번호 확인", example = "lovelymin0426!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

}
