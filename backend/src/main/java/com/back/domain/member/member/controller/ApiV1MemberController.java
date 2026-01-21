package com.back.domain.member.member.controller;

import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
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
@Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러") //Swagger 문서 태그용
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    //회원가입
    record MemberJoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String loginid,
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
    public RsData<MemberDto> join(
            @Valid @RequestBody MemberJoinReqBody reqBody
    ) {

        Member member = memberService.join(
                reqBody.loginid(),
                reqBody.password(),
                reqBody.email()
        );

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getLoginid()),
                new MemberDto(member)
        );
    }

    //로그인
    record MemberLoginReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String loginid,
            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }

    record MemberLoginResBody(
            MemberDto item,
            String apiKey,
            String accessToken
    ) {
    }


    @PostMapping("/login")
    @Transactional(readOnly = true)
    public RsData<MemberLoginResBody> login(
            @Valid @RequestBody MemberLoginReqBody reqBody
    ) {
        Member member = memberService.findByLoginid(reqBody.loginid())
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 아이디입니다."));

        memberService.checkPassword(
                member,
                reqBody.password()
        );

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("apiKey", member.getApiKey());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getLoginid()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }

    @DeleteMapping("/me")
    public RsData<Void> deleteMe() {
        MemberDto actor = rq.getActor();

        if (actor == null) {
            throw new ServiceException("401-1", "로그인이 필요합니다.");
        }

        memberService.deleteById((int)actor.id()); // Todo: 몇몇은 long이고 몇몇은 int인게 통일이 안되어 있음. 수정 필요.

        rq.setCookie("accessToken", "");

        return new RsData<>(
                "200-1",
                "회원탈퇴가 완료되었습니다.",
                null
    @GetMapping("/me")
    public RsData<MemberDto> me() {
        Member actor = memberService
                .findByLoginid(rq.getActor().getLoginid())
                .get();

        return new RsData<>(
                "200-1",
                "%s님의 정보입니다.".formatted(actor.getLoginid()),
                new MemberDto(actor)
        );
    }

    @PostMapping("/logout")
    public RsData<Void> logout() {
        rq.deleteCookie("apiKey");
        rq.deleteCookie("accessToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }
}
