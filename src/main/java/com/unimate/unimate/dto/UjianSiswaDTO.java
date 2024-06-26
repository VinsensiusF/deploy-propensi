package com.unimate.unimate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UjianSiswaDTO {
    @NotNull
    private Long ujianId;

    @NotNull
    private Long studentId;
}
