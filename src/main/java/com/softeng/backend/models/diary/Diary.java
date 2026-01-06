package com.softeng.backend.models.diary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.cloud.spring.data.firestore.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Document(collectionName = "diary")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Diary {
    @NotNull(message = "contentType is required") @NotBlank
    private String contentType; //implement enum later if needed
    @NotNull(message = "contentBody is required") @NotBlank
    private String contentBody;
    @NotNull(message = "file is required")
    private ArrayList<String> files;

    @NotNull(message = "createTimestamp is required")
    @PastOrPresent(message = "createTimestamp cannot be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date createTimestamp;
}
