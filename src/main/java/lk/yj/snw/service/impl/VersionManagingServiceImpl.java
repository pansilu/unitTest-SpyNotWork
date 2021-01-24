package lk.yj.snw.service.impl;

import lk.yj.snw.model.VersionManagement;
import lk.yj.snw.repositary.VersionManagingRepository;
import lk.yj.snw.service.VersionManagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class VersionManagingServiceImpl implements VersionManagingService {

    @Autowired
    private VersionManagingRepository versionManagingRepository;

    @Value("${dir.plugins}")
    private String dirPlugins;

    @Override
    public boolean isVersionExists(String dockerName){
        return versionManagingRepository.isVersionExists(dockerName);
    }

    @Override
    public VersionManagement create(VersionManagement versionManagement) {
        if (versionManagement.getId() != 0) {
            return null;
        }

        return versionManagingRepository.save(versionManagement);
    }

    @Override
    public Collection<VersionManagement> findAllByUploadedFileId(int id) {
        return versionManagingRepository.findAllByUploadedFileId(id);
    }

    @Override
    public int maxVersionByUploadedFileId(int id) {
        return versionManagingRepository.maxVersionByUploadedFileId(id);
    }

    @Override
    public void deActivateAllByUploadedFileId(int id) {
        versionManagingRepository.deActivateAllByUploadedFileId(id);
    }

}
