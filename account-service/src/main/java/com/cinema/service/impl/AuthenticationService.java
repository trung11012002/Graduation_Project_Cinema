package com.cinema.service.impl;

import com.cinema.dto.request.AuthenticationResquest;
import com.cinema.dto.request.IntrospectRequest;
import com.cinema.dto.request.LogoutRequest;
import com.cinema.dto.response.AuthenticationResponse;
import com.cinema.dto.response.IntrospectResponse;
import com.cinema.entity.User;
import com.cinema.exception.AppException;
import com.cinema.exception.ErrorCode;
import com.cinema.repository.UserRepository;
import com.cinema.service.IAuthenticationService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    BaseRedisService<String, String, String> redisServiceInvalidToken;


    @NonFinal
    @Value("${jwtSignerKey.access}")
    protected String SIGNER_KEY_ACCESS;

    @NonFinal
    @Value("${jwtSignerKey.refresh}")
    protected String SIGNER_KEY_REFRESH;

    @NonFinal
    @Value("${valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${refreshable-duration}")
    protected long REFRESH_DURATION;

    @NonFinal
    @Value("${timeDeleteTokenInvalid}")
    protected long TIME_DELETE_TOKEN_INVALID;

    public AuthenticationResponse authencticate(AuthenticationResquest resquest) {
        User user = userRepository.findByUsername(resquest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(resquest.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // role
//        List<RoleResponse> roles = roleService.getRoleByUserId(user.getId());
//        String listRole = "";
//        for (RoleResponse role : roles) {
//            listRole += (role.getCode() + " ");
//        }

        var tokenAccess = generateAccessToken(user);
        var tokenRefresh = generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .tokenAccess(tokenAccess)
                .tokenRefresh(tokenRefresh)
                .authenticated(true)
//                .role(listRole.trim())
                .role("")
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyTokenAccess(token);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyTokenAccess(request.getTokenAccess());
            String jit = signToken.getJWTClaimsSet().getJWTID();
            redisServiceInvalidToken.set(jit, "invalid_token");
            redisServiceInvalidToken.setTimeToLive(jit, TIME_DELETE_TOKEN_INVALID);
        } catch (AppException | JOSEException | ParseException exception) {
            log.info("Token already expired");
        }

        try {
            var signToken = verifyTokenRefresh(request.getTokenRefresh());
            String jit = signToken.getJWTClaimsSet().getJWTID();
            redisServiceInvalidToken.set(jit, "invalid_token");
            redisServiceInvalidToken.setTimeToLive(jit, TIME_DELETE_TOKEN_INVALID);
        } catch (AppException | JOSEException | ParseException exception) {
            log.info("Token already expired");
        }
    }
//
//    @Override
//    public AuthenticationResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException {
//        SignedJWT signedJWT = verifyTokenRefresh(refreshRequest.getTokenRefresh());
//        String username = signedJWT.getJWTClaimsSet().getSubject();
//        UserResponse user = (UserResponse) redisServiceUser.get("user_" + username);
//        if (user == null) throw new AppException(ErrorCode.USER_NOT_EXISTED);
//        var token = generateAccessToken(user);
//        return AuthenticationResponse.builder()
//                .tokenAccess(token)
//                .authenticated(true)
//                .build();
//    }

    private String generateAccessToken(User user) {
        // create access token
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("trung.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + VALID_DURATION * 1000))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY_ACCESS.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.out.println("Cannot create access token");
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(User user) {
        // create refresh token
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + REFRESH_DURATION * 1000))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY_REFRESH.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.out.println("Cannot create refresh token");
            throw new RuntimeException(e);
        }
    }

    public SignedJWT verifyTokenAccess(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY_ACCESS.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (!verified) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (expiryTime.before(new Date())) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (redisServiceInvalidToken.get(signedJWT.getJWTClaimsSet().getJWTID()) != null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    public SignedJWT verifyTokenRefresh(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY_REFRESH.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (!verified) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (expiryTime.before(new Date())) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (redisServiceInvalidToken.get(signedJWT.getJWTClaimsSet().getJWTID()) != null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
