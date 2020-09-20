package com.thoughtworks.rslist.api;


import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotVaildException;
import com.thoughtworks.rslist.po.RsEventPo;
import com.thoughtworks.rslist.po.UserPo;
import com.thoughtworks.rslist.po.VotePo;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RsController {

    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VoteRepository voteRepository;


    @GetMapping("/rs/{rsEventId}")
    public ResponseEntity getOneRsEvent(@PathVariable int rsEventId) {
        Optional<RsEventPo> rsEventPo = rsEventRepository.findById(rsEventId);
        if (!rsEventPo.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(rsEventPo.get());
    }

    @GetMapping("/rs/list")
    public ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start
            , @RequestParam(required = false) Integer end) {
        List<RsEventPo> rsEventall = rsEventRepository.findAll();
        List<RsEvent> rsEvents = rsEventall.stream().map(
                item -> RsEvent.builder().eventName(item.getEventName()).keyWord(item.getKeyWord())
                        .userId(item.getUserPo().getId()).build()
        ).collect(Collectors.toList());
        if (start <= 0 || end > rsEvents.size() || start > end){
            throw new RsEventNotVaildException("invalid request param");
        }
        if (start == null || end == null) {
            return ResponseEntity.ok(rsEvents);
        }

        return ResponseEntity.ok(rsEvents.subList(start - 1, end));
    }



    @PostMapping("/rs/event")
    public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
        Optional<UserPo> userPo = userRepository.findById(rsEvent.getUserId());
        if (!userPo.isPresent() ){
            return ResponseEntity.badRequest().build();
        }
        RsEventPo rsEventPo = RsEventPo.builder().keyWord(rsEvent.getKeyWord()).eventName(rsEvent.getEventName())
                .userPo(userPo.get()).build();
        RsEventPo saveRsEvent = rsEventRepository.save(rsEventPo);
        int temp = saveRsEvent.getId();
        String index = ""+temp;
        return ResponseEntity.created(null).header("index",index).build();
    }

    @PatchMapping("/rs/{rsEventId}")
    public ResponseEntity modifyRsEvent(@PathVariable int rsEventId,@RequestBody RsEvent rsEvent) {
        Optional<RsEventPo> rsEventPo = rsEventRepository.findById(rsEventId);
        if (!rsEventPo.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        int userPoId = rsEventPo.get().getUserPo().getId();
        if (userPoId != rsEvent.getUserId()){
            return ResponseEntity.badRequest().build();
        }
        RsEventPo rsEventPoNew = rsEventPo.get();
        if (rsEvent.getEventName() != null){
            rsEventPoNew.setEventName(rsEvent.getEventName());
        }
        if (rsEvent.getKeyWord() != null) {
           rsEventPoNew.setKeyWord(rsEvent.getKeyWord());
        }
        rsEventRepository.save(rsEventPoNew);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/rs/delete/{rsEventId}")
    public ResponseEntity deleteRsEvent(@PathVariable int rsEventId){
        Optional<RsEventPo> rsEventPo = rsEventRepository.findById(rsEventId);
        if(!rsEventPo.isPresent()){
            throw new RuntimeException("Subscript out of bounds");
        }
        rsEventRepository.deleteById(rsEventId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rs/vote/{rsEventId}")
    public ResponseEntity voteRsEvent(@PathVariable int rsEventId, @RequestBody Vote vote){
        vote.setRsEventId(rsEventId);
        Optional<RsEventPo> rsEventPo = rsEventRepository.findById(vote.getRsEventId());
        Optional<UserPo> userPo = userRepository.findById(vote.getUserId());
        if (!rsEventPo.isPresent() || !userPo.isPresent() || vote.getVoteNum() > userPo.get().getVoteNum()) {
            throw new RuntimeException();
        }

        VotePo votePo = VotePo.builder().localDateTime(vote.getTime())
                .num(vote.getVoteNum())
                .rsEventPo(rsEventPo.get())
                .userPo(userPo.get())
                .build();
        voteRepository.save(votePo);
        UserPo user = userPo.get();
        user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
        userRepository.save(user);
        RsEventPo rsEvent = rsEventPo.get();
        rsEvent.setVoteNum(rsEvent.getVoteNum()+vote.getVoteNum());
        rsEventRepository.save(rsEvent);
        return ResponseEntity.ok().build();

    }



    @ExceptionHandler({RsEventNotVaildException.class, MethodArgumentNotValidException.class})
    public ResponseEntity rsExceptionHandler(Exception e){
        String errorMessage;
        if (e instanceof MethodArgumentNotValidException){
            errorMessage = "invalid param";
        }else {
            errorMessage = e.getMessage();
        }
        Error error = new Error();
        error.setError(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }


}
