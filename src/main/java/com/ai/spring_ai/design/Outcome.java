package com.ai.spring_ai.design;

import java.util.List;

public record Outcome(String statement, List<String> successCriteria) {

    public Outcome {
        successCriteria = successCriteria == null ? List.of() : List.copyOf(successCriteria);
    }
}