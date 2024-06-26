package com.unimate.unimate.dto;

import com.unimate.unimate.enums.BlogType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Blob;
import java.util.ArrayList;

@Data
@Accessors(chain = true)
public class UpdateBlogDTO {
    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private String writer;

    //example : 5
    @NotNull
    private String readingTime;

    //INFORMASI_BEASISWA, PEKERJAAN_LUAR_NEGERI, TIPS_TRIK
    @NotNull
    private BlogType type;

    @NotNull
    private ArrayList<String> blogTag;

    private Blob image;
}
