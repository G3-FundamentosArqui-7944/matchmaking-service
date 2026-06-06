package com.bodymatch.matchmaking.session;

import com.bodymatch.matchmaking.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/training-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Training Sessions", description = "Coach-led training sessions")
public class TrainingSessionController {
    private final TrainingSessionService trainingSessionService;

    public TrainingSessionController(TrainingSessionService trainingSessionService) {
        this.trainingSessionService = trainingSessionService;
    }

    @PostMapping
    public ResponseEntity<TrainingSessionResponse> schedule(@RequestBody ScheduleTrainingSessionRequest request) {
        return trainingSessionService.schedule(request)
                .map(TrainingSessionResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/{sessionId}/complete")
    public ResponseEntity<TrainingSessionResponse> complete(
            @PathVariable Long sessionId, @RequestBody CompleteTrainingSessionRequest body) {
        return trainingSessionService.complete(sessionId, body.coachNotes())
                .map(TrainingSessionResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<TrainingSessionResponse> cancel(@PathVariable Long sessionId) {
        return trainingSessionService.cancel(sessionId)
                .map(TrainingSessionResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<TrainingSessionResponse>> byAthlete(@PathVariable Long athleteId) {
        var resources = trainingSessionService.findByAthlete(new UserId(athleteId))
                .stream().map(TrainingSessionResponse::from).toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<TrainingSessionResponse>> byCoach(@PathVariable Long coachId) {
        var resources = trainingSessionService.findByCoach(new UserId(coachId))
                .stream().map(TrainingSessionResponse::from).toList();
        return ResponseEntity.ok(resources);
    }
}
