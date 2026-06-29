package com.grootan.router.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePreferenceRequest {

    @NotNull(message = "Email preference is required")
    private Boolean emailEnabled;

    @NotNull(message = "SMS preference is required")
    private Boolean smsEnabled;

}