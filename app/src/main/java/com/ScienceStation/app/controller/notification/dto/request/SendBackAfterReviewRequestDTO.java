package com.ScienceStation.app.controller.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SendBackAfterReviewRequestDTO {

    private boolean changeType;

    private Long notificationId;

    private String deadLine;
}
