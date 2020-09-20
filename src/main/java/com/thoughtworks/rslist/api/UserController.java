package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotVaildException;
import com.thoughtworks.rslist.po.UserPo;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    RsEventRepository rsEventRepository;

    @PostMapping("/user")
    public ResponseEntity adduser(@RequestBody @Valid User user){
        UserPo userPo = new UserPo();
        userPo.setName(user.getName());
        userPo.setGender(user.getGender());
        userPo.setAge(user.getAge());
        userPo.setEmail(user.getEmail());
        userPo.setPhone(user.getPhone());
        userPo.setVoteNum(user.getVoteNum());
<<<<<<< HEAD
        userRepository.save(userPo);
        return ResponseEntity.ok(null);
=======
        return ResponseEntity.ok(userRepository.save(userPo));
>>>>>>> jpa-2
    }

    @GetMapping("/user")
    public ResponseEntity getUser(@RequestParam Integer id) {
<<<<<<< HEAD

        Optional<UserPo> byId = userRepository.findById(id);
        if (!byId.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(byId.get());
=======
        Optional<UserPo> userPo = userRepository.findById(id);
        if (!userPo.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userPo.get());
>>>>>>> jpa-2
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
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
