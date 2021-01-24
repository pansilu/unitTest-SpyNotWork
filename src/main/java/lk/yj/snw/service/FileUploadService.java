package lk.yj.snw.service;

import lk.yj.snw.controller.dto.DeploymentDTO;
import lk.yj.snw.controller.dto.UploadFileDTO;
import lk.yj.snw.exception.UploadManagementException;
import lk.yj.snw.model.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    DeploymentDTO save(MultipartFile file, UploadFileDTO uploadFileDTO) throws IOException, UploadManagementException;

    UploadedFile findOneByOrigin(String origin);

    int getTotalServiceOnBoardCountForUser(int userId);

    boolean existByOriginAndUserId(String origin, int userId);
}
