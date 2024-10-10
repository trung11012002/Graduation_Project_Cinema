package com.cinema.service;

import com.cinema.dto.request.AuthenticationResquest;
import com.cinema.dto.request.IntrospectRequest;
import com.cinema.dto.request.LogoutRequest;
import com.cinema.dto.response.AuthenticationResponse;
import com.cinema.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthenticationService {
    AuthenticationResponse authencticate(AuthenticationResquest resquest);

    IntrospectResponse introspect(IntrospectRequest request);
    void logout(LogoutRequest request);
//
//    AuthenticationResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException;
}
