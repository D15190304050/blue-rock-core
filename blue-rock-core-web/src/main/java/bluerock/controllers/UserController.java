package bluerock.controllers;

import bluerock.api.IUserService;
import bluerock.params.InitializeParam;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private IUserService userService;

    @GetMapping("/hello")
    public String hello()
    {
        log.info("Enter hello()...");
        return "Hello";
    }

    @PostMapping("/init")
    public ServiceResponse<Boolean> initialize(@RequestBody InitializeParam initializeParam)
    {
        return userService.initialize(initializeParam);
    }
}
