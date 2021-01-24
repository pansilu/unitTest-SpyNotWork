package lk.yj.snw.controller;

import lk.yj.snw.controller.dto.DeploymentDTO;
import lk.yj.snw.controller.dto.UploadFileDTO;
import lk.yj.snw.exception.UploadManagementException;
import lk.yj.snw.model.QueueDetails;
import lk.yj.snw.service.ConfigurationService;
import lk.yj.snw.service.FileUploadService;
import lk.yj.snw.service.QueueManagementService;
import lk.yj.snw.util.ConfigGroup;
import lk.yj.snw.util.ConfigKey;
import lk.yj.snw.util.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Yasas Pansilu Jayasuriya
 * @version 1.0
 * @E-mail yasas.jayasuriya@axiatadigitallabs.com
 * @Telephone +94777332170
 * @project unitTest-SpyNotWork
 * @user Yasas_105071
 * @created on 1/24/2021
 * @Package lk.yj.snw.controller
 * @company Axiata Digital Labs(Pvt) Ltd.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    @MockBean
    private QueueManagementService queueManagementService;

    @MockBean
    private ModelMapper modelMapper;

    @SpyBean
    private FileUploadController fileUploadController;

    @Test
    public void uploadFile() throws Exception {

        String origin = "generic-device-service-1";
        String userId = "65532";

        when(fileUploadService.findOneByOrigin(origin)).thenReturn(null);
        when(fileUploadService.existByOriginAndUserId(origin, Integer.parseInt(userId))).thenReturn(false);

        Map<String, String> mockResponseHeaderMap = new HashMap<>();
        mockResponseHeaderMap.put(Constants.USER_ID_KEY, userId);
        mockResponseHeaderMap.put(Constants.CONFIG_VALUE, "5");

        doReturn(mockResponseHeaderMap).when(fileUploadController).checkAuthentication(new MockHttpServletRequest(), ConfigGroup.MAX_SERVICE_ONBOARD_COUNT, ConfigKey.TOTAL);
        doNothing().when(fileUploadController).checkBasicConfiguration(ConfigGroup.MAX_SERVICE_ONBOARD_COUNT, Integer.parseInt(userId), mockResponseHeaderMap.get(Constants.CONFIG_VALUE));

        MockMultipartFile file = new MockMultipartFile("file", "hello.zip",
                "application/zip", ">>> print(“Hello World”)".getBytes());

        when(fileUploadService.save(file, new UploadFileDTO())).thenReturn(new DeploymentDTO());

        String queueId = "1111";

        doReturn(queueId).when(fileUploadController).deployToJenkins(new DeploymentDTO());

        QueueDetails mockQueueDetails = new QueueDetails();
        mockQueueDetails.setId(111);
        mockQueueDetails.setQueueId(Integer.parseInt(queueId));

        when(modelMapper.map(new DeploymentDTO(), QueueDetails.class)).thenReturn(mockQueueDetails);
        when(queueManagementService.create(new QueueDetails())).thenReturn(mockQueueDetails);


        ResultActions resultActions = mockMvc.perform(multipart("/api/uploaded-files").file(file)
                .header(Constants.API_HEADER_USER_ID_KEY, userId)
                .header(Constants.API_HEADER_TENANT_ID_KEY, "1")
                .param("origin", origin)
                .param("userId", userId)
                .param("dockerName", origin + "-docker")
        ).andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }
}
