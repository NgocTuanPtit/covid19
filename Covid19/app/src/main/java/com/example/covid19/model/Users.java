package com.example.covid19.model;

import lombok.Data;

@Data
public class Users {
    private Integer id;
    private String fullName;
    private String password;
    private String email;
    private String address;
}
