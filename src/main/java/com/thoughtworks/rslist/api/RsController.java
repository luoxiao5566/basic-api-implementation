package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class RsController {
    private List<RsEvent> rsList = initRsEventList();
    private Map<String , User> userMap = new LinkedHashMap<>();

    private List<RsEvent> initRsEventList() {
        List<RsEvent> rsEventsList = new ArrayList<>();
        User user = new User("xyxia","male",19,"a@b.com","18888888888");
        rsEventsList.add(new RsEvent("第一条事件", "无标签",user));
        rsEventsList.add(new RsEvent("第二条事件", "无标签",user));
        rsEventsList.add(new RsEvent("第三条事件", "无标签",user));
        return rsEventsList;
    }

    @GetMapping("/rs/{index}")
    public RsEvent getOneRsEvent(@PathVariable int index) {
        return rsList.get(index - 1);
    }

    @GetMapping("/rs/list")
    public List<RsEvent> getRsEventBetween(@RequestParam(required = false) Integer start
            , @RequestParam(required = false) Integer end) {
        if (start == null || end == null) {
            return rsList;
        }
        return rsList.subList(start - 1, end);
    }
    @GetMapping("/rs/map")
    public boolean getOneUser(@RequestParam String name) {
        if (userMap.containsKey(name)){
            return true;
        }
        return false;
    }


    @PostMapping("/rs/event")
    public void addRsEvent(@RequestBody @Valid RsEvent rsEvent) {
        String name = rsEvent.getUser().getName();
        if (userMap.containsKey(name)){
            rsList.add(rsEvent);
        }
        userMap.put(name,rsEvent.getUser());
        rsList.add(rsEvent);
    }

    @PatchMapping("/rs/patch")
    public void modifyRsEvent(@RequestParam int index,@RequestBody RsEvent rsEvent) {
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

    }

    @DeleteMapping("/rs/delete/{index}")
    public void deleteRsEvent(@PathVariable int index){
        int size = rsList.size();
        if(index <= 0 || index>size){
            throw new RuntimeException("Subscript out of bounds");
        }
        rsList.remove(index-1);
    }

}
