package com.ai.spring_ai.api;

import com.ai.spring_ai.design.Outcome;
import com.ai.spring_ai.design.ToBeLoop;

public record ToBeUpdateRequest(ToBeLoop toBeLoop, Outcome outcome) {}
