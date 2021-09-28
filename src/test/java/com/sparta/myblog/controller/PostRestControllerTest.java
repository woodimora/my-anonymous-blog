package com.sparta.myblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.myblog.dto.PostRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class PostRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void 게시글작성() throws Exception {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title("get_posts")
                .writer("test1")
                .password("1111")
                .contents("get post test")
                .build();
        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value("get_posts"))
                .andExpect(MockMvcResultMatchers.jsonPath("writer").value("test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("contents").value("get post test"));


    }

    @Test
    public void 게시글page조회() throws Exception {
        //given
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .param("page", "0")
                        .param("display", "10"))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void 게시글상세조회() throws Exception {
        //given
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}",31))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}