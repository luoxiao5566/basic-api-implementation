package com.thoughtworks.rslist.api;


import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotVaildException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.util.*;

@RestController
public class RsController {
    private List<RsEvent> rsList = initRsEventList();
    private Map<String , User> userMap = initRsEventMap();

    private Map<String, User> initRsEventMap() {
        Map<String,User> rsEventsMap = new LinkedHashMap<>();
        User user = new User("xyxia","male",19,"a@b.com","18888888888");
        rsEventsMap.put(user.getName(),user);
        return rsEventsMap;
    }


    private List<RsEvent> initRsEventList() {
        List<RsEvent> rsEventsList = new ArrayList<>();
        User user = new User("xyxia","male",19,"a@b.com","18888888888");
        rsEventsList.add(new RsEvent("第一条事件", "无标签",user));
        rsEventsList.add(new RsEvent("第二条事件", "无标签",user));
        rsEventsList.add(new RsEvent("第三条事件", "无标签",user));
        return rsEventsList;
    }

    @GetMapping("/rs/{index}")
    public ResponseEntity getOneRsEvent(@PathVariable int index) {
        if (index <= 0 || index > rsList.size()){
            throw new RsEventNotVaildException("invalid index");
        }
        return ResponseEntity.ok(rsList.get(index - 1));
    }

    @GetMapping("/rs/list")
    public ResponseEntity getRsEventBetween(@RequestParam(required = false) Integer start
            , @RequestParam(required = false) Integer end) {
        if (start <=0 || end > rsList.size() || start > end){
            throw new RsEventNotVaildException("invalid request param");
        }
        if (start == null || end == null) {
            return ResponseEntity.ok(rsList);
        }
        return ResponseEntity.ok(rsList.subList(start - 1, end));
    }
    @GetMapping("/rs/map")
    public ResponseEntity detectUser(@RequestParam String name) {
        if (userMap.containsKey(name)){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @GetMapping("/users")
    public ResponseEntity getUser(){
        List<User> tempList = new ArrayList<>();
        Iterator iterator = userMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue() instanceof User){
                User user = (User) entry.getValue();
                tempList.add(user);
            }

        }
        return ResponseEntity.ok(tempList);
    }

    @PostMapping("/rs/event")
    public ResponseEntity addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
        String name = rsEvent.getUser().getName();
        if (userMap.containsKey(name)){
            User user = userMap.get(name);
            rsEvent.setUser(user);
            rsList.add(rsEvent);
        }
        userMap.put(name,rsEvent.getUser());
        rsList.add(rsEvent);
        int temp = rsList.size();
        String index = ""+temp;
        return ResponseEntity.created(null).header("index",index).build();
    }

    @PatchMapping("/rs/patch")
    public ResponseEntity modifyRsEvent(@RequestParam int index,@RequestBody RsEvent rsEvent) {
        RsEvent event =rsList.get(index-1);

        if (rsEvent.getKeyWord() != null) {
            event.setKeyWord(rsEvent.getKeyWord());
        }
        if (rsEvent.getEventName() != null) {
            event.setEventName(rsEvent.getEventName());

        }
        if (rsEvent.getUser() != null) {
            event.setUser(rsEvent.getUser());
        }
        return ResponseEntity.ok(null);

    }

    @DeleteMapping("/rs/delete/{index}")
    public ResponseEntity deleteRsEvent(@PathVariable int index){
        int size = rsList.size();
        if(index <= 0 || index>size){
            throw new RuntimeException("Subscript out of bounds");
        }
        rsList.remove(index-1);
        return ResponseEntity.ok(null);
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
