package lk.yj.snw.service.impl;

import lk.yj.snw.controller.dto.DeploymentDTO;
import lk.yj.snw.controller.dto.UploadFileDTO;
import lk.yj.snw.exception.UploadManagementException;
import lk.yj.snw.model.PortPool;
import lk.yj.snw.model.UploadedFile;
import lk.yj.snw.model.VersionManagement;
import lk.yj.snw.repositary.FileUploadRepository;
import lk.yj.snw.service.DeletedPortPoolService;
import lk.yj.snw.service.FileUploadService;
import lk.yj.snw.service.VersionManagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class FileUploadServiceImpl implements FileUploadService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String FILE_COPY_ERROR = "Error in copying file to the destination";
    private static final String PATH_DELIMITER = "/";

    @Value("${dir.plugins}")
    private String dirPlugins;

    @Value("${start.port}")
    private int startPort;

    @Value("${end.port}")
    private int endPort;

    @Autowired
    private VersionManagingService versionManagingService;

    @Autowired
    private DeletedPortPoolService deletedPortPoolService;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Override
    public DeploymentDTO save(MultipartFile file, UploadFileDTO uploadFileDTO) throws IOException, UploadManagementException {
        Path path = Paths.get(dirPlugins);

        DeploymentDTO deploymentDTO = new DeploymentDTO();
        deploymentDTO.setUserId(uploadFileDTO.getUserId());
        deploymentDTO.setDockerName(uploadFileDTO.getDockerName());
        deploymentDTO.setServiceName(uploadFileDTO.getOrigin());

        if(!fileUploadRepository.existByOriginAndUserId(uploadFileDTO.getOrigin(),uploadFileDTO.getUserId())) {

            int portNumber = getPortNumber();
            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setOrigin(uploadFileDTO.getOrigin());
            uploadedFile.setUserId(uploadFileDTO.getUserId());
            uploadedFile.setServiceType(uploadFileDTO.getServiceType());
            uploadedFile.setPortNumber(portNumber);

            deploymentDTO.setPortNumber(portNumber);

            try {
                UploadedFile savedUploadedFile = fileUploadRepository.save(uploadedFile);

                int version = 1;
                String fileName = uploadFileDTO.getOrigin() + "-" + version + "-" +uploadFileDTO.getUserId();
                String fileNameWithExtension = fileName + ".zip";

                file.transferTo(new File(path + PATH_DELIMITER + fileNameWithExtension));

                if (new File((path + PATH_DELIMITER + fileNameWithExtension)).exists()) {

                    VersionManagement versionManagement = new VersionManagement();
                    versionManagement.setUploadedFile(savedUploadedFile);

                    versionManagement.setActive(true);
                    versionManagement.setVersion(version);
                    versionManagement.setDockerName(uploadFileDTO.getDockerName());
                    versionManagement.setServiceName(fileName);

                    return setDeploymentDetails(versionManagement, deploymentDTO);

                } else {
                    logger.error(FILE_COPY_ERROR);
                    throw new UploadManagementException(FILE_COPY_ERROR);
                }

            } catch (Exception e) {
                logger.error("Error in saving File : ", e);
                throw new UploadManagementException("Error in saving File : ");
            }

        } else {

            UploadedFile existingUploadedFile = fileUploadRepository.findOneByUserIdAndOrigin(uploadFileDTO.getUserId(), uploadFileDTO.getOrigin());

            int version = 1;
            Collection<VersionManagement> versionManagements = versionManagingService.findAllByUploadedFileId(existingUploadedFile.getId());
            if (versionManagements != null && !versionManagements.isEmpty()) {
                version += versionManagingService.maxVersionByUploadedFileId(existingUploadedFile.getId());
            }

            String fileName = uploadFileDTO.getOrigin() + "-" + version + "-" +uploadFileDTO.getUserId();
            String fileNameWithExtension = fileName + ".zip";

            file.transferTo(new File(path + PATH_DELIMITER + fileNameWithExtension));

            if(versionManagingService.isVersionExists(uploadFileDTO.getDockerName())){

                logger.info("Version name already exists.");
                throw new UploadManagementException("Version name already exists.");
            }

            if (new File((path + PATH_DELIMITER + fileNameWithExtension)).exists()) {

                versionManagingService.deActivateAllByUploadedFileId(existingUploadedFile.getId());

                VersionManagement versionManagement = new VersionManagement();
                versionManagement.setUploadedFile(existingUploadedFile);
                versionManagement.setActive(true);
                versionManagement.setVersion(version);
                versionManagement.setDockerName(uploadFileDTO.getDockerName());
                versionManagement.setServiceName(fileName);

                return setDeploymentDetails(versionManagement, deploymentDTO, existingUploadedFile.getPortNumber());

            } else {
                logger.error(FILE_COPY_ERROR);
                throw new UploadManagementException(FILE_COPY_ERROR);
            }
        }
    }

    @Override
    public UploadedFile findOneByOrigin(String origin)   {
       return fileUploadRepository.findOneByOrigin(origin);
    }

    @Override
    public int getTotalServiceOnBoardCountForUser(int userId) {
        return fileUploadRepository.getTotalExternalServiceCountForUser(userId);
    }

    @Override
    public boolean existByOriginAndUserId(String origin, int userId) {
        return fileUploadRepository.existByOriginAndUserId(origin, userId);
    }

    private int getPortNumber() throws UploadManagementException {

        int portNumber;

        Collection<PortPool> portPools = deletedPortPoolService.findAll();
        Integer maxPort = fileUploadRepository.maxPortNumber();
        if (portPools != null && !portPools.isEmpty()) {
            PortPool portPool = portPools.iterator().next();
            portNumber = portPool.getPortNumber();
            deletedPortPoolService.delete(portPool.getId());
        } else {
            if (maxPort != null && maxPort > 0) {
                if (maxPort < endPort) {
                    portNumber = maxPort + 1;
                } else {
                    throw new UploadManagementException("Available ports are over. Contact Admin");
                }
            } else {
                portNumber = startPort;
            }
        }
        return portNumber;
    }

    private DeploymentDTO setDeploymentDetails(VersionManagement versionManagement, DeploymentDTO deploymentDTO, int portNumber) throws UploadManagementException {

        try {
            VersionManagement savedVersionManagement = versionManagingService.create(versionManagement);
            deploymentDTO.setPortNumber(portNumber);
            deploymentDTO.setFileName(savedVersionManagement.getServiceName()+".zip");
            deploymentDTO.setVersionManagementId(savedVersionManagement.getId());
            return deploymentDTO;
        } catch (Exception e) {
            logger.error("Error in version managing ", e);
            throw new UploadManagementException("Error in version managing :");
        }
    }

    private DeploymentDTO setDeploymentDetails(VersionManagement versionManagement, DeploymentDTO deploymentDTO) throws UploadManagementException {

        try {
            VersionManagement savedVersionManagement = versionManagingService.create(versionManagement);
            deploymentDTO.setFileName(savedVersionManagement.getServiceName()+".zip");
            deploymentDTO.setVersionManagementId(savedVersionManagement.getId());
            return deploymentDTO;
        } catch (Exception e) {
            logger.error("Error in version managing first file upload : ",e);
            throw new UploadManagementException("Error in version managing first file upload :");
        }
    }

}
