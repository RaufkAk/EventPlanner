package com.yeditepe.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}/validate")
    Boolean validateUser(@PathVariable("id") Long userId,
                         @RequestHeader(value = "Authorization", required = false) String authorizationHeader);
}
