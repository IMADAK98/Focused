package com.ai.spring_ai.dto.design;

import com.ai.spring_ai.model.Outcome;
import com.ai.spring_ai.model.ToBeLoop;

public record ToBeUpdateRequest(ToBeLoop toBeLoop, Outcome outcome) {}
