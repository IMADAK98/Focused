package com.ai.spring_ai.dto.ai;

import java.util.List;

public record Outcome(String statement, List<String> successCriteria) {}
