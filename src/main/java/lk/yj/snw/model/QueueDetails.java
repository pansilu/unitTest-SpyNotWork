package lk.yj.snw.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "queue_details")
public class QueueDetails implements Serializable {

    private static final long serialVersionUID = -5679632916334927387L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private String dockerName;
    private String serviceName;
    private String fileName;
    private int portNumber;
    private int queueId;
    @Column(nullable = false,columnDefinition = "int(11) default '0'")
    private int buildId;
    private int versionManagementId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDockerName() {
        return dockerName;
    }

    public void setDockerName(String dockerName) {
        this.dockerName = dockerName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public int getBuildId() {
        return buildId;
    }

    public void setBuildId(int buildId) {
        this.buildId = buildId;
    }

    public int getVersionManagementId() {
        return versionManagementId;
    }

    public void setVersionManagementId(int versionManagementId) {
        this.versionManagementId = versionManagementId;
    }
}
