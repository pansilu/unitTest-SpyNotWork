package lk.yj.snw.model;

import lk.yj.snw.util.Constants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "uploaded_file", uniqueConstraints= @UniqueConstraint(columnNames={"origin", "userId"}))
public class UploadedFile implements Serializable {

    private static final long serialVersionUID = -5679632916334924397L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String origin;

    private String serviceType;

    private int userId;

    @Column(unique = true)
    private int portNumber;

    public UploadedFile() {
    }

    public UploadedFile(String origin, int userId, int portNumber) {
        this.origin = origin;
        this.userId = userId;
        this.portNumber = portNumber;
        this.serviceType = Constants.SERVICE_TYPE_EXTERNAL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}
