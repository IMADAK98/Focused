package com.ai.spring_ai.model;

public record Bottleneck(Integer candidateIndex, String primaryFrictionAnalysis, Integer confirmedIndex) {

    public boolean confirmed() {
        return confirmedIndex != null;
    }
}