// 댓글 API 요청
package com.scboard.comment;

import com.scboard.comment.dto.CommentRequest;
import com.scboard.user.User;
import com.scboard.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommentApiController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentApiController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Long add(@PathVariable("postId") Long postId, @RequestBody CommentRequest request, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) return null;
        return commentService.add(postId, request.content, user);
    }

    @PutMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") Long id, @RequestBody CommentRequest request, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user != null) commentService.update(id, request.content, user.getId());
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id, HttpSession session) {
        User user = userService.getLoginUser(session);
        commentService.delete(id, user == null ? null : user.getId());
    }
}
