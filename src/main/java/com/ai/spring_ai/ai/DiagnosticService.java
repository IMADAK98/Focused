package com.ai.spring_ai.ai;

import com.ai.spring_ai.dto.ai.Phase1Request;
import com.ai.spring_ai.dto.ai.Phase1Response;

public interface DiagnosticService {

    Phase1Response diagnose(Phase1Request request);
}
