package lk.yj.snw.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "version_management")
public class VersionManagement implements Serializable {

    private static final long serialVersionUID = -5679662916334924397L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String serviceName;

    @Column(unique = true)
    private String dockerName;

    private int version;

    private boolean active;

    @JoinColumn(name = "uploaded_file_id", referencedColumnName = "id")
    @ManyToOne//(fetch = FetchType.LAZY, optional = false)
    private UploadedFile uploadedFile;

    public VersionManagement(String serviceName, String dockerName, UploadedFile uploadedFile) {
        this.serviceName = serviceName;
        this.dockerName = dockerName;
        this.version = 1;
        this.active = true;
        this.uploadedFile = uploadedFile;
    }

    public VersionManagement() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDockerName() {
        return dockerName;
    }

    public void setDockerName(String dockerName) {
        this.dockerName = dockerName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

}
