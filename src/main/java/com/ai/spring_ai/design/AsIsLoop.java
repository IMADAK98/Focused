package com.ai.spring_ai.design;

import java.util.List;

public record AsIsLoop(List<Stage> stages, Integer candidateBottleneckIndex, String primaryFrictionAnalysis) {

    public AsIsLoop {
        stages = Stages.contiguous(stages);
    }
}