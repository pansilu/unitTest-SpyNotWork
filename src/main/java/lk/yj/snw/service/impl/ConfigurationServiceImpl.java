package lk.yj.snw.service.impl;

import lk.yj.snw.exception.UploadManagementException;
import lk.yj.snw.service.ConfigurationService;
import lk.yj.snw.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    private final FileUploadService fileUploadService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ConfigurationServiceImpl(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @Override
    public void checkMaxExternalServicesPerUser(int userId, int maxLimit) throws UploadManagementException {

        int currentCount = fileUploadService.getTotalServiceOnBoardCountForUser(userId);

        if (currentCount >= maxLimit) {
            logger.warn("Max External Service create quota is exceeded for user: {}", userId);
            throw new UploadManagementException(403001, HttpStatus.FORBIDDEN, "Max External Service Create Quota is exceeded for your app. Please contact platform administration for more details.");
        }

        logger.debug("Check max external service config passed. current_count: {}, max_limit: {}, userId: {}", currentCount, maxLimit, userId);
    }
}
