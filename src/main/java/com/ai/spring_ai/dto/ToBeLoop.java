package com.ai.spring_ai.dto;

import java.util.List;

public record ToBeLoop(List<ToBeStage> stages, int bottleneckStageIndex, String coreStrategy) {}
