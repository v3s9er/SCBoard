// 목록 화면용 글 정보
package com.scboard.post.dto;

import java.time.LocalDateTime;

public class PostSummaryView {
    public Long id;
    public String title;
    public String authorNickname;
    public LocalDateTime createdAt;
    public long commentCount;

    public PostSummaryView(Long id, String title, String authorNickname, LocalDateTime createdAt, long commentCount) {
        this.id = id;
        this.title = title;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
        this.commentCount = commentCount;
    }
}
