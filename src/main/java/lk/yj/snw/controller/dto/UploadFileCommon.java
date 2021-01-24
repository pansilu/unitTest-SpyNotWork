package lk.yj.snw.controller.dto;

public class UploadFileCommon {

    private String origin;
    private String serviceType;
    private int userId;

    public String getOrigin() {
        return origin;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getUserId() {
        return userId;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
