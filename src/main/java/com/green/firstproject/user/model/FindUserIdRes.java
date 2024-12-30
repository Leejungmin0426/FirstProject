package com.green.firstproject.user.model;

import com.green.firstproject.common.exception.ResponseResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserIdRes extends ResponseResult {

    private String userId;

    public FindUserIdRes(String code, String userId) {
        super(code); // ResponseResult의 생성자 호출
        this.userId = userId;
    }

}
