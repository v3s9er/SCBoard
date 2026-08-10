// 게시글 목록 API 응답값
package com.scboard.post.dto;

import java.time.LocalDateTime;

public class PostSummaryResponse {
    public Long id;
    public String title;
    public String author;
    public LocalDateTime createdAt;
    public long commentCount;

    public static PostSummaryResponse from(PostSummaryView post) {
        PostSummaryResponse result = new PostSummaryResponse();
        result.id = post.id;
        result.title = post.title;
        result.author = post.authorNickname;
        result.createdAt = post.createdAt;
        result.commentCount = post.commentCount;
        return result;
    }
}
