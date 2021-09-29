package com.sparta.myblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sparta.myblog.dto.PostRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{id}", 31))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void 게시글수정인증() throws Exception {
        //given
        //when
        //error
        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/auth/{id}", 31)
                        .param("password", "12345"))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assertions.assertThat(result1.getResponse().getContentAsString()).isEqualTo("");
        //ok
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/auth/{id}", 31)
                        .param("password", "1111"))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("31"))
                .andExpect(MockMvcResultMatchers.jsonPath("title").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("writer").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("contents").exists());
    }

    @Test
    public void 게시글삭제() throws Exception {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title("delete_post")
                .writer("writer")
                .password("5252")
                .contents("get post test")
                .build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Long id = JsonPath.parse(contentAsString).read("id", Long.class);
        //when

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{id}", id)
                        .content("{\"password\": \"5252\"}")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("success"));
        //then
    }

    @Test
    public void xss방어테스트() throws Exception {
        //given
        PostRequestDto requestDto = PostRequestDto.builder()
                .title("xss test")
                .writer("xss")
                .password("xss")
                .contents("<script>alert(\"contents script 작성\")</script>")
                .build();
        String expected = "&lt;script&gt;alert&#40;&quot;contents script 작성&quot;&#41;&lt;/script&gt;";
        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(requestDto)))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("contents").value(expected))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Long id = JsonPath.parse(contentAsString).read("id", Long.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/auth/{id}", id)
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .param("password","<script>alert(\"param script 작성\")</script>"))
                //then
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("contents").doesNotHaveJsonPath());
    }
}