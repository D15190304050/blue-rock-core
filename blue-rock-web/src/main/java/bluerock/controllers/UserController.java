package bluerock.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController
{
    @GetMapping("/hello")
    public String hello()
    {
        log.info("Enter hello()...");
        return "Hello";
    }
}
