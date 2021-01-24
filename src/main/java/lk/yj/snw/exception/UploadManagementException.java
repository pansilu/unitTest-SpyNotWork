package lk.yj.snw.exception;

import org.springframework.http.HttpStatus;

public class UploadManagementException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int errorCode;
    private final HttpStatus httpStatus;

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public UploadManagementException(String errorMessage) {
        super(errorMessage);
        errorCode = 1000;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    public UploadManagementException() {
        super();
        errorCode = 1000;
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    public UploadManagementException(int errorCode, HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
