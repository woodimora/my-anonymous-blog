package com.sparta.myblog.service;

import com.sparta.myblog.domain.Post;
import com.sparta.myblog.domain.PostRepository;
import com.sparta.myblog.dto.PostRequestDto;
import com.sparta.myblog.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional // 메소드 동작이 SQL 쿼리문임을 선언합니다.
    public PostResponseDto update(Long id, PostRequestDto requestDto) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 아이디가 존재하지 않습니다.")
        );
        post.update(requestDto);
        return new PostResponseDto(post);
    }

    @Transactional // 메소드 동작이 SQL 쿼리문임을 선언합니다.
    public PostResponseDto updateViewCount(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("해당 아이디가 존재하지 않습니다.")
        );
        post.updateViewCount();
        return new PostResponseDto(post);
    }
}
