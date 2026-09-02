package com.ai.spring_ai.dto;

import java.util.List;

public record Phase2Request(
        ConfirmedAsIsLoop confirmedAsIsLoop,
        int confirmedBottleneckIndex,
        List<String> directionChoices,
        List<String> successCriteriaChoices) {}
