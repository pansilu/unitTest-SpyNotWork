package lk.yj.snw.controller.dto;

public class DeploymentDTO {

    private int id;
    private int userId;
    private String dockerName;
    private int buildId;
    private String serviceName;
    private String fileName;
    private int portNumber;
    private int versionManagementId;
    private String queueId;

    public int getId() {
        return id;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public int getBuildId() {
        return buildId;
    }

    public void setDockerName(String dockerName) {
        this.dockerName = dockerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setBuildId(int buildId) {
        this.buildId = buildId;
    }

    public int getVersionManagementId() {
        return versionManagementId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDockerName() {
        return dockerName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setVersionManagementId(int versionManagementId) {
        this.versionManagementId = versionManagementId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }
}
