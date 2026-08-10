// 게시글 상세 API 응답
package com.scboard.post.dto;

import com.scboard.comment.dto.CommentResponse;
import com.scboard.file.dto.AttachmentResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostResponse {
    public Long id;
    public String title;
    public String content;
    public String author;
    public LocalDateTime createdAt;
    public List<CommentResponse> comments = new ArrayList<>();
    public List<AttachmentResponse> attachments = new ArrayList<>();

    public static PostResponse from(PostDetailView post) {
        PostResponse result = new PostResponse();
        result.id = post.id;
        result.title = post.title;
        result.content = post.content;
        result.author = post.authorNickname;
        result.createdAt = post.createdAt;
        for (PostDetailView.CommentView comment : post.comments) {
            CommentResponse item = new CommentResponse();
            item.id = comment.id;
            item.content = comment.content;
            item.author = comment.authorNickname;
            item.createdAt = comment.createdAt;
            result.comments.add(item);
        }
        for (PostDetailView.AttachmentView attachment : post.attachments) {
            AttachmentResponse item = new AttachmentResponse();
            item.id = attachment.id;
            item.originalName = attachment.originalName;
            item.size = attachment.size;
            item.downloadUrl = "/files/" + attachment.id;
            result.attachments.add(item);
        }
        return result;
    }
}
