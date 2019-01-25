package com.ScienceStation.app.controller.magazine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateMagazineRequestDTO {

    private Long issn;

    private String name;

    private boolean openAccess;

    private Long mainEditorId;
}
