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
@TestMethodOrder(MethodOrderer.class)
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
                .andExpect(jsonPath("$[1].userId",is(saveRsEvent1.getId())))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_one_re_event() throws Exception {

        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$.keyWord", is("无标签")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/2"))
                .andExpect(jsonPath("$.eventName", is("第二条事件")))
                .andExpect(jsonPath("$.keyWord", is("无标签")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/3"))
                .andExpect(jsonPath("$.eventName", is("第三条事件")))
                .andExpect(jsonPath("$.keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_get_rs_re_between() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=2&end=3"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventName", is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(jsonPath("$[2].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
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
    public void should_add_rs_event_when_user_not_exist() throws Exception {
        RsEvent rsEvent = new RsEvent("猪肉涨价了","经济",100);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void should_modify_rs_event() throws Exception {

        UserPo saveUser = userRepository.save(UserPo.builder().name("xiaowng").age(18).phone("18888888888")
                .email("a@b.com").gender("male").voteNum(10).build());
        RsEventPo saveRsEvent = rsEventRepository.save(RsEventPo.builder().eventName("老社畜了").keyWord("娱乐")
                .userPo(saveUser).build());
        RsEvent newRsEvent = new RsEvent("新社畜感觉很难受","娱乐1",saveUser.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(newRsEvent);

        mockMvc.perform(patch("/rs/{rsEventId}",saveRsEvent.getId()).content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        List<RsEventPo> allRsEvent = rsEventRepository.findAll();
        assertEquals(1,allRsEvent.size());
        assertEquals("新社畜感觉很难受",allRsEvent.get(0).getEventName());
        assertEquals("娱乐1",allRsEvent.get(0).getKeyWord());
        assertEquals(saveRsEvent.getId(),allRsEvent.get(0).getId());
        assertEquals(saveUser.getId(),allRsEvent.get(0).getUserPo().getId());

    }

    @Test
    public void should_modify_rs2_event() throws Exception {
        User user = new User("xyxia","male",19,"a@b.com","18888888888");
        RsEvent rsEvent = new RsEvent("第五条事件",null,1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/patch?index=1").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName", is("第五条事件")))
                .andExpect(jsonPath("$.keyWord", is("无标签")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_modify_rs3_event() throws Exception {
        User user = new User("xyxia","male",19,"a@b.com","18888888888");
        RsEvent rsEvent = new RsEvent(null,"无标签1",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(patch("/rs/patch?index=1").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/1"))
                .andExpect(jsonPath("$.eventName", is("第一条事件")))
                .andExpect(jsonPath("$.keyWord", is("无标签1")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_delete_rs_event() throws Exception {
        mockMvc.perform(delete("/rs/delete/1")).andExpect(status().isOk());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord", is("无标签")))
                .andExpect(jsonPath("$[1].eventName", is("第三条事件")))
                .andExpect(jsonPath("$[1].keyWord", is("无标签")))
                .andExpect(status().isOk());
    }
    @Test
    public void should_add_rs_event_and_user() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了","娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().stringValues("index","4"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(4)))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无标签")))
                .andExpect(jsonPath("$[0].user.name",is("xyxia")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无标签")))
                .andExpect(jsonPath("$[1].user.name",is("xyxia")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无标签")))
                .andExpect(jsonPath("$[2].user.name",is("xyxia")))
                .andExpect(jsonPath("$[3].eventName",is("都是老社畜了")))
                .andExpect(jsonPath("$[3].keyWord",is("娱乐")))
                .andExpect(jsonPath("$[3].user.name",is("xiaowang")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_add_rs_event_and_user_when_user_not_exist() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了","娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$",hasSize(4)))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无标签")))
                .andExpect(jsonPath("$[0].user.name",is("xyxia")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无标签")))
                .andExpect(jsonPath("$[1].user.name",is("xyxia")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无标签")))
                .andExpect(jsonPath("$[2].user.name",is("xyxia")))
                .andExpect(jsonPath("$[3].eventName",is("都是老社畜了")))
                .andExpect(jsonPath("$[3].keyWord",is("娱乐")))
                .andExpect(jsonPath("$[3].user.name",is("xiaowang")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rs/map?name=xiaowang"))
                .andExpect(content().string("true"))
                .andExpect(status().isOk());
    }

    @Test
    public void eventName_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent(null,"娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void keyWord_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", null,1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void user_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", "娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void name_should_less_than_8() throws Exception {
        User user = new User("xiaowangxxx","male",20,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", "娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void age_should_between_18_and_100() throws Exception {
        User user = new User("xiaowang","male",12,"c@b.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", "娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void email_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"cb.com","18888888889");
        RsEvent rsEvent = new RsEvent("都是老社畜了", "娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void phone_should_suit_format() throws Exception {
        User user = new User("xiaowang","male",20,"c@b.com","1888888888966");
        RsEvent rsEvent = new RsEvent("都是老社畜了", "娱乐",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void should_get_user_list() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].name",is("xyxia")))
                .andExpect(jsonPath("$[0].email",is("a@b.com")))
                .andExpect(status().isOk());
    }
    @Test
    public void should_throw_rs_event_not_valid_exception() throws Exception {
        mockMvc.perform(get("/rs/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid index")));
    }

    @Test
    public void should_throw_method_argument_not_valid_exception() throws Exception {
        User user = new User("xyxiaxxxxxx","male",19,"a@b.com","18888888888");
        RsEvent rsEvent = new RsEvent("猪肉涨价了","经济",1);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(rsEvent);
        mockMvc.perform(post("/rs/event").content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }

    @Test
    public void should_throw_get_rs_event_between_not_valid_exception() throws Exception {
        mockMvc.perform(get("/rs/list?start=0&end=2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));
    }

    @Test
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
