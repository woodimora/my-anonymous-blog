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
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final PostRepository postRepository;
    private final PostService postService;

    @GetMapping("/api/posts")
    public Page<PostResponseDto> getPostList(@RequestParam Integer page, @RequestParam Integer display) {
        PageRequest pageRequest = PageRequest.of(page, display, Sort.by("createdAt").descending());
        return postRepository.findAll(pageRequest).map(PostResponseDto::new);
    }

    @PostMapping("/api/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        return new PostResponseDto(postRepository.save(new Post(requestDto)));
    }

    @GetMapping("/api/posts/{id}")
    public PostResponseDto getPostById(@CookieValue(value = "postId", defaultValue = "") String postIds, @PathVariable Long id, HttpServletResponse response) {
        ArrayList<Long> postIdList = new ArrayList<>();
        if (!postIds.equals("")) {
            String[] split = postIds.split("&");
            long temp = 0;
            for (String s : split) {
                long value = Long.parseLong(s);
                if(temp != value){
                    postIdList.add(value);
                    temp = value;
                }
            }
        }
        if (!findPostId(id, postIdList)) {
            PostResponseDto responseDto = postService.updateViewCount(id);
            postIdList.add(id);
            Collections.sort(postIdList);
            StringBuilder result = new StringBuilder(String.valueOf(postIdList.get(0)));
            for(int i = 1; i < postIdList.size(); i++){
                result.append("&").append(postIdList.get(i));
            }
            Cookie cookie = new Cookie("postId", result.toString());
            cookie.setMaxAge(24 * 60 * 60); // expires in 1 days
            response.addCookie(cookie);
            return responseDto;
        }
        else {
            return new PostResponseDto(postRepository.findById(id).orElseThrow(
                    () -> new IllegalStateException("아이디가 존재하지 않습니다.")
            ));
        }
    }

    private boolean findPostId(Long id, ArrayList<Long> postId) {
        int start = 0, end = postId.size() - 1;
        while (start <= end) {
            int mid = (start + end) / 2;
            Long curValue = postId.get(mid);
            if (id > curValue) {
                start = mid + 1;
            } else if (id < curValue) {
                end = mid - 1;
            } else {
                return true;
            }
        }
        return false;
    }

    @PutMapping("/api/posts/{id}")
    public PostResponseDto checkPassword(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("아이디가 존재하지 않습니다."));
        if (requestDto.getPassword().equals(post.getPassword())) {
            return new PostResponseDto(post);
        }
        return null;
    }

    @PostMapping("/api/posts/{id}")
    public PostResponseDto editPost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.update(id, requestDto);
    }

    @DeleteMapping("/api/posts/{id}")
    public String deletePost(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("아이디가 존재하지 않습니다.")
        );
        postRepository.delete(post);
        return "success";
    }
}

