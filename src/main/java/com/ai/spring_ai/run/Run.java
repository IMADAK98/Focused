package com.ai.spring_ai.run;

import java.time.LocalDate;
import java.util.List;

public record Run(String id, String toBeLoopId, LocalDate startDate, LocalDate endDate, List<DailyCheckIn> dailyCheckIns) {

    public Run {
        dailyCheckIns = dailyCheckIns == null ? List.of() : List.copyOf(dailyCheckIns);
    }

    public Run withCheckIns(List<DailyCheckIn> checkIns) {
        return new Run(id, toBeLoopId, startDate, endDate, checkIns);
    }
}