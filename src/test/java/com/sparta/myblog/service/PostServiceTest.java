package com.sparta.myblog.service;

import com.sparta.myblog.domain.Post;
import com.sparta.myblog.domain.PostRepository;
import com.sparta.myblog.dto.PostRequestDto;
import com.sparta.myblog.dto.PostResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    PostRepository repository;

    @Test
    public void update() throws Exception {
        //given
        PostRequestDto requestDto1 = PostRequestDto.builder()
                .writer("member1")
                .password("1234")
                .title("제목")
                .contents("내용")
                .build();
        Post post = repository.save(new Post(requestDto1));
        //when
        PostRequestDto requestDto2 = PostRequestDto.builder()
                .writer("member1")
                .password("1234")
                .title("test Update")
                .contents("test Update")
                .build();

        PostResponseDto result = postService.update(post.getId(), requestDto2);

        Post findPost = repository.findById(result.getId()).orElseThrow(
                () -> new IllegalStateException("아이디가 존재하지 않습니다.")
        );
        //then

        Assertions.assertThat(findPost.getId()).isEqualTo(result.getId());
        Assertions.assertThat(findPost.getTitle()).isEqualTo("제목이다");
        Assertions.assertThat(findPost.getContents()).isEqualTo("내용이다");
    }
    
    @Test
    public void updateViewCount() throws Exception {
        //given
        PostRequestDto requestDto1 = PostRequestDto.builder()
                .writer("member1")
                .password("1234")
                .title("test view count")
                .contents("test view count")
                .build();
        Post post = repository.save(new Post(requestDto1));
        //when
        PostResponseDto result = postService.updateViewCount(post.getId());
        //then
        Assertions.assertThat(result.getView_count()).isEqualTo(1);
    }
}