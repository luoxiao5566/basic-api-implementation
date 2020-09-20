package com.thoughtworks.rslist.api;


import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.po.VotePo;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.PageRanges;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VoteController {

    @Autowired
    VoteRepository voteRepository;
/*
    @GetMapping("/voteRecord")
    public ResponseEntity<List<Vote>> getVoteRecord(@RequestParam int userId,@RequestParam int rsEventId,
                                                    @RequestParam int pageIndex){
        Pageable pageable = PageRequest.of(pageIndex-1,5);
        return ResponseEntity.ok(
                voteRepository.findByUserIdAndRsEventId(userId,rsEventId,pageable).stream().map(
                        item -> Vote.builder().userId(item.getUserPo().getId())
                                .time(item.getLocalDateTime())
                                .rsEventId(item.getRsEventPo().getId())
                                .voteNum(item.getNum()).build()
                ).collect(Collectors.toList())
        );
    }



    @GetMapping("/vote")
    public ResponseEntity<List<Vote>> should_get_record(@RequestParam(required = false) Integer userId,
                                                        @RequestParam(required = false) Integer rsEventId,
                                                        @RequestParam(required = false) String startTime,
                                                        @RequestParam(required = false) String endTime,
                                                        @RequestParam int pageIndex) {
        List<VotePo> allPO = new ArrayList<>();
        List<Vote> all;
        Pageable pageable = PageRequest.of(pageIndex-1,5);
        if (startTime != null && endTime != null) {
            allPO = voteRepository.myLocalDateTime(startTime, endTime);
        } else {
            allPO = voteRepository.findByUserIdAndRsEventId(userId, rsEventId,pageable);
        }
        all = allPO.stream().map(
                item -> Vote.builder().userId(item.getUserPo().getId())
                        .time(item.getLocalDateTime())
                        .rsEventId(item.getRsEventPo().getId())
                        .voteNum(item.getNum()).build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }*/
}
