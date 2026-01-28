package com.back.domain.user.user.controller;

import com.back.domain.user.user.dto.*;
import com.back.domain.user.user.entity.User;
import com.back.domain.user.user.service.UserService;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "ApiV1UserController", description = "API 회원 컨트롤러") //Swagger 문서 태그용
public class ApiV1UserController {
    private final UserService userService;
    private final Rq rq;

    @PostMapping("/signup")
    @Transactional
    @Operation(summary = "회원가입")
    public RsData<UserDto> join(
            @Valid @RequestBody UserJoinRequest request,
            BindingResult bindingResult // [중요] 위치는 반드시 검증 객체 바로 뒤!
    ) {
        // 유효성 검사 실패 체크
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();

            // 실패 응답 리턴
            return new RsData<>(
                    "400",
                    errorMessage, //
                    null
            );
        }

        // 성공 시 로직 실행
        User user = userService.join(
                request.loginId(),
                request.password(),
                request.email()
        );

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(user.getLoginId()),
                new UserDto(user)
        );
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "로그인")
    public RsData<UserLoginResponse> login(
            @Valid @RequestBody UserLoginRequest reqBody
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
                new UserLoginResponse(
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

    @PatchMapping("/me")
    @Operation(summary = "프로필(이메일) 수정")
    public RsData<UserUpdateResponse> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        UserDto actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        User updatedUser = userService.updateProfile(actor.id(), request.email());

        // 새 토큰 발급 및 쿠키 갱신 (기존 토큰은 무효화됨)
        String newAccessToken = userService.genAccessToken(updatedUser);
        rq.setCookie("accessToken", newAccessToken);
        rq.setCookie("apiKey", updatedUser.getApiKey());

        return new RsData<>(
                "200-2",
                "회원정보가 수정되었습니다.",
                new UserUpdateResponse(updatedUser)
        );
    }

    @PatchMapping("/me/password")
    @Operation(summary = "비밀번호 변경")
    public RsData<UserUpdateResponse> changePassword(
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        UserDto actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        User updatedUser = userService.changePassword(
                actor.id(),
                request.currentPassword(),
                request.newPassword()
        );

        // 새 토큰 발급 및 쿠키 갱신 (기존 모든 토큰은 무효화됨)
        String newAccessToken = userService.genAccessToken(updatedUser);
        rq.setCookie("accessToken", newAccessToken);
        rq.setCookie("apiKey", updatedUser.getApiKey());

        return new RsData<>(
                "200-2",
                "비밀번호가 변경되었습니다.",
                new UserUpdateResponse(updatedUser)
        );
    }

    @PostMapping("/me/verify-password")
    @Operation(summary = "비밀번호 확인")
    public RsData<Void> verifyPassword(
            @Valid @RequestBody PasswordVerifyRequest request
    ) {
        UserDto actor = rq.getActor();
        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        User user = userService.findById(actor.id())
                .orElseThrow(() -> new ServiceException("404-1", "사용자를 찾을 수 없습니다."));

        userService.checkPassword(user, request.password());

        return new RsData<>(
                "200-1",
                "비밀번호가 확인되었습니다."
        );
    }
}
