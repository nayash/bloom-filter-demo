package com.outliers.bloomfilter.controllers;

import com.outliers.bloomfilter.dto.request.UserRegistrationRequest;
import com.outliers.bloomfilter.entities.User;
import com.outliers.bloomfilter.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("register")
    public Map<String, String> register(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) throws Exception {
        HashMap<String, String> response = new HashMap<>();
        userService.save(User.from(userRegistrationRequest));
        response.put("resp", "ok");
        return response;
    }

    @PostMapping("add")
    public Map<String, String> add(@RequestParam int n) {
        HashMap<String, String> response = new HashMap<>();
        userService.saveRandom(n);
        response.put("resp", "request accepted");
        return response;
    }

    @GetMapping("sim-check-naive")
    public Map<String, String> checkNaive(@RequestParam int n) {
        HashMap<String, String> response = new HashMap<>();
        userService.simulateNaiveChecks(n);
        response.put("resp", "done");
        return response;
    }

    @GetMapping("sim-check-bloom")
    public Map<String, String> checkBloom(@RequestParam int n) {
        HashMap<String, String> response = new HashMap<>();
        userService.simulateBloomChecks(n);
        response.put("resp", "done");
        return response;
    }
}
