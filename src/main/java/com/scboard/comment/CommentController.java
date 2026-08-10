// 댓글 화면 요청
package com.scboard.comment;

import com.scboard.comment.dto.CommentForm;
import com.scboard.user.User;
import com.scboard.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/posts/{postId}/comments")
    public String add(@PathVariable("postId") Long postId, @ModelAttribute CommentForm form, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        commentService.add(postId, form.getContent(), user);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/edit")
    public String edit(@PathVariable("id") Long id, @RequestParam("postId") Long postId,
                       @ModelAttribute CommentForm form, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user != null) commentService.update(id, form.getContent(), user.getId());
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        User user = userService.getLoginUser(session);
        Long postId = commentService.delete(id, user == null ? null : user.getId());
        return "redirect:/posts/" + postId;
    }
}
