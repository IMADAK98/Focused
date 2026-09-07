package com.ai.spring_ai.mapper;

import com.ai.spring_ai.dto.ai.AsIsStage;
import com.ai.spring_ai.dto.ai.IntakeData;
import com.ai.spring_ai.model.AsIsLoop;
import com.ai.spring_ai.model.Intake;
import com.ai.spring_ai.model.Outcome;
import com.ai.spring_ai.model.Stage;
import com.ai.spring_ai.model.ToBeLoop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DraftMapper {

    IntakeData toIntakeData(Intake intake);

    @Mapping(target = "redesignIntervention", ignore = true)
    Stage toStageFromAsIs(AsIsStage stage);

    @Mapping(target = "currentFriction", ignore = true)
    Stage toStageFromToBe(com.ai.spring_ai.dto.ai.ToBeStage stage);

    AsIsStage toAsIsStage(Stage stage);

    List<AsIsStage> toAsIsStages(List<Stage> stages);

    @Mapping(target = "stages", source = "stages")
    AsIsLoop toAsIsLoop(com.ai.spring_ai.dto.ai.AsIsLoop draft);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "stages", source = "draft.stages")
    @Mapping(target = "bottleneckStageIndex", source = "draft.bottleneckStageIndex")
    @Mapping(target = "coreStrategy", source = "draft.coreStrategy")
    ToBeLoop toToBeLoop(String id, com.ai.spring_ai.dto.ai.ToBeLoop draft);

    Outcome toOutcome(com.ai.spring_ai.dto.ai.Outcome draft);
}
