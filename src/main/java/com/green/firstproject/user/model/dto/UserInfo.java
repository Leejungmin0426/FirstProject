package com.green.firstproject.user.model.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "사용자 정보")
public class UserInfo {

    @Schema(title = "이메일", description = "사용자 이메일", example = "example@example.com")
    private String email;

    @Schema(title = "닉네임", description = "사용자 닉네임", example = "userNickname")
    private String nickname;

    @Schema(title = "상태 메시지", description = "사용자의 상태 메시지", example = "Hello World!")
    private String statusMessage;

    @Schema(title = "프로필 사진", description = "사용자 프로필 사진 URL", example = "profile.jpg")
    private String profilePic;

    @Schema(title = "유저 넘버", description = "유저 고유 넘버", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userNo;

}