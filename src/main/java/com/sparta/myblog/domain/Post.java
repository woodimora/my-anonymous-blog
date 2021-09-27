package com.sparta.myblog.domain;

import com.sparta.myblog.dto.PostRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String writer;

    @Column
    private int view_count;

    @Column
    private int comments_count;

    public Post(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.password = postRequestDto.getPassword();
        this.contents = postRequestDto.getContents();
        this.writer = postRequestDto.getWriter();
        this.view_count = 0;
        this.comments_count = 0;
    }

    public void update(PostRequestDto postRequestDto){
        this.title = postRequestDto.getTitle();
        this.password = postRequestDto.getPassword();
        this.contents = postRequestDto.getContents();
    }

    public void updateViewCount(){
        this.view_count++;
    }
}
