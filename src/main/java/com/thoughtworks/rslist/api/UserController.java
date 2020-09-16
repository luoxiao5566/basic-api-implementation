package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.Error;
import com.thoughtworks.rslist.exception.RsEventNotVaildException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    List<User> userList = new ArrayList<>();

    @PostMapping("/user")
    public void adduser(@RequestBody @Valid User user){
        userList.add(user);
    }

    @GetMapping("/user")
    public List<User> getUserList() {
        return userList;
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
