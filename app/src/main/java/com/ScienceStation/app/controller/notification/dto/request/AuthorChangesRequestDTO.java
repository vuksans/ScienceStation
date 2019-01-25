package com.ScienceStation.app.controller.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorChangesRequestDTO {

    private Long notificationId;

    private String changes;

}
