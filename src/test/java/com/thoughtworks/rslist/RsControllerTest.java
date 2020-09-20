package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.RsEventPo;
import com.thoughtworks.rslist.po.UserPo;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;
    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        UserPo saveUser = userRepository.save(UserPo.builder().name("xiaowng").age(18).phone("18888888888")
                .email("a@b.com").gender("male").voteNum(10).build());
        RsEventPo saveRsEvent = rsEventRepository.save(RsEventPo.builder().eventName("老社畜了").keyWord("娱乐")
                .userPo(saveUser).build());
    }

    @Test
    @Order(1)
    public void should_get_re_event_list() throws Exception {
        UserPo saveUser1 = userRepository.save(UserPo.builder().name("xiaowng2").age(18).phone("18888888888")
                .email("a@b.com").gender("male").voteNum(10).build());
        RsEventPo saveRsEvent1 = rsEventRepository.save(RsEventPo.builder().eventName("新社畜感觉很难受")
                .keyWord("娱乐1").userPo(saveUser1).build());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[0].eventName",is("老社畜了")))
                .andExpect(jsonPath("$[0].keyWord",is("娱乐")))
                .andExpect(jsonPath("$[0].userId",is(1)))
                .andExpect(jsonPath("$[1].eventName",is("新社畜感觉很难受")))
                .andExpect(jsonPath("$[1].keyWord",is("娱乐1")))
                .andExpect(jsonPath("$[1].userId",is(saveUser1.getId())))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void should_get_one_re_event() throws Exception {
        mockMvc.perform(get("/rs/2"))
                .andExpect(jsonPath("$.eventName", is("老社畜了")))
                .andExpect(jsonPath("$.keyWord", is("娱乐")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void should_get_rs_re_between() throws Exception {
        UserPo saveUser1 = userRepository.save(UserPo.builder().name("xiaowng2").age(18).phone("18888888888")
                .email("a@b.com").gender("male").voteNum(10).build());
        RsEventPo saveRsEvent1 = rsEventRepository.save(RsEventPo.builder().eventName("新社畜感觉很难受")
                .keyWord("娱乐1").userPo(saveUser1).build());
        RsEventPo saveRsEvent2 = rsEventRepository.save(RsEventPo.builder().eventName("涨工资了")
                .keyWord("经济").userPo(saveUser1).build());
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("老社畜了")))
                .andExpect(jsonPath("$[0].keyWord", is("娱乐")))
                .andExpect(jsonPath("$[1].eventName", is("新社畜感觉很难受")))
                .andExpect(jsonPath("$[1].keyWord", is("娱乐1")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=2&end=3"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("新社畜感觉很难受")))
                .andExpect(jsonPath("$[0].keyWord", is("娱乐1")))
                .andExpect(jsonPath("$[1].eventName", is("涨工资了")))
                .andExpect(jsonPath("$[1].keyWord", is("经济")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("老社畜了")))
                .andExpect(jsonPath("$[0].keyWord", is("娱乐")))
                .andExpect(jsonPath("$[1].eventName", is("新社畜感觉很难受")))
                .andExpect(jsonPath("$[1].keyWord", is("娱乐1")))
                .andExpect(jsonPath("$[2].eventName", is("涨工资了")))
                .andExpect(jsonPath("$[2].keyWord", is("经济")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void should_add_rs_event_when_user_exist() throws Exception {
        UserPo saveUser = userRepository.save(UserPo.builder().name("chenhui").age(13).phone("18888888888")
                .email("a@b.com").gender("female").voteNum(10).build());
        RsEvent rsEvent = new RsEvent("猪肉涨价了","经济",saveUser.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<RsEventPo> all = rsEventRepository.findAll();
        assertNotNull(all);
        assertEquals(1,all.size());
        assertEquals("猪肉涨价了",all.get(0).getEventName());
        assertEquals("经济",all.get(0).getKeyWord());
        assertEquals(saveUser.getId(),all.get(0).getUserPo().getId());
    }
    @Test
    @Order(5)
    public void should_add_rs_event_when_user_not_exist() throws Exception {
        RsEvent rsEvent = new RsEvent("猪肉涨价了","经济",100);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Order(6)
    public void should_modify_rs_event() throws Exception {
        RsEvent newRsEvent = new RsEvent("新社畜感觉很难受","娱乐1",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(newRsEvent);

        mockMvc.perform(patch("/rs/{rsEventId}",2).content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].eventName",is("新社畜感觉很难受")))
                .andExpect(jsonPath("$[0].keyWord",is("娱乐1")))
                .andExpect(jsonPath("$[0].userId",is(1)))
                .andExpect(status().isOk());


    }

    @Test
    @Order(7)
    public void should_modify_rs2_event() throws Exception {
        RsEvent newRsEvent = new RsEvent("新社畜感觉很难受",null,1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(newRsEvent);
        mockMvc.perform(patch("/rs/{rsEventId}",2).content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].eventName",is("新社畜感觉很难受")))
                .andExpect(jsonPath("$[0].keyWord",is("娱乐")))
                .andExpect(jsonPath("$[0].userId",is(1)))
                .andExpect(status().isOk());

    }

    @Test
    @Order(8)
    public void should_modify_rs3_event() throws Exception {
        RsEvent newRsEvent = new RsEvent(null,"娱乐1",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(newRsEvent);
        mockMvc.perform(patch("/rs/{rsEventId}",2).content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].eventName",is("老社畜了")))
                .andExpect(jsonPath("$[0].keyWord",is("娱乐1")))
                .andExpect(jsonPath("$[0].userId",is(1)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(9)
    public void should_delete_rs_event() throws Exception {
        UserPo saveUser1 = userRepository.save(UserPo.builder().name("xiaowng2").age(18).phone("18888888888")
                .email("a@b.com").gender("male").voteNum(10).build());
        RsEventPo saveRsEvent1 = rsEventRepository.save(RsEventPo.builder().eventName("新社畜感觉很难受")
                .keyWord("娱乐1").userPo(saveUser1).build());
        mockMvc.perform(delete("/rs/delete/{rsEventId}",saveRsEvent1.getId())).andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].eventName",is("老社畜了")))
                .andExpect(jsonPath("$[0].keyWord",is("娱乐")))
                .andExpect(jsonPath("$[0].userId",is(1)))
                .andExpect(status().isOk());


    }

    @Test
    @Order(10)
    public void eventName_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent(null,"娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Order(11)
    public void keyWord_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", null,1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Order(12)
    public void user_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", "娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /*@Test
    public void should_throw_method_argument_not_valid_exception() throws Exception {
        User user = new User("xyxiaxxxxxx","male",19,"a@b.com","18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了","经济",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }*/

    @Test
    @Order(13)
    public void should_throw_get_rs_event_between_not_valid_exception() throws Exception {
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
    }

    @Test
    @Order(14)
    public void should_vote_rs_event() throws Exception{
        UserPo saveUser = userRepository.save(UserPo.builder().name("xiaowng").age(18).phone("18888888888")
                .email("a@b.com").gender("male").voteNum(10).build());
        RsEventPo saveRsEvent = rsEventRepository.save(RsEventPo.builder().eventName("老社畜了").keyWord("娱乐")
                .userPo(saveUser).build());
        String timeNow =String.valueOf(LocalDateTime.now());
        Vote vote = Vote.builder().voteNum(5).userId(saveUser.getId()).time(timeNow).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(vote);

        mockMvc.perform(post("/rs/vote/{rsEventId}",saveRsEvent.getId())
                .content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        List<RsEventPo> rsEventAll = rsEventRepository.findAll();
        assertEquals(5,rsEventAll.get(0).getVoteNum());
        List<UserPo> userPoAll = userRepository.findAll();
        assertEquals(5,userPoAll.get(0).getVoteNum());

    }


}
