package com.grootan.router.controller;

import com.grootan.router.dto.response.TrackingResponse;
import com.grootan.router.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;


    @GetMapping("/{messageId}")
    public ResponseEntity<TrackingResponse> getTracking(
            @PathVariable Long messageId) {

        TrackingResponse response =
                trackingService.getMessageTracking(messageId);

        return ResponseEntity.ok(response);
    }
}