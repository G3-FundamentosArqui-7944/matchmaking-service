package com.bodymatch.matchmaking.connection;

import com.bodymatch.matchmaking.shared.UserId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/connection-requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Connection Requests", description = "Athlete-Coach connection requests")
public class ConnectionRequestController {
    private final ConnectionRequestService connectionService;

    public ConnectionRequestController(ConnectionRequestService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping
    public ResponseEntity<ConnectionRequestResponse> request(@RequestBody CreateConnectionRequest request) {
        return connectionService.request(request)
                .map(ConnectionRequestResponse::from)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<ConnectionRequestResponse> respond(
            @PathVariable Long requestId, @RequestBody RespondConnectionRequest body) {
        return connectionService.respond(requestId, body)
                .map(ConnectionRequestResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<ConnectionRequestResponse>> byAthlete(@PathVariable Long athleteId) {
        var resources = connectionService.findByAthlete(new UserId(athleteId))
                .stream().map(ConnectionRequestResponse::from).toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<ConnectionRequestResponse>> byCoach(@PathVariable Long coachId) {
        var resources = connectionService.findByCoach(new UserId(coachId))
                .stream().map(ConnectionRequestResponse::from).toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/coach/{coachId}/clients")
    public ResponseEntity<List<ConnectionRequestResponse>> coachClients(@PathVariable Long coachId) {
        var resources = connectionService.findCoachClients(new UserId(coachId))
                .stream().map(ConnectionRequestResponse::from).toList();
        return ResponseEntity.ok(resources);
    }
}
