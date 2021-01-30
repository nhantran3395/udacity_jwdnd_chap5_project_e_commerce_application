package com.example.demo;

import com.example.demo.controllers.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestUserRequest {

    @Autowired
    private UserController userController;

    @Autowired
    private MockMvc mvc;

    @Test
    public void checkRegisterUserSuccess () throws Exception {
        mvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691"))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("bernard.harvey@gmail.com"))
            .andExpect(jsonPath("$.fullName").value("Bernard Harvey"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    public void checkRegisterUserFailed_UsernameExist () throws Exception {
        mvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv10691")));

        //another register request with the same username. this will cause Validation Exception
        mvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Johnson Harvey","bharv10691","bharv10691"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
        .andExpect(jsonPath("$.apierror.message").value("Username exists!"))
        .andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
        ;
    }

    @Test
    public void checkRegisterUserFailed_rePasswordNotMatch () throws Exception {
        mvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv10691","bharv106911"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.apierror.message").value("Passwords don't match!"))
                .andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
        ;
    }

    @Test
    public void checkRegisterUserFailed_usernameFailedEmailConstraint () throws Exception {
        mvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateUserRequestBody("bernard.harvey","Bernard Harvey","bharv10691","bharv106911"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.apierror.message").value("Validation error"))
                .andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.apierror.subErrors.[0].field").value("username"))
                .andExpect(jsonPath("$.apierror.subErrors.[0].message").value("must be a well-formed email address"));
        ;
    }

    @Test
    public void checkRegisterUserFailed_passwordFailedLengthConstraint () throws Exception {
        mvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getCreateUserRequestBody("bernard.harvey@gmail.com","Bernard Harvey","bharv","bharv"))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.apierror.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.apierror.message").value("Validation error"))
                .andExpect(jsonPath("$.apierror.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.apierror.subErrors.[0].field").value("password"))
                .andExpect(jsonPath("$.apierror.subErrors.[0].message").value("size must be between 8 and 20"));
        ;
    }

    private String getCreateUserRequestBody (String username, String fullName, String password, String rePassword){
        return "{\n" +
                "    \"username\":" + "\"" + username + "\"" + ",\n" +
                "    \"fullName\":" + "\"" + fullName + "\"" + ",\n" +
                "    \"password\":" + "\"" + password + "\"" + ",\n" +
                "    \"rePassword\":" + "\"" + rePassword + "\"" + "\n" +
                "}";
    }
}
