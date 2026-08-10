// 게시글 화면 요청
package com.scboard.post;

import com.scboard.comment.dto.CommentForm;
import com.scboard.post.dto.PostDetailView;
import com.scboard.post.dto.PostForm;
import com.scboard.user.User;
import com.scboard.user.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PostController {
    private final PostService postService;
    private final UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/posts")
    public String list(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        model.addAttribute("postPage", postService.list(page));
        return "posts/list";
    }

    @GetMapping("/posts/new")
    public String createForm(Model model) {
        model.addAttribute("postForm", new PostForm());
        model.addAttribute("edit", false);
        return "posts/form";
    }

    @PostMapping("/posts")
    public String create(@ModelAttribute PostForm form, @RequestParam(value = "files", required = false) List<MultipartFile> files,
                         HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        Long id = postService.create(form, user, files);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {
        PostDetailView post = postService.getDetail(id);
        model.addAttribute("post", post);
        model.addAttribute("currentUserId", session.getAttribute("loginUserId"));
        model.addAttribute("commentForm", new CommentForm());
        return "posts/detail";
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        PostDetailView post = postService.getDetail(id);
        PostForm form = new PostForm();
        form.setTitle(post.title);
        form.setContent(post.content);
        model.addAttribute("postForm", form);
        model.addAttribute("edit", true);
        model.addAttribute("post", post);
        model.addAttribute("currentUserId", session.getAttribute("loginUserId"));
        return "posts/form";
    }

    @PostMapping("/posts/{id}/edit")
    public String edit(@PathVariable("id") Long id, @ModelAttribute PostForm form,
                       @RequestParam(value = "files", required = false) List<MultipartFile> files, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) return "redirect:/login";
        postService.update(id, form, files, user.getId());
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        User user = userService.getLoginUser(session);
        postService.delete(id, user == null ? null : user.getId());
        return "redirect:/posts";
    }
}
