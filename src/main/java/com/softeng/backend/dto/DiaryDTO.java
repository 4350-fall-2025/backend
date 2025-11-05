package com.softeng.backend.dto;

import com.softeng.backend.models.diary.Diary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
                "contentBody", diary.getContentBody(),
                "files", diary.getFiles(),
                "createTimestamp", diary.getCreateTimestamp().toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }
}
