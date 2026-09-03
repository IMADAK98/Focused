package com.ai.spring_ai.ai;

import com.ai.spring_ai.dto.ai.Phase2Request;
import com.ai.spring_ai.dto.ai.Phase2Response;

public interface RedesignService {

    Phase2Response redesign(Phase2Request request);
}
