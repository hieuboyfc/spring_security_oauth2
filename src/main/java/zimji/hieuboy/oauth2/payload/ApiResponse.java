package zimji.hieuboy.oauth2.payload;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 23:09
 */

@Accessors(fluent = true)
@Data
@NoArgsConstructor
public class ApiResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return data;
    }

    public void setObject(Object data) {
        this.data = data;
    }
}
