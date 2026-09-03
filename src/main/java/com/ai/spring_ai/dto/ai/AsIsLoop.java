package com.ai.spring_ai.dto.ai;

import java.util.List;

public record AsIsLoop(List<AsIsStage> stages, int candidateBottleneckIndex, String primaryFrictionAnalysis) {}
