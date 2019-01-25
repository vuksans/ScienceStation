package com.ScienceStation.app.controller.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChooseReviewerRequestDTO {

    private Long reviewerId;

    private String deadLine;

    private String reason;

    private Long notificationId;
}
