package com.bodymatch.matchmaking.coach;

import com.bodymatch.matchmaking.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/coaches", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Coaches", description = "Coach profile and matchmaking endpoints")
public class CoachController {
    private final CoachProfileService coachService;

    public CoachController(CoachProfileService coachService) {
        this.coachService = coachService;
    }

    @PostMapping
    public ResponseEntity<CoachProfileResponse> createProfile(@RequestBody CreateCoachProfileRequest request) {
        return coachService.create(request)
                .map(CoachProfileResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CoachProfileResponse> getByUserId(@PathVariable Long userId) {
        return coachService.findByUserId(new UserId(userId))
                .map(CoachProfileResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CoachProfileResponse>> search(
            @RequestParam(required = false) List<String> specialties,
            @RequestParam(required = false) Integer minYearsOfExperience,
            @RequestParam(required = false) BigDecimal maxHourlyRate,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(defaultValue = "true") boolean onlyAccepting) {
        Set<Specialty> specialtySet = specialties == null ? Set.of()
                : specialties.stream().map(Specialty::valueOf).collect(Collectors.toSet());
        var resources = coachService.search(specialtySet, minYearsOfExperience, maxHourlyRate, minRating, onlyAccepting)
                .stream().map(CoachProfileResponse::from).toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/recommendations/{athleteId}")
    public ResponseEntity<List<CoachProfileResponse>> recommendations(
            @PathVariable Long athleteId,
            @RequestParam(defaultValue = "10") int limit) {
        var resources = coachService.recommendFor(new UserId(athleteId), limit)
                .stream().map(CoachProfileResponse::from).toList();
        return ResponseEntity.ok(resources);
    }

    @PostMapping("/{coachId}/availability")
    public ResponseEntity<CoachProfileResponse> addAvailability(
            @PathVariable Long coachId, @RequestBody AddAvailabilityRequest request) {
        return coachService.addAvailability(new UserId(coachId), request)
                .map(CoachProfileResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
