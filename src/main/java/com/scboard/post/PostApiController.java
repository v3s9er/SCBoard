// 게시글 API 요청
package com.scboard.post;

import com.scboard.post.dto.PostForm;
import com.scboard.post.dto.PostPageResponse;
import com.scboard.post.dto.PostRequest;
import com.scboard.post.dto.PostResponse;
import com.scboard.user.User;
import com.scboard.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostApiController {
    private final PostService postService;
    private final UserService userService;

    public PostApiController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public PostPageResponse list(@RequestParam(name = "page", defaultValue = "0") int page) {
        return PostPageResponse.from(postService.list(page));
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable("id") Long id) {
        return postService.getResponse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@RequestBody PostRequest request, HttpSession session) {
        User user = userService.getLoginUser(session);
        if (user == null) return null;
        PostForm form = new PostForm();
        form.setTitle(request.title);
        form.setContent(request.content);
        Long id = postService.create(form, user, null);
        return postService.getResponse(id);
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable("id") Long id, @RequestBody PostRequest request, HttpSession session) {
        User user = userService.getLoginUser(session);
        PostForm form = new PostForm();
        form.setTitle(request.title);
        form.setContent(request.content);
        if (user != null) postService.update(id, form, null, user.getId());
        return postService.getResponse(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id, HttpSession session) {
        User user = userService.getLoginUser(session);
        postService.delete(id, user == null ? null : user.getId());
    }
}
