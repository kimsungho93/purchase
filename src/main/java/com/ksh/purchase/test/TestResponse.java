package com.ksh.purchase.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class TestResponse {
    private String name;
    private int age;
    private boolean isAdult;
}
