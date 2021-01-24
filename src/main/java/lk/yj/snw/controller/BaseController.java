package lk.yj.snw.controller;

import lk.yj.snw.service.ConfigurationService;
import lk.yj.snw.util.ConfigGroup;
import lk.yj.snw.util.ConfigKey;
import lk.yj.snw.controller.dto.DeploymentDTO;
import lk.yj.snw.exception.ErrorResponse;
import lk.yj.snw.exception.UploadManagementException;
import lk.yj.snw.util.Constants;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String BUILD_ERROR = "Error while deploying to jenkins";

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    private RestTemplate defaultRestTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Value("${jenkins.username}")
    private String userName;

    @Value("${jenkins.password}")
    private String pass;

    @Value("${spring.profiles.active}")
    private String buildEnvironment;

    @Value("${jenkins.deploy.url}")
    private String jenkinsDeployUrl;

    @Value("${rabbit.host}")
    private String rabbitHost;

    @Value("${rabbit.port}")
    private String rabbitPort;

    @Value("${rabbit.user}")
    private String rabbitUser;

    @Value("${rabbit.password}")
    private String rabbitPassword;

    @Value("${api.security.enabled}")
    private boolean apiSecurityEnabled;

    @Value("${url.authenticationService}")
    private String authenticationServiceUrl;

    @ExceptionHandler(UploadManagementException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(UploadManagementException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(ex.getErrorCode());
        error.setDesc(ex.getMessage());
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    protected String deployToJenkins(DeploymentDTO deploymentDTO) throws UploadManagementException {

        MultiValueMap<String, String> map = constructRequestMap(deploymentDTO);
        map.add(Constants.RABBIT_HOST, rabbitHost);
        map.add(Constants.RABBIT_PORT, rabbitPort);
        map.add(Constants.RABBIT_USER, rabbitUser);
        map.add(Constants.RABBIT_P_KEY, rabbitPassword);

        return deployToJenkinsAndGetQueueId(map,jenkinsDeployUrl);
    }

    private MultiValueMap<String, String> constructRequestMap(DeploymentDTO deploymentDTO){

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(Constants.SERVICE_NAME, deploymentDTO.getServiceName());
        map.add(Constants.ZIP_FILE_NAME, deploymentDTO.getFileName());
        map.add(Constants.PORT_NUMBER, String.valueOf(deploymentDTO.getPortNumber()));
        map.add(Constants.DOCKER_IMAGE_NAME, deploymentDTO.getDockerName().split(":")[0]);
        map.add(Constants.USER_ID, String.valueOf(deploymentDTO.getUserId()));
        map.add(Constants.ENVIRONMENT, buildEnvironment);

        return map;

    }

     private String deployToJenkinsAndGetQueueId(MultiValueMap<String, String> map,String url) throws UploadManagementException{

         String queueId = null;
         try {

             RestTemplate restTemplate = new RestTemplate();
             restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(userName, pass));

             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

             HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

             ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);

             if(!response.getHeaders().isEmpty() && response.getHeaders().get("Location") != null) {
                 queueId = Objects.requireNonNull(response.getHeaders().get(Constants.LOCATION)).get(0).split("item/")[1].split("/")[0];
             }

         } catch (HttpClientErrorException hcexp) {
             logger.error("HttpClientErrorException post call exception : ", hcexp);
             throw new UploadManagementException(BUILD_ERROR);

         }catch (Exception exp) {
             logger.error("Exception post call exception : ", exp);
             throw new UploadManagementException(BUILD_ERROR);
         }

         return queueId;
     }

    public Map<String, String> checkAuthentication(HttpServletRequest request, ConfigGroup configGroup, ConfigKey configKey) throws UploadManagementException {

        String apiHeaderMifeJWT = request.getHeader(Constants.API_HEADER_MIFE_KEY);
        String apiHeaderUserId = request.getHeader(Constants.API_HEADER_USER_ID_KEY);
        String apiHeaderTenantId = request.getHeader(Constants.API_HEADER_TENANT_ID_KEY);

        Map<String, String> responseHeaderMap = new HashMap<>();

        if (apiSecurityEnabled) {
            try {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                headers.add(Constants.API_HEADER_MIFE_KEY, apiHeaderMifeJWT);
                headers.add(Constants.API_HEADER_USER_ID_KEY, apiHeaderUserId);
                headers.add(Constants.API_HEADER_TENANT_ID_KEY, apiHeaderTenantId);

                if (configGroup != null) {
                    headers.add(Constants.CONFIG_GROUP, configGroup.toString());
                    headers.add(Constants.CONFIG_KEY, configKey.toString().toLowerCase());
                }

                headers.add(Constants.LOG_IDENTIFIER_KEY, MDC.get(Constants.LOG_IDENTIFIER_KEY));

                HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
                HttpEntity<Boolean> response = defaultRestTemplate.exchange(authenticationServiceUrl, HttpMethod.POST, httpEntity, Boolean.class);

                if (Boolean.FALSE.equals(response.getBody())) {
                    logger.error("Access Denied. X-JWT-Assertion - {}, UserId: {}, TenantId: {}", apiHeaderMifeJWT, apiHeaderUserId, apiHeaderTenantId);
                    throw new UploadManagementException(40100, HttpStatus.UNAUTHORIZED, "Access Denied");
                }

                responseHeaderMap.put(Constants.USER_ID_KEY, response.getHeaders().getFirst(Constants.USER_ID_KEY));

                if (configGroup != null) {
                    responseHeaderMap.put(Constants.CONFIG_VALUE, response.getHeaders().getFirst(Constants.CONFIG_VALUE));
                    logger.debug("config_group: {}, config_key: {}, config_value: {}", configGroup, configKey, responseHeaderMap.get(Constants.CONFIG_VALUE));
                }

            } catch (RestClientException exception) {
                final String errorMessage = "Message was not accept by the authentication point." + exception.getMessage();
                logger.error(errorMessage, exception);
                throw new UploadManagementException(50001, HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
            }
        }

        return responseHeaderMap;
    }

    protected void setLogIdentifier(HttpServletRequest request) {

        String logIdentifier = request.getHeader(Constants.LOG_IDENTIFIER_KEY);

        if (logIdentifier != null) {
            MDC.put(Constants.LOG_IDENTIFIER_KEY, logIdentifier);
        } else {
            MDC.put(Constants.LOG_IDENTIFIER_KEY, UUID.randomUUID().toString());
        }
    }

    protected void checkBasicConfiguration(ConfigGroup configGroup, int userId, String configValue) throws UploadManagementException {

        if (StringUtils.isEmpty(configValue)) {
            logger.warn("No configuration value found for group: {} for userId: {}. So unlimited access granted.", configGroup, userId);
            return;
        }

        int maxLimit = Integer.parseInt(configValue);

        configurationService.checkMaxExternalServicesPerUser(userId, maxLimit);
    }
}
