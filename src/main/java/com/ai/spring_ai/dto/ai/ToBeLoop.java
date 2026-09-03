package com.ai.spring_ai.dto.ai;

import java.util.List;

public record ToBeLoop(List<ToBeStage> stages, int bottleneckStageIndex, String coreStrategy) {}
