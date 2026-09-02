package com.ai.spring_ai.design;

public record Bottleneck(Integer candidateIndex, String primaryFrictionAnalysis, Integer confirmedIndex) {

    public boolean confirmed() {
        return confirmedIndex != null;
    }
}