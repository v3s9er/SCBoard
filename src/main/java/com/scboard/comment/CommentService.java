// 댓글 저장, 수정, 삭제
package com.scboard.comment;

import com.scboard.post.Post;
import com.scboard.post.PostRepository;
import com.scboard.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Long add(Long postId, String content, User user) {
        Post post = postRepository.findById(postId).get();
        Comment comment = new Comment(content, post, user);
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public Long update(Long id, String content, Long userId) {
        Comment comment = commentRepository.findById(id).get();
        if (!isWriter(comment, userId)) {
            return comment.getPost().getId();
        }
        comment.update(content);
        return comment.getPost().getId();
    }

    @Transactional
    public Long delete(Long id, Long userId) {
        Comment comment = commentRepository.findById(id).get();
        Long postId = comment.getPost().getId();
        if (!isWriter(comment, userId)) {
            return postId;
        }
        commentRepository.delete(comment);
        return postId;
    }

    private boolean isWriter(Comment comment, Long userId) {
        return userId != null && comment.getAuthor().getId().equals(userId);
    }
}
