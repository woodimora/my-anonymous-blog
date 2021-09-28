package com.sparta.myblog.dto;

import com.sparta.myblog.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

//응답을 위한 Dto
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String writer;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int view_count;
    private int comments_count;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.writer = post.getWriter();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.view_count = post.getView_count();
        this.comments_count = post.getComments_count();
    }
}
