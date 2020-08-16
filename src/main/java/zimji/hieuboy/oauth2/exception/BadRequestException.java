package zimji.hieuboy.oauth2.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:34
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message;
    private boolean throwSuccess = false;

    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }

    public BadRequestException(String message, boolean throwSuccess) {
        super(message);
        this.message = message;
        this.throwSuccess = throwSuccess;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}
