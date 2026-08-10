// 상세 화면용 글 정보
package com.scboard.post.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDetailView {
    public Long id;
    public String title;
    public String content;
    public Long authorId;
    public String authorNickname;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public List<CommentView> comments = new ArrayList<>();
    public List<AttachmentView> attachments = new ArrayList<>();

    public static class CommentView {
        public Long id;
        public String content;
        public Long authorId;
        public String authorNickname;
        public LocalDateTime createdAt;
    }

    public static class AttachmentView {
        public Long id;
        public String originalName;
        public Long size;
    }
}
