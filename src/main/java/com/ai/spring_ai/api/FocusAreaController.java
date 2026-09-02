package com.ai.spring_ai.api;

import com.ai.spring_ai.design.AsIsLoop;
import com.ai.spring_ai.design.DesignService;
import com.ai.spring_ai.design.FocusArea;
import com.ai.spring_ai.design.FocusAreaCatalogItem;
import com.ai.spring_ai.design.Intake;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "design")
public class FocusAreaController {

    private final DesignService designService;

    public FocusAreaController(DesignService designService) {
        this.designService = designService;
    }

    @GetMapping("/focus-area-catalog")
    @Operation(summary = "FocusArea catalog for screen 1")
    public List<FocusAreaCatalogItem> catalog() {
        return designService.catalog();
    }

    @GetMapping("/focus-areas")
    @Operation(summary = "List FocusArea sessions for the stub User")
    public List<FocusArea> list() {
        return designService.list();
    }

    @PostMapping("/focus-areas")
    @Operation(summary = "Create a FocusArea session")
    public FocusArea create(@RequestBody(required = false) CreateFocusAreaRequest request) {
        CreateFocusAreaRequest body = request == null ? new CreateFocusAreaRequest(null, null, null) : request;
        return designService.create(body.catalogId(), body.name(), body.description());
    }

    @GetMapping("/focus-areas/{id}")
    @Operation(summary = "Get a FocusArea aggregate (deep-link payload)")
    public FocusArea get(@PathVariable String id) {
        return designService.get(id);
    }

    @GetMapping("/focus-areas/{id}/intake")
    @Operation(summary = "Get Intake")
    public Intake getIntake(@PathVariable String id) {
        return designService.get(id).intake();
    }

    @PutMapping("/focus-areas/{id}/intake")
    @Operation(summary = "Submit or update Intake")
    public FocusArea saveIntake(@PathVariable String id, @RequestBody Intake intake) {
        return designService.saveIntake(id, intake);
    }

    @GetMapping("/focus-areas/{id}/as-is")
    @Operation(summary = "Get AsIsLoop plus candidate/confirmed Bottleneck")
    public FocusArea getAsIs(@PathVariable String id) {
        return designService.get(id);
    }

    @PostMapping("/focus-areas/{id}/as-is/draft")
    @Operation(summary = "AI As-Is draft (candidate Bottleneck only, not confirmed)")
    public FocusArea draftAsIs(@PathVariable String id) {
        return designService.draftAsIs(id);
    }

    @PutMapping("/focus-areas/{id}/as-is")
    @Operation(summary = "Save edited As-Is Stages")
    public FocusArea updateAsIs(@PathVariable String id, @RequestBody AsIsLoop asIsLoop) {
        return designService.updateAsIs(id, asIsLoop);
    }

    @GetMapping("/focus-areas/{id}/calibration")
    @Operation(summary = "Get Human Calibration / Bottleneck state")
    public FocusArea getCalibration(@PathVariable String id) {
        return designService.get(id);
    }

    @PostMapping("/focus-areas/{id}/calibration")
    @Operation(summary = "Confirm or adjust the Bottleneck (human commit)")
    public FocusArea calibrate(@PathVariable String id, @RequestBody CalibrationRequest request) {
        return designService.calibrate(id, request.confirmedBottleneckIndex(), request.stages());
    }

    @GetMapping("/focus-areas/{id}/to-be")
    @Operation(summary = "Get ToBeLoop and Outcome")
    public FocusArea getToBe(@PathVariable String id) {
        return designService.get(id);
    }

    @PostMapping("/focus-areas/{id}/to-be/draft")
    @Operation(summary = "AI To-Be + Outcome draft (requires confirmed Bottleneck)")
    public FocusArea draftToBe(@PathVariable String id) {
        return designService.draftToBe(id);
    }

    @PutMapping("/focus-areas/{id}/to-be")
    @Operation(summary = "Save edited To-Be and/or Outcome")
    public FocusArea updateToBe(@PathVariable String id, @RequestBody ToBeUpdateRequest request) {
        return designService.updateToBe(id, request.toBeLoop(), request.outcome());
    }

    @PostMapping("/focus-areas/{id}/to-be/confirm")
    @Operation(summary = "Commit To-Be and open the 7-day Run")
    public FocusArea confirmToBe(@PathVariable String id) {
        return designService.confirmToBe(id);
    }
}