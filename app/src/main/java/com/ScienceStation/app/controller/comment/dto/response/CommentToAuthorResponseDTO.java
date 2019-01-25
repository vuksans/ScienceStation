package com.ScienceStation.app.controller.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentToAuthorResponseDTO {

    private Long id;

    String comment;
}
