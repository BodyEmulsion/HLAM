package io.pelt.hlam.auth;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class AuthControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @Test
    void getJWT_whenNormalUserData_thenReturnsJWTAndStatus200() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.matchesRegex("^[\\w-]*\\.[\\w-]*\\.[\\w-]*$")));
    }

    @Test
    void getJWT_whenUserNotExists_thenStatus422() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "not-admin")
                        .param("password", "password"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("error-message", "User not found"));
    }

    @Test
    void getJWT_whenWrongPassword_thenStatus422() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "not-password"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("error-message", "Wrong password"));
    }

    @Test
    void getPublicKey() {
    }

    @Test
    void getGuestJWT() {
    }

    @Test
    void register() {
    }
}