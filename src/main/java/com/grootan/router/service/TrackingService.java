package com.grootan.router.service;

import com.grootan.router.dto.response.TrackingResponse;

public interface TrackingService {

    TrackingResponse getMessageTracking(Long messageId);

}