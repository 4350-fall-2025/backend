package com.softeng.backend.dto;

import com.softeng.backend.models.diary.Diary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class DiaryDTO {
    @NotNull @NotBlank
    private final String id;
    @NotNull @NotBlank
    private final Diary diary;

    public DiaryDTO() {
        id = "";
        diary = new Diary();
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "id", id,
                "contentType", diary.getContentType(),
                "contentBody", diary.getContentType(),
                "files", diary.getFiles(),
                "createTimestamp", diary.getCreateTimestamp()
        );
    }
}
