package com.ScienceStation.app.controller.journal.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateJournalRequestDTO {

    private String headLine;

    private String keyPoints;

    private String journalAbstract;

    private Long branch_id;

    private Long magazine_id;
}
