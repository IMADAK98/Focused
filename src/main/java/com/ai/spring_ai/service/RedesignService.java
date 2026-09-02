package com.ai.spring_ai.service;

import com.ai.spring_ai.dto.Phase2Request;
import com.ai.spring_ai.dto.Phase2Response;

public interface RedesignService {

    Phase2Response redesign(Phase2Request request);
}
