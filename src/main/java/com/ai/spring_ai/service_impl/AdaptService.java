package com.ai.spring_ai.service_impl;

import com.ai.spring_ai.dto.adapt.AdaptPreview;
import com.ai.spring_ai.model.FocusArea;
import com.ai.spring_ai.repository.InMemoryFocusAreaStore;
import com.ai.spring_ai.model.DailyCheckIn;
import com.ai.spring_ai.model.Run;
import org.springframework.stereotype.Service;

@Service
public class AdaptService {

    private final InMemoryFocusAreaStore store;

    public AdaptService(InMemoryFocusAreaStore store) {
        this.store = store;
    }

    public AdaptPreview preview(String focusAreaId) {
        FocusArea focusArea = store.require(focusAreaId);
        Run run = focusArea.run();
        if (run == null) {
            return new AdaptPreview(focusAreaId, false, "Day-7 Signal→Adapt is deferred until a Run exists.", 0, 0);
        }
        int logged = 0;
        int successful = 0;
        for (DailyCheckIn checkIn : run.dailyCheckIns()) {
            if (checkIn.success() != null) {
                logged++;
                if (Boolean.TRUE.equals(checkIn.success())) {
                    successful++;
                }
            }
        }
        return new AdaptPreview(
                focusAreaId,
                false,
                "Point 5 Day-7 evaluation is deferred. This stub only reports check-in counts.",
                logged,
                successful);
    }
}