package com.ai.spring_ai.adapt;

public record AdaptPreview(
        String focusAreaId,
        boolean available,
        String reason,
        Integer loggedDays,
        Integer successfulDays) {}
