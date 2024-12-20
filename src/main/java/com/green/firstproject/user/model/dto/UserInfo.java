package com.green.firstproject.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    @Schema(title = "사용자 본인 이메일", example = "miniming@naver.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(title = "사용자 본인 닉네임", example = "minimini", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    @Schema(title = "사용자 본인 상태 메시지", description = "사용자의 상태 메시지 (null 허용)", example = "Just do it!")
    private String userStatusMessage; // null 가능

    @Schema(title = "프로필 사진", description = "프로필 사진의 URL 또는 null 허용")
    private String profilePic; // null 가능

    @Schema(title = "유저넘버", description = "유저 고유 넘버", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userNo;

    @JsonIgnore
    private String password;

}
