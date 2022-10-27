package com.jumkid.vehicle.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmartKeywordHandler {

    public String andSplit(String keyword) {
        String[] keywords = keyword.split(" ");
        return String.join(" AND ", keywords);
    }

}
