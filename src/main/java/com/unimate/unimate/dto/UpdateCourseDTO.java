package com.unimate.unimate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCourseDTO{
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Long kelasId;
}
