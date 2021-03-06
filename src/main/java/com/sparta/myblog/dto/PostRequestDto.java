package com.sparta.myblog.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

//요청에 대한 Dto
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRequestDto {
    private String title;
    private String password;
    private String contents;
    private String writer;

    //Builder를 사용하여 객체 생성
    @Builder
    public PostRequestDto(String title, String password, String contents, String writer) {
        Assert.notNull(title, "title must not be null");
        Assert.notNull(password, "password must not be null");
        Assert.notNull(contents, "contents must not be null");
        Assert.notNull(writer, "writer must not be null");

        this.title = title;
        this.password = password;
        this.contents = contents;
        this.writer = writer;
    }
}
