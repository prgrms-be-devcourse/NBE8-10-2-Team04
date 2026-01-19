package com.back.domain.member.member.dto;

import com.back.domain.member.member.entity.Member;

public record MemberDto(
        long id,
        String loginid,
        String email
) {
    public MemberDto(long id, String loginid, String email) {
        this.id = id;
        this.loginid = loginid;
        this.email = email;
    }

    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getLoginid(),
                member.getEmail()
        );
    }
}