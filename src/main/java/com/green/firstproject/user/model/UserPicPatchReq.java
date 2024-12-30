package com.green.firstproject.user.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema
@Setter
@ToString
public class UserPicPatchReq {
    private MultipartFile pic;

    @JsonIgnore
    private long signedUserId;
    @JsonIgnore
    private String picName;

}
