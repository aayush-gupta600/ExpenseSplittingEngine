package com.college.expensesplittingengine.Auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {

    private String username;
    private String token;

    public LoginResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }
}