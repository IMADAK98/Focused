package com.ai.spring_ai.service;

import com.ai.spring_ai.dto.Phase1Request;
import com.ai.spring_ai.dto.Phase1Response;

public interface DiagnosticService {

    Phase1Response diagnose(Phase1Request request);
}
