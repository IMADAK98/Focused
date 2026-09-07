package com.ai.spring_ai.service_impl;

import com.ai.spring_ai.dto.design.IntakeChipCatalogItem;
import com.ai.spring_ai.dto.ai.ConfirmedAsIsLoop;
import com.ai.spring_ai.dto.ai.Phase1Request;
import com.ai.spring_ai.dto.ai.Phase1Response;
import com.ai.spring_ai.dto.ai.Phase2Request;
import com.ai.spring_ai.dto.ai.Phase2Response;
import com.ai.spring_ai.mapper.DraftMapper;
import com.ai.spring_ai.model.AsIsLoop;
import com.ai.spring_ai.model.Bottleneck;
import com.ai.spring_ai.model.FocusArea;
import com.ai.spring_ai.model.FocusAreaCatalogItem;
import com.ai.spring_ai.model.Intake;
import com.ai.spring_ai.model.IntakeChip;
import com.ai.spring_ai.model.Outcome;
import com.ai.spring_ai.model.PipelineStatus;
import com.ai.spring_ai.model.Stage;
import com.ai.spring_ai.model.Stages;
import com.ai.spring_ai.model.ToBeLoop;
import com.ai.spring_ai.repository.InMemoryFocusAreaStore;
import com.ai.spring_ai.service.DiagnosticService;
import com.ai.spring_ai.service.RedesignService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DesignService {

    public static final List<FocusAreaCatalogItem> CATALOG = List.of(
            new FocusAreaCatalogItem("morning-energy", "Morning Energy", "Build sustainable morning momentum"),
            new FocusAreaCatalogItem("deep-work", "Deep Work", "Protect focused work sessions"),
            new FocusAreaCatalogItem("exercise", "Exercise", "Establish consistent movement"));

    private final InMemoryFocusAreaStore store;
    private final IdentityService identityService;
    private final DiagnosticService diagnosticService;
    private final RedesignService redesignService;
    private final RunService runService;
    private final DraftMapper draftMapper;

    public DesignService(
            InMemoryFocusAreaStore store,
            IdentityService identityService,
            DiagnosticService diagnosticService,
            RedesignService redesignService,
            RunService runService,
            DraftMapper draftMapper) {
        this.store = store;
        this.identityService = identityService;
        this.diagnosticService = diagnosticService;
        this.redesignService = redesignService;
        this.runService = runService;
        this.draftMapper = draftMapper;
    }

    public List<FocusAreaCatalogItem> catalog() {
        return CATALOG;
    }

    public List<IntakeChipCatalogItem> intakeChipCatalog(String focusAreaCatalogId, String kind) {
        return IntakeChipCatalog.find(focusAreaCatalogId, kind);
    }

    public List<FocusArea> list() {
        return store.findByUser(identityService.currentUser().id());
    }

    public FocusArea get(String id) {
        return store.require(id);
    }

    public FocusArea create(String catalogId, String name, String description) {
        FocusAreaCatalogItem catalogItem = resolveCatalog(catalogId, name, description);
        FocusArea created = new FocusArea(
                UUID.randomUUID().toString(),
                identityService.currentUser().id(),
                catalogItem.name(),
                catalogItem.description(),
                PipelineStatus.INTAKE,
                null,
                null,
                null,
                null,
                null,
                null);
        return store.save(created);
    }

    public FocusArea saveIntake(String focusAreaId, Intake intake) {
        FocusArea focusArea = store.require(focusAreaId);
        validateIntakeChips(focusArea, intake);
        PipelineStatus status = focusArea.status() == PipelineStatus.INTAKE ? PipelineStatus.AS_IS : focusArea.status();
        return store.save(focusArea.withIntake(intake).withStatus(status));
    }

    public FocusArea draftAsIs(String focusAreaId) {
        FocusArea focusArea = store.require(focusAreaId);
        requireIntake(focusArea);
        Phase1Response draft = diagnosticService.diagnose(toPhase1Request(focusArea));
        AsIsLoop asIsLoop = draftMapper.toAsIsLoop(draft.asIsLoop());
        Stages.requireAtLeastTwo(asIsLoop.stages());
        Stages.requireIndexInRange(asIsLoop.candidateBottleneckIndex(), asIsLoop.stages(), "candidateBottleneckIndex");
        // AI writes candidate only — confirmed stays null until Human Calibration.
        Bottleneck bottleneck = new Bottleneck(
                asIsLoop.candidateBottleneckIndex(),
                asIsLoop.primaryFrictionAnalysis(),
                null);
        PipelineStatus status = furthest(focusArea.status(), PipelineStatus.AS_IS);
        return store.save(focusArea.withAsIs(asIsLoop, bottleneck, status));
    }

    public FocusArea updateAsIs(String focusAreaId, AsIsLoop asIsLoop) {
        FocusArea focusArea = store.require(focusAreaId);
        Stages.requireAtLeastTwo(asIsLoop.stages());
        if (asIsLoop.candidateBottleneckIndex() != null) {
            Stages.requireIndexInRange(asIsLoop.candidateBottleneckIndex(), asIsLoop.stages(), "candidateBottleneckIndex");
        }
        Bottleneck existing = focusArea.bottleneck();
        Bottleneck bottleneck = new Bottleneck(
                asIsLoop.candidateBottleneckIndex(),
                asIsLoop.primaryFrictionAnalysis(),
                existing == null ? null : existing.confirmedIndex());
        PipelineStatus status = furthest(focusArea.status(), PipelineStatus.AS_IS);
        return store.save(focusArea.withAsIs(asIsLoop, bottleneck, status));
    }

    public FocusArea calibrate(String focusAreaId, int confirmedBottleneckIndex, List<Stage> editedStages) {
        FocusArea focusArea = store.require(focusAreaId);
        AsIsLoop current = focusArea.asIsLoop();
        if (current == null) {
            throw new IllegalStateException("As-Is must exist before Human Calibration");
        }
        AsIsLoop asIsLoop = editedStages == null || editedStages.isEmpty()
                ? current
                : new AsIsLoop(editedStages, current.candidateBottleneckIndex(), current.primaryFrictionAnalysis());
        Stages.requireAtLeastTwo(asIsLoop.stages());
        Stages.requireIndexInRange(confirmedBottleneckIndex, asIsLoop.stages(), "confirmedBottleneckIndex");
        Bottleneck bottleneck = new Bottleneck(
                asIsLoop.candidateBottleneckIndex(),
                asIsLoop.primaryFrictionAnalysis(),
                confirmedBottleneckIndex);
        return store.save(focusArea.withBottleneck(bottleneck, asIsLoop, furthest(focusArea.status(), PipelineStatus.CALIBRATION)));
    }

    public FocusArea draftToBe(String focusAreaId) {
        FocusArea focusArea = store.require(focusAreaId);
        requireConfirmedBottleneck(focusArea);
        Phase2Response draft = redesignService.redesign(toPhase2Request(focusArea));
        ToBeLoop toBeLoop = draftMapper.toToBeLoop(UUID.randomUUID().toString(), draft.toBeLoop());
        Outcome outcome = draftMapper.toOutcome(draft.outcome());
        Stages.requireAtLeastTwo(toBeLoop.stages());
        return store.save(focusArea.withToBe(toBeLoop, outcome, furthest(focusArea.status(), PipelineStatus.TO_BE)));
    }

    public FocusArea updateToBe(String focusAreaId, ToBeLoop toBeLoop, Outcome outcome) {
        FocusArea focusArea = store.require(focusAreaId);
        requireConfirmedBottleneck(focusArea);
        ToBeLoop nextToBe = toBeLoop == null ? focusArea.toBeLoop() : toBeLoop;
        Outcome nextOutcome = outcome == null ? focusArea.outcome() : outcome;
        if (nextToBe == null) {
            throw new IllegalArgumentException("ToBeLoop is required");
        }
        Stages.requireAtLeastTwo(nextToBe.stages());
        Stages.requireIndexInRange(nextToBe.bottleneckStageIndex(), nextToBe.stages(), "bottleneckStageIndex");
        String id = nextToBe.id() == null ? UUID.randomUUID().toString() : nextToBe.id();
        ToBeLoop stored = new ToBeLoop(id, nextToBe.stages(), nextToBe.bottleneckStageIndex(), nextToBe.coreStrategy());
        return store.save(focusArea.withToBe(stored, nextOutcome, furthest(focusArea.status(), PipelineStatus.TO_BE)));
    }

    public FocusArea confirmToBe(String focusAreaId) {
        FocusArea focusArea = store.require(focusAreaId);
        if (focusArea.toBeLoop() == null || focusArea.outcome() == null) {
            throw new IllegalStateException("To-Be and Outcome must exist before confirm");
        }
        requireConfirmedBottleneck(focusArea);
        if (focusArea.run() != null) {
            return store.save(focusArea.withStatus(PipelineStatus.RUN));
        }
        return store.save(focusArea.withRun(runService.createEmptyRun(focusArea.toBeLoop().id()), PipelineStatus.RUN));
    }

    private void validateIntakeChips(FocusArea focusArea, Intake intake) {
        if (intake.chips().isEmpty()) {
            return;
        }
        List<IntakeChip> selected = intake.chips().stream().filter(IntakeChip::selected).toList();
        if (selected.size() < 1) {
            throw new IllegalArgumentException("Select at least 1 intake chip (max 3)");
        }
        if (selected.size() > 3) {
            throw new IllegalArgumentException("Select at most 3 intake chips");
        }
        String focusAreaCatalogId = resolveFocusAreaCatalogId(focusArea);
        if (focusAreaCatalogId == null) {
            throw new IllegalArgumentException("Intake chips are not supported for this FocusArea catalog");
        }
        for (IntakeChip chip : selected) {
            if (chip.id() == null || chip.id().isBlank()) {
                throw new IllegalArgumentException("Each selected intake chip must have an id");
            }
            if (!IntakeChipCatalog.contains(focusAreaCatalogId, "whats_not_working", chip.id())) {
                throw new IllegalArgumentException("Unknown intake chip id: " + chip.id());
            }
        }
    }

    private static String resolveFocusAreaCatalogId(FocusArea focusArea) {
        return CATALOG.stream()
                .filter(item -> item.name().equals(focusArea.name())
                        && java.util.Objects.equals(item.description(), focusArea.description()))
                .map(FocusAreaCatalogItem::id)
                .findFirst()
                .orElse(null);
    }

    private FocusAreaCatalogItem resolveCatalog(String catalogId, String name, String description) {
        if (catalogId != null && !catalogId.isBlank()) {
            return CATALOG.stream()
                    .filter(item -> item.id().equals(catalogId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown FocusArea catalog id: " + catalogId));
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name or catalogId is required");
        }
        return new FocusAreaCatalogItem(null, name, description);
    }

    private static void requireIntake(FocusArea focusArea) {
        if (focusArea.intake() == null) {
            throw new IllegalStateException("Intake is required before an As-Is draft");
        }
    }

    private static void requireConfirmedBottleneck(FocusArea focusArea) {
        if (focusArea.bottleneck() == null || !focusArea.bottleneck().confirmed()) {
            throw new IllegalStateException("Human Calibration must confirm the Bottleneck before To-Be");
        }
        if (focusArea.asIsLoop() == null) {
            throw new IllegalStateException("As-Is must exist before To-Be");
        }
    }

    private Phase1Request toPhase1Request(FocusArea focusArea) {
        Intake intake = focusArea.intake();
        return new Phase1Request(
                focusArea.name(),
                intake.directionChoices(),
                intake.successCriteriaChoices(),
                draftMapper.toIntakeData(withHabitsFromChips(intake)));
    }

    private static Intake withHabitsFromChips(Intake intake) {
        if (!intake.selectedHabits().isEmpty() || intake.chips().isEmpty()) {
            return intake;
        }
        List<String> fromChips = intake.chips().stream()
                .filter(IntakeChip::selected)
                .map(IntakeChip::text)
                .toList();
        if (fromChips.isEmpty()) {
            return intake;
        }
        return new Intake(
                intake.directionChoices(),
                intake.successCriteriaChoices(),
                fromChips,
                intake.environmentalCues(),
                intake.failurePoints(),
                intake.customNotes(),
                intake.chips());
    }

    private Phase2Request toPhase2Request(FocusArea focusArea) {
        Intake intake = focusArea.intake();
        return new Phase2Request(
                new ConfirmedAsIsLoop(draftMapper.toAsIsStages(focusArea.asIsLoop().stages())),
                focusArea.bottleneck().confirmedIndex(),
                intake == null ? List.of() : intake.directionChoices(),
                intake == null ? List.of() : intake.successCriteriaChoices());
    }

    private static PipelineStatus furthest(PipelineStatus current, PipelineStatus next) {
        return next.ordinal() > current.ordinal() ? next : current;
    }
}
