// 첨부파일 요청
package com.scboard.file;

import com.scboard.post.PostService;
import com.scboard.user.User;
import com.scboard.user.UserService;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FileController {
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    private final PostService postService;
    private final UserService userService;

    public FileController(AttachmentRepository attachmentRepository, FileStorageService fileStorageService,
                          PostService postService, UserService userService) {
        this.attachmentRepository = attachmentRepository;
        this.fileStorageService = fileStorageService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        Attachment file = attachmentRepository.findById(id).get();
        Resource resource = fileStorageService.loadAsResource(file.getStoredName());
        String name = ContentDisposition.attachment().filename(file.getOriginalName(), StandardCharsets.UTF_8).build().toString();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, name)
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @PostMapping("/files/{id}/delete")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        User user = userService.getLoginUser(session);
        Long postId = postService.deleteAttachment(id, user == null ? null : user.getId());
        return "redirect:/posts/" + postId + "/edit";
    }
}
