package com.rest.backend_rest.dtos;

import com.rest.backend_rest.models.DiaryEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryEntryDTO {
    private String date;    // yyyy-MM-dd
    private String time;
    private String heading;
    private String entry;

    public DiaryEntryDTO(DiaryEntry e) {
        this.date = e.getTimeEntry().toLocalDate().toString();
        this.time = e.getTimeEntry().toLocalTime().toString();
        this.heading = e.getHeading();
        this.entry = e.getEntry();
    }
}
