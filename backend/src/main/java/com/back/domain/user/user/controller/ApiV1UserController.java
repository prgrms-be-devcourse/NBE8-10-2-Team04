package com.back.domain.user.user.controller;

import com.back.domain.user.user.dto.UserDto;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "ApiV1UserController", description = "API 회원 컨트롤러") //Swagger 문서 태그용
public class ApiV1UserController {
    private final UserService userService;
    private final Rq rq;

    //회원가입
    record UserJoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String loginId,
            @NotBlank
            @Size(min = 2, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String email
    ) {
    }

    @PostMapping("/signup")
    @Transactional
    @Operation(summary = "회원가입")
    public RsData<UserDto> join(
            @Valid @RequestBody UserJoinReqBody reqBody
    ) {

        User user = userService.join(
                reqBody.loginId(),
                reqBody.password(),
                reqBody.email()
        );

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(user.getLoginId()),
                new UserDto(user)
        );
    }

    //로그인
    record UserLoginReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String loginId,
            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }

    record UserLoginResBody(
            UserDto item,
            String apiKey,
            String accessToken
    ) {
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "로그인")
    public RsData<UserLoginResBody> login(
            @Valid @RequestBody UserLoginReqBody reqBody
    ) {
        User user = userService.findByLoginId(reqBody.loginId())
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 아이디입니다."));

        userService.checkPassword(
                user,
                reqBody.password()
        );

        String accessToken = userService.genAccessToken(user);

        rq.setCookie("apiKey", user.getApiKey());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(user.getLoginId()),
                new UserLoginResBody(
                        new UserDto(user),
                        user.getApiKey(),
                        accessToken
                )
        );
    }

    @DeleteMapping("/me")
    @Operation(summary = "탈퇴")
    public RsData<Void> deleteMe() {
        UserDto actor = rq.getActor();

        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        userService.deleteById(actor.id());

        rq.setCookie("accessToken", "");

        return new RsData<>(
                "200-1",
                "회원탈퇴가 완료되었습니다.",
                null);
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    public RsData<UserDto> me() {
//        User actor = userService
//                .findByLoginId(rq.getActor().id())
//                .get();
        UserDto actor = rq.getActor();

        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        // UserDto는 이미 필요한 정보를 포함하고 있으므로 그대로 반환
        return new RsData<>(
                "200-1",
//                "%s님의 정보입니다.".formatted(actor.id()),
//                new UserDto(actor)
                "%s님의 정보입니다.".formatted(actor.loginId()),
                actor
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public RsData<Void> logout() {
        rq.deleteCookie("apiKey");
        rq.deleteCookie("accessToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }
}
