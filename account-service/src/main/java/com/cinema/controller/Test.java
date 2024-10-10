package com.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Test {
    static PasswordEncoder passwordEncoder =  new BCryptPasswordEncoder();
    public static void main(String[] args) {

        System.out.println(passwordEncoder.encode("12345678"));
    }
}
