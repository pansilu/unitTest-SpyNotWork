package lk.yj.snw.controller.dto;

import lk.yj.snw.model.QueueDetails;

public class QueueDTO {

    private QueueDetails queueDetails;
    private UploadFileDTO uploadFileDTO;
    private  String fileStatus;

    public QueueDetails getQueueDetails() {
        return queueDetails;
    }

    public void setQueueDetails(QueueDetails queueDetails) {
        this.queueDetails = queueDetails;
    }

    public UploadFileDTO getUploadFileDTO() {
        return uploadFileDTO;
    }

    public void setUploadFileDTO(UploadFileDTO uploadFileDTO) {
        this.uploadFileDTO = uploadFileDTO;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }
}
