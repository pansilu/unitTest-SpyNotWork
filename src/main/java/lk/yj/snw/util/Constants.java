package lk.yj.snw.util;

public class Constants {

    private Constants(){
        throw new IllegalStateException("Utility Class");
    }

    public static final String FILE_EXTENSION = "application/zip";
    public static final String FILE_EXTENSION_ZIP = "application/zip-compressed";
    public static final String FILE_EXTENSION_ZIP_X = "application/x-zip-compressed";
    public static final String SERVICE_NAME = "SERVICENAME";
    public static final String ZIP_FILE_NAME = "ZIPFILENAME";
    public static final String PORT_NUMBER = "PORTNUMBER";
    public static final String RABBIT_HOST = "RABBITHOST";
    public static final String RABBIT_PORT = "RABBITPORT";
    public static final String RABBIT_USER = "RABBITUSER";
    public static final String RABBIT_P_KEY = "RABBITPASSWORD";
    public static final String DOCKER_IMAGE_NAME = "DOCKERIMAGENAME";
    public static final String USER_ID = "USERID";
    public static final String ENVIRONMENT = "ENVIRONMENT";
    public static final String SUCCESS = "success";
    public static final String PENDING = "PENDING";
    public static final String NUMBER = "number";
    public static final String RESULT = "result";
    public static final String BUILDING = "building";
    public static final String WHY = "why";
    public static final String EXECUTABLE = "executable";
    public static final String LOCATION = "Location";
    public static final String SERVICE_TYPE_EXTERNAL = "external";
    public static final String SERVICE_TYPE_TCP = "tcp";

    public static final String API_HEADER_MIFE_KEY = "X-JWT-Assertion";
    public static final String API_HEADER_USER_ID_KEY = "X-User-Id";
    public static final String API_HEADER_TENANT_ID_KEY = "X-Tenant-Id";
    public static final String CONFIG_VALUE = "Config-Value";
    public static final String CONFIG_GROUP = "Config-Group";
    public static final String CONFIG_KEY = "Config-Key";
    public static final String LOG_IDENTIFIER_KEY = "UUID";
    public static final String USER_ID_KEY = "User-Id";
}
