package com.back.domain.item.item.vo;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record CyclePeriod(int amount, Unit unit) {

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)([dmy])$");

    public static CyclePeriod from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("cycleDays는 필수입니다.");
        }

        Matcher matcher = PATTERN.matcher(raw.trim().toLowerCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("cycleDays 형식이 올바르지 않습니다. 예: 30d, 2m, 1y");
        }

        int amount = Integer.parseInt(matcher.group(1));
        Unit unit = Unit.from(matcher.group(2).charAt(0));

        if (amount <= 0) throw new IllegalArgumentException("cycleDays 값은 1 이상이어야 합니다.");

        return new CyclePeriod(amount, unit);
    }

    public LocalDate addTo(LocalDate startDate) {
        if (startDate == null) throw new IllegalArgumentException("startDate는 필수입니다.");

        return switch (unit) {
            case DAY -> startDate.plusDays(amount);
            case MONTH -> startDate.plusMonths(amount);
            case YEAR -> startDate.plusYears(amount);
        };
    }

    public enum Unit {
        DAY('d'), MONTH('m'), YEAR('y');

        private final char code;

        Unit(char code) {
            this.code = code;
        }

        static Unit from(char code) {
            return switch (code) {
                case 'd' -> DAY;
                case 'm' -> MONTH;
                case 'y' -> YEAR;
                default -> throw new IllegalArgumentException("지원하지 않는 단위입니다: " + code);
            };
        }
    }
}
