// 업로드 파일을 폴더에 저장함
package com.scboard.file;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    @Value("${app.upload-dir}")
    private String uploadDir;

    public void save(MultipartFile file, String fileName) {
        File folder = new File(uploadDir).getAbsoluteFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try {
            file.transferTo(new File(folder, fileName).getAbsoluteFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource loadAsResource(String storedName) {
        try {
            Resource file = new UrlResource(new File(uploadDir, storedName).getAbsoluteFile().toURI());
            if (!file.exists()) {
                throw new RuntimeException("file not found");
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String storedName) {
        File file = new File(uploadDir, storedName).getAbsoluteFile();
        file.delete();
    }
}
