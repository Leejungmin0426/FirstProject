package com.green.firstproject.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Schema(description = "유저 프로필 수정")
public class UserUpdateProfileReq {
    @Schema(name = "target_user_no", description = "타겟 유저 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long targetUserNo;

    @Schema(description = "유저 닉네임", example = "유저닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Schema(name = "status_message", title = "사용자 본인 상태 메시지", description = "사용자의 상태 메시지 (null 허용)", example = "Just do it!")
    private String statusMessage;

    @Schema(description = "유저 프로필 사진", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String pic;

    @Schema(description = "로그인된 유저 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long signedUserNo;
}
