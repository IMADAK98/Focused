package com.ai.spring_ai.dto.design;

import com.ai.spring_ai.design.Stage;

import java.util.List;

public record CalibrationRequest(int confirmedBottleneckIndex, List<Stage> stages) {}
