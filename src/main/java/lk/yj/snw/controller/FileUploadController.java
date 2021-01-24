package lk.yj.snw.controller;

import lk.yj.snw.controller.dto.DeploymentDTO;
import lk.yj.snw.controller.dto.QueueDTO;
import lk.yj.snw.controller.dto.UploadFileDTO;
import lk.yj.snw.exception.UploadManagementException;
import lk.yj.snw.model.QueueDetails;
import lk.yj.snw.model.UploadedFile;
import lk.yj.snw.service.FileUploadService;
import lk.yj.snw.service.QueueManagementService;
import lk.yj.snw.util.ConfigGroup;
import lk.yj.snw.util.ConfigKey;
import lk.yj.snw.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/")
public class FileUploadController extends BaseController {

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    QueueManagementService queueManagementService;

    /**
     * @api {post} http://HostName:PORT/api/uploaded-files Upload a new Micro Service
     * @apiName uploadFile
     * @apiGroup Uploading
     *
     * @apiHeaderExample {json} Header-Example:
     *      {"Content-Type" : "application/json"}
     *
     * @apiParam {MultipartFile} file The ZIP file(Micro Service) to upload
     * @apiParam {String} origin The original name of the Micro Service
     * @apiParam {Integer} userId The user id of user
     * @apiParam {String} dockerName The Docker Image Name of the Micro Service
     *
     * @apiSuccessExample {json} Success-Response:
            File Copied Successfully
     */
    @PostMapping(value = "uploaded-files")
    @ResponseBody
    public ResponseEntity<QueueDTO> uploadFile(@RequestHeader(value = Constants.API_HEADER_USER_ID_KEY) String apiHeaderUserId,
                                               @RequestHeader(value = Constants.API_HEADER_TENANT_ID_KEY) String apiHeaderTenantId,
                                               @RequestParam("file") MultipartFile file, @RequestParam("origin") String origin,
                                               @RequestParam("userId") int userId, @RequestParam("dockerName") String dockerName, HttpServletRequest request) throws UploadManagementException {

        setLogIdentifier(request);

        logger.debug("apiHeaderTenantId: {}", apiHeaderTenantId);
        logger.debug("apiHeaderUserId: {}", apiHeaderUserId);

        QueueDTO queueDTO = new QueueDTO();
        UploadFileDTO uploadFileDTO = verifyService(file, origin, userId, dockerName);
        uploadFileDTO.setServiceType(Constants.SERVICE_TYPE_EXTERNAL);

        if (!fileUploadService.existByOriginAndUserId(origin, userId)) {
            Map<String, String> responseHeaderMap = checkAuthentication(request, ConfigGroup.MAX_SERVICE_ONBOARD_COUNT, ConfigKey.TOTAL);
            checkBasicConfiguration(ConfigGroup.MAX_SERVICE_ONBOARD_COUNT, Integer.parseInt(apiHeaderUserId), responseHeaderMap.get(Constants.CONFIG_VALUE));
        }
        
        try {

            DeploymentDTO deploymentDTO = fileUploadService.save(file, uploadFileDTO);
            logger.info("Saved Service zip file");
            String queueId = deployToJenkins(deploymentDTO);
            if(queueId != null) {
                deploymentDTO.setQueueId(queueId);
                QueueDetails queueDetails = queueManagementService.create(modelMapper.map(deploymentDTO, QueueDetails.class));
                queueDTO.setQueueDetails(queueDetails);
                queueDTO.setUploadFileDTO(uploadFileDTO);
                queueDTO.setFileStatus("File Copied Successfully");
            } else {
                throw new UploadManagementException("Queueing Error");
            }

        }
        catch (Exception e) {
            queueDTO.setFileStatus(e.getMessage());
            return new ResponseEntity<>(queueDTO,HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(queueDTO, HttpStatus.OK);
    }

    private UploadFileDTO verifyService(MultipartFile file, String origin, int userId, String dockerName)throws UploadManagementException{

        UploadFileDTO uploadFileDTO = new UploadFileDTO();
        uploadFileDTO.setOrigin(origin);
        uploadFileDTO.setUserId(userId);
        uploadFileDTO.setDockerName(dockerName);

        if(uploadFileDTO.getOrigin() == null || uploadFileDTO.getOrigin().equals("")) {
            throw new UploadManagementException("Service Name cannot be null.");
        }

        if(uploadFileDTO.getUserId() == 0) {
            throw new UploadManagementException("Missing User Id");
        }

        if(uploadFileDTO.getDockerName() == null || uploadFileDTO.getDockerName().equals("")) {
            throw new UploadManagementException("Docker Image Name cannot be null.");
        }

        if(!Objects.equals(file.getContentType(), Constants.FILE_EXTENSION) && !Objects.equals(file.getContentType(), Constants.FILE_EXTENSION_ZIP)
                && !Objects.requireNonNull(file.getContentType()).equals(Constants.FILE_EXTENSION_ZIP_X)) {
            throw new UploadManagementException("Please upload a ZIP file. Current type: {}" + file.getContentType());
        }

        if(file.isEmpty()) {
            throw new UploadManagementException("Service File is empty.");
        }

        logger.info("Service Name - {}", uploadFileDTO.getOrigin());
        UploadedFile savedFile = fileUploadService.findOneByOrigin(uploadFileDTO.getOrigin());
        if (savedFile != null && savedFile.getUserId() != uploadFileDTO.getUserId()){
            throw new UploadManagementException("Service Name Already Exists");
        }

    return uploadFileDTO;
    }
}
