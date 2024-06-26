package com.unimate.unimate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangeEmailDTO {
    @NotNull
    private String oldEmail;

    @NotNull
    private String newEmail;
}
