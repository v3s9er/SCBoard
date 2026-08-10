// 게시글 목록 API 응답
package com.scboard.post.dto;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

public class PostPageResponse {
    public List<PostSummaryResponse> posts = new ArrayList<>();
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;
    public boolean hasNext;

    public static PostPageResponse from(Page<PostSummaryView> pageData) {
        PostPageResponse result = new PostPageResponse();
        result.page = pageData.getNumber();
        result.size = pageData.getSize();
        result.totalElements = pageData.getTotalElements();
        result.totalPages = pageData.getTotalPages();
        result.hasNext = pageData.hasNext();
        for (PostSummaryView post : pageData.getContent()) {
            result.posts.add(PostSummaryResponse.from(post));
        }
        return result;
    }
}
