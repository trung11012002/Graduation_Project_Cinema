package com.cinema.controller;

import com.cinema.dto.request.AuthenticationResquest;
import com.cinema.dto.request.IntrospectRequest;
import com.cinema.dto.request.LogoutRequest;
import com.cinema.dto.response.ApiResponse;
import com.cinema.dto.response.AuthenticationResponse;
import com.cinema.dto.response.IntrospectResponse;
import com.cinema.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    IAuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationResquest resquest) {
        var result = authenticationService.authencticate(resquest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .code(1000)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody @Valid IntrospectRequest resquest) {
        var result = authenticationService.introspect(resquest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .code(1000)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest resquest){
        authenticationService.logout(resquest);
        return ApiResponse.<Void>builder().code(1000).build();
    }

//    @PostMapping("/refresh")
//    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest resquest)
//            throws ParseException, JOSEException {
//        return ApiResponse.<AuthenticationResponse>builder()
//                .result(authenticationService.refreshToken(resquest))
//                .code(1000)
//                .build();
//    }
}
