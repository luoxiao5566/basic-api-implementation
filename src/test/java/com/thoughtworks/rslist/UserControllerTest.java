package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.OrderBy;

import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    public void should_register_user() throws Exception {
        User user = new User("xyxia","male",19,"a@b.com","18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/user"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].name",is("xyxia")))
                .andExpect(jsonPath("$[0].gender",is("male")))
                .andExpect(jsonPath("$[0].age",is(19)))
                .andExpect(jsonPath("$[0].email",is("a@b.com")))
                .andExpect(jsonPath("$[0].phone",is("18888888888")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public  void name_should_less_than_8() throws Exception {
        User user = new User("xyxiaxxxx","male",19,"a@b.com","18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Order(3)
    public  void age_should_between_18_and_100() throws Exception {
        User user = new User("xyxia","male",15,"a@b.com","18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public  void email_should_suit_format() throws Exception {
        User user = new User("xyxia","male",19,"ab.com","18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    public  void phone_should_suit_format() throws Exception {
        User user = new User("xyxia","male",19,"a@b.com","188888888881");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public  void should_throw_user_method_argument_not_valid_exception() throws Exception {
        User user = new User("xyxiaxxxxxx","male",19,"a@b.com","18888888888");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));

    }


}
