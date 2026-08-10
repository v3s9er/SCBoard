// 댓글 API 응답값
package com.scboard.comment.dto;

import java.time.LocalDateTime;

public class CommentResponse {
    public Long id;
    public String content;
    public String author;
    public LocalDateTime createdAt;
}
