package com.thoughtworks.rslist;


import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.po.RsEventPo;
import com.thoughtworks.rslist.po.UserPo;
import com.thoughtworks.rslist.po.VotePo;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    UserPo userPo;
    RsEventPo rsEventPo;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        voteRepository.deleteAll();;
        userPo = UserPo.builder().name("xiaowang").age(27).email("a@b.com").gender("male")
                .phone("18888888888").voteNum(10).build();
        userPo = userRepository.save(userPo);
        rsEventPo = RsEventPo.builder().eventName("涨工资了").keyWord("经济").voteNum(0).build();
        rsEventPo = rsEventRepository.save(rsEventPo);
    }

    @Test
    public void should_get_vote_record() throws Exception {
        String time = String.valueOf(LocalDateTime.now());
        VotePo votePo = VotePo.builder().userPo(userPo).rsEventPo(rsEventPo)
                .localDateTime(time).num(5).build();
        voteRepository.save(votePo);
        mockMvc.perform(get("/voteRecord").param("userId",String.valueOf(userPo.getId()))
                .param("rsEventId",String.valueOf(rsEventPo.getId())))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].userId",is(userPo.getId())))
                .andExpect(jsonPath("$[0].rsEventId",is(rsEventPo.getId())))
                .andExpect(jsonPath("$[0].voteNum",is(5)))
                .andExpect(status().isOk());
    }


}
