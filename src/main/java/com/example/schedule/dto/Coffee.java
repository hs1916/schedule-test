package com.example.schedule.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Coffee {

    private String name;
    private Integer price;

}
