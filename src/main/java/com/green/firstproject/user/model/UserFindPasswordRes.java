package com.green.firstproject.user.model;

import com.green.firstproject.common.exception.ResponseResult;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFindPasswordRes extends ResponseResult {

    public UserFindPasswordRes(String code) {
        super(code); // ResponseResult의 생성자 호출
    }
}
