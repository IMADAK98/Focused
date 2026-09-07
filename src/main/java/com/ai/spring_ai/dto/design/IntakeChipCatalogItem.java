package com.ai.spring_ai.dto.design;

public record IntakeChipCatalogItem(
        String id,
        String label,
        String group,
        int sortOrder,
        String focusAreaId,
        String intakeKind) {}
