// 첨부파일 DB 조회
package com.scboard.file;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByPostIdOrderByIdAsc(Long postId);
}
