package com.ScienceStation.app.controller.comment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCommentDTO {

    private String comment;

    private String hiddenComment;

    private Long notificationId;
}
