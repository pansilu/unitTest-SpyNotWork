package lk.yj.snw.controller.dto;

public class UploadFileDTO extends UploadFileCommon{

    private String dockerName;

    public String getDockerName() {
        return dockerName;
    }

    public void setDockerName(String dockerName) {
        this.dockerName = dockerName;
    }

}
