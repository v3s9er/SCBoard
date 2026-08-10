// 글과 파일 저장, 수정
package com.scboard.post;

import com.scboard.comment.Comment;
import com.scboard.comment.CommentRepository;
import com.scboard.file.Attachment;
import com.scboard.file.AttachmentRepository;
import com.scboard.file.FileStorageService;
import com.scboard.post.dto.PostDetailView;
import com.scboard.post.dto.PostForm;
import com.scboard.post.dto.PostResponse;
import com.scboard.post.dto.PostSummaryView;
import com.scboard.user.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    public PostService(PostRepository postRepository, CommentRepository commentRepository,
                       AttachmentRepository attachmentRepository, FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Page<PostSummaryView> list(int page) {
        Page<Post> postPage = postRepository.findAll(PageRequest.of(page, 10));
        List<PostSummaryView> result = new ArrayList<>();
        for (Post post : postPage.getContent()) {
            result.add(new PostSummaryView(post.getId(), post.getTitle(), post.getAuthor().getNickname(),
                    post.getCreatedAt(), post.getComments().size()));
        }
        return new PageImpl<>(result, postPage.getPageable(), postPage.getTotalElements());
    }

    public Post get(Long id) {
        return postRepository.findById(id).get();
    }

    @Transactional
    public PostDetailView getDetail(Long id) {
        Post post = postRepository.findById(id).get();
        List<Comment> comments = commentRepository.findByPostIdOrderByIdAsc(id);
        List<Attachment> files = attachmentRepository.findAllByPostIdOrderByIdAsc(id);
        PostDetailView view = new PostDetailView();
        view.id = post.getId();
        view.title = post.getTitle();
        view.content = post.getContent();
        view.authorId = post.getAuthor().getId();
        view.authorNickname = post.getAuthor().getNickname();
        view.createdAt = post.getCreatedAt();
        view.updatedAt = post.getUpdatedAt();
        for (Comment comment : comments) {
            PostDetailView.CommentView item = new PostDetailView.CommentView();
            item.id = comment.getId();
            item.content = comment.getContent();
            item.authorId = comment.getAuthor().getId();
            item.authorNickname = comment.getAuthor().getNickname();
            item.createdAt = comment.getCreatedAt();
            view.comments.add(item);
        }
        for (Attachment file : files) {
            PostDetailView.AttachmentView item = new PostDetailView.AttachmentView();
            item.id = file.getId();
            item.originalName = file.getOriginalName();
            item.size = file.getSize();
            view.attachments.add(item);
        }
        return view;
    }

    public PostResponse getResponse(Long id) {
        return PostResponse.from(getDetail(id));
    }

    @Transactional
    public Long create(PostForm form, User user, List<MultipartFile> files) {
        Post post = new Post(form.getTitle(), form.getContent(), user);
        postRepository.save(post);
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String name = file.getOriginalFilename();
                    if (name == null) name = "file";
                    fileStorageService.save(file,name);
                    Attachment attachment = new Attachment(name, name, file.getContentType(), file.getSize());
                    post.addAttachment(attachment);
                }
            }
        }
        return post.getId();
    }

    @Transactional
    public void update(Long id, PostForm form, List<MultipartFile> files, Long userId) {
        Post post = postRepository.findById(id).get();
        if (!isWriter(post, userId)) {
            return;
        }
        post.update(form.getTitle(), form.getContent());
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String name = file.getOriginalFilename();
                    if (name == null) name = "file";
                    fileStorageService.save(file,name);
                    Attachment attachment = new Attachment(name, name, file.getContentType(), file.getSize());
                    post.addAttachment(attachment);
                }
            }
        }
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Post post = postRepository.findById(id).get();
        if (!isWriter(post, userId)) {
            return;
        }
        for (Attachment file : post.getAttachments()) {
            fileStorageService.delete(file.getStoredName());
        }
        postRepository.delete(post);
    }

    @Transactional
    public Long deleteAttachment(Long id, Long userId) {
        Attachment attachment = attachmentRepository.findById(id).get();
        Long postId = attachment.getPost().getId();
        if (!isWriter(attachment.getPost(), userId)) {
            return postId;
        }
        attachment.getPost().getAttachments().remove(attachment);
        fileStorageService.delete(attachment.getStoredName());
        return postId;
    }

    private boolean isWriter(Post post, Long userId) {
        return userId != null && post.getAuthor().getId().equals(userId);
    }
}
