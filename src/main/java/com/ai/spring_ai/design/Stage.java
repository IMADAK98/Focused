package com.ai.spring_ai.design;

public record Stage(
        int position,
        String title,
        String cue,
        String environment,
        String currentFriction,
        String redesignIntervention) {}
