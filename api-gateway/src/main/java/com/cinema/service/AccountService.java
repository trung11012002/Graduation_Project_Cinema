package com.cinema.service;

import com.cinema.dto.ApiResponse;
import com.cinema.dto.request.IntrospectRequest;
import com.cinema.dto.response.IntrospectResponse;
import com.cinema.repository.AccountClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder()
                        .token(token)
                .build());
    }
}
