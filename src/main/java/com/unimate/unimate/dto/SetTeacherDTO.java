package com.unimate.unimate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SetTeacherDTO {
    @NotNull
    private Long teacherId;
    @NotNull
    private Long kelasId;
}
