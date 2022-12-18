package bluerock.controllers;

import dataworks.collections.KeyValuePair;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/connection")
public class ConnectionTestController
{
    @Autowired
    private ValueOperations<String, String> valueOperations;

    @PostMapping("/redis/put")
    public ServiceResponse<Boolean> redisPut(@RequestBody KeyValuePair<String, String> keyValuePair)
    {
        log.info("keyValuePair = " + keyValuePair);
        valueOperations.set(keyValuePair.getKey(), keyValuePair.getValue());
        return ServiceResponse.buildSuccessResponse(true);
    }

    @GetMapping("/redis/get")
    public ServiceResponse<String> redisGet(String key)
    {
        log.info("key = " + key);
        String value = valueOperations.get(key);
        return ServiceResponse.buildSuccessResponse(value);
    }
}
