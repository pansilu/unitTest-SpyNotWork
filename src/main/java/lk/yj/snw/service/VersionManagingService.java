package lk.yj.snw.service;

import lk.yj.snw.model.VersionManagement;

import java.util.Collection;

public interface VersionManagingService {

    boolean isVersionExists(String dockerName);

    VersionManagement create(VersionManagement versionManagement);

    Collection<VersionManagement> findAllByUploadedFileId(int id);

    int maxVersionByUploadedFileId(int id);

    void deActivateAllByUploadedFileId(int id);
}
