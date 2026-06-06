package com.bodymatch.matchmaking.athlete;

import com.bodymatch.matchmaking.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/athletes", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Athletes", description = "Athlete profile endpoints")
public class AthleteController {
    private final AthleteProfileService athleteService;

    public AthleteController(AthleteProfileService athleteService) {
        this.athleteService = athleteService;
    }

    @PostMapping
    public ResponseEntity<AthleteProfileResponse> createProfile(@RequestBody CreateAthleteProfileRequest request) {
        return athleteService.create(request)
                .map(AthleteProfileResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AthleteProfileResponse> getByUserId(@PathVariable Long userId) {
        return athleteService.findByUserId(new UserId(userId))
                .map(AthleteProfileResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
