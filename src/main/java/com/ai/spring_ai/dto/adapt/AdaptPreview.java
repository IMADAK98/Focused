package com.ai.spring_ai.dto.adapt;

public record AdaptPreview(
        String focusAreaId,
        boolean available,
        String reason,
        Integer loggedDays,
        Integer successfulDays) {}
