package com.ai.spring_ai.design;

import java.util.List;

public record ToBeLoop(String id, List<Stage> stages, int bottleneckStageIndex, String coreStrategy) {

    public ToBeLoop {
        stages = Stages.contiguous(stages);
    }
}