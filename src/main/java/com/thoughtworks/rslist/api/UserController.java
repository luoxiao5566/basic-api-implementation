package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotVaildException;
import com.thoughtworks.rslist.po.UserPo;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    List<User> userList = new ArrayList<>();
    @Autowired
    UserRepository userRepository;

    @PostMapping("/user")
    public void adduser(@RequestBody @Valid User user){
        UserPo userPo = new UserPo();
        userPo.setName(user.getName());
        userPo.setGender(user.getGender());
        userPo.setAge(user.getAge());
        userPo.setEmail(user.getEmail());
        userPo.setPhone(user.getPhone());
        userPo.setVoteNum(user.getVoteNum());
        userRepository.save(userPo);
    }

    @GetMapping("/user")
    public UserPo getUser(@RequestParam Integer id) {
        Optional<UserPo> byId = userRepository.findById(id);
        return byId.isPresent()?byId.get():null;
    }
    @DeleteMapping("/user/delete")
    public void deleteUser(@RequestParam Integer id){
        userRepository.deleteById(id);
    }

    @ExceptionHandler({RsEventNotVaildException.class, MethodArgumentNotValidException.class})
    public ResponseEntity rsExceptionHandler(Exception e){
        String errorMessage;
        if (e instanceof MethodArgumentNotValidException){
            errorMessage = "invalid user";
        }else {
            errorMessage = e.getMessage();
        }
        Error error = new Error();
        error.setError(errorMessage);
        return ResponseEntity.badRequest().body(error);
    }

}
