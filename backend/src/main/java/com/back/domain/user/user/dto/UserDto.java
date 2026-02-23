package com.back.domain.user.user.dto;

import com.back.domain.user.user.entity.User;

import java.util.List;

public record UserDto(
        Long id,
        String loginId,
        String email
) {
    /**
     * Entity -> DTO 변환을 위한 정적 팩토리 메서드
     *
     * @param user 변환할 User 엔티티
     * @return UserDto
     */
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getLoginId(),
                user.getEmail()
        );
    }

    /**
     * 여러 Entity를 한번에 변환
     *
     * @param users 변환할 User 엔티티 리스트
     * @return UserDto 리스트
     */
    public static List<UserDto> fromList(List<User> users) {
        return users.stream()
                .map(UserDto::from)
                .toList();
    }
}