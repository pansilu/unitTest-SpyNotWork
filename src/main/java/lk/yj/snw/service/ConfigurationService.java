package lk.yj.snw.service;

import lk.yj.snw.exception.UploadManagementException;

public interface ConfigurationService {

    void checkMaxExternalServicesPerUser(int userId, int maxLimit) throws UploadManagementException;
}
