package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public long count() {
        return memberRepository.count();
    }

    public Member join(String loginid, String password, String email) {
        memberRepository
                .findByLoginid(loginid)
                .ifPresent(_member -> {
                    throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
                });
        Member member = new Member(loginid, password, email);
        return memberRepository.save(member);
    }
    public Optional<Member> findByLoginid(String loginid) {
        return memberRepository.findByLoginid(loginid);
    }
}
