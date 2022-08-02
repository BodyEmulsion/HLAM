package io.pelt.hlam.auth;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class AuthControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        RestDocumentationResultHandler document = document("{method-name}",
                preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document)
                .build();
    }

    @Test
    void getJWT_whenNormalUserData_thenReturnsJWTAndStatus200() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.matchesRegex("^[\\w-]*\\.[\\w-]*\\.[\\w-]*$")))
                .andDo(document("get-jwt", requestParameters(
                        parameterWithName("username").description("Имя пользователя. Пользователь с таким именем должеж существовать в базе данных"),
                        parameterWithName("password").description("Пароль. Пароль должен соответствовать паролю пользователя"))));
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
    void getPublicKey() throws Exception {
        mockMvc.perform(get("/get-public-key"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        //TODO: add keyspec validation
    }

    @Test
    void getGuestJWT() throws Exception {
        mockMvc.perform(post("/guest-login"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.matchesRegex("^[\\w-]*\\.[\\w-]*\\.[\\w-]*$")));
    }

    @Test
    void register() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "email@email.com")
                        .param("username", "unique-username")
                        .param("password", "StRoNgPaSsWoRd1"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.matchesRegex("^[\\w-]*\\.[\\w-]*\\.[\\w-]*$")))
                .andDo(document("register", requestParameters(
                        parameterWithName("username").description("Имя пользователя должно быть уникальным"),
                        parameterWithName("password").description("Пароль должен состоять из 8 и более символов, " +
                                "содержать заглавные и строчные буквы, а так же как минимум 1 цифру"),
                        parameterWithName("email").description("Адрес электронной почты, на которую придет " +
                                "сообщение для активации аккаунта")
                )));
    }
}