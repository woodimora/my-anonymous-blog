package com.sparta.myblog.controller;

import com.sparta.myblog.domain.Post;
import com.sparta.myblog.domain.PostRepository;
import com.sparta.myblog.dto.PostRequestDto;
import com.sparta.myblog.dto.PostResponseDto;
import com.sparta.myblog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final PostRepository postRepository;
    private final PostService postService;

    //전체 게시글 조회. 생성날짜의 내림차순으로 정렬하여 전달.
    @GetMapping("/api/posts")
    public Page<PostResponseDto> getPostList(@RequestParam Integer page, @RequestParam Integer display) {
        PageRequest pageRequest = PageRequest.of(page, display, Sort.by("createdAt").descending());
        return postRepository.findAll(pageRequest).map(PostResponseDto::new);
    }

    //게시글 상세 조회
    @GetMapping("/api/posts/{id}")
    public PostResponseDto getPostById(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {

        //게시글 조회수를 위해 현재 쿠키에 담겨져 있는 게시글 id를 확인.
        String cookieName = "postId" + id;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    //게시글 정보 응답
                    return new PostResponseDto(postRepository.findById(id).orElseThrow(
                            () -> new IllegalStateException("아이디가 존재하지 않습니다.")
                    ));
                }
            }
        }

        //쿠키에 존재하지 않으면 조회수 증가 및 쿠키 생성
        PostResponseDto responseDto = postService.updateViewCount(id);  //조회수 증가
        Cookie createCookie = new Cookie(cookieName, "true");   //쿠키 생성
        createCookie.setMaxAge(60 * 60); // 쿠키 만료시간 1시간
        response.addCookie(createCookie);

        return responseDto;
    }

    //게시글 작성
    @PostMapping("/api/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        return new PostResponseDto(postRepository.save(new Post(requestDto)));
    }

    //게시글 수정
    @PostMapping("/api/posts/{id}")
    public PostResponseDto editPost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.update(id, requestDto);
    }

    //게시글 수정 인증 - 비밀번호 확인
    @GetMapping("/api/posts/auth/{id}")
    public PostResponseDto getEditAuth(@PathVariable Long id, @RequestParam String password) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("아이디가 존재하지 않습니다."));
        //비밀번호 확인
        if (password.equals(post.getPassword())) {
            return new PostResponseDto(post);
        }
        return null;
    }

    //게시물 삭제 - 비밀번호 확인
    @DeleteMapping("/api/posts/{id}")
    public String deletePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("아이디가 존재하지 않습니다.")
        );
        //비밀번호 확인
        if (requestDto.getPassword().equals(post.getPassword())) {
            postRepository.delete(post);
            return "success";
        } else {
            return "fail";
        }
    }
}

