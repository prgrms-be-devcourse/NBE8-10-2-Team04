package com.back.domain.member.member.dto;

import com.back.domain.member.member.entity.Member;

public record MemberDto(
        long id,
        String loginId,
        String email
) {
    public MemberDto(long id, String loginId, String email) {
        this.id = id;
        this.loginId = loginId;
        this.email = email;
    }

    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getLoginId(),
                member.getEmail()
        );
    }
}