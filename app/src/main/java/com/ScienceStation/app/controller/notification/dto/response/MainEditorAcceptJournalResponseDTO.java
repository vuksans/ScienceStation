package com.ScienceStation.app.controller.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MainEditorAcceptJournalResponseDTO {

    private boolean success;

    private String reason;
}
