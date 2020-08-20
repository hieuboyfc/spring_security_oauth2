package zimji.hieuboy.oauth2.modules.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 19/08/2020 - 21:33
 */

@Accessors(fluent = true)
@Entity
@Table(name = "email_outbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailOutboxEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ID Tự tăng
    @Basic
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    // Gửi email đến
    @Basic
    @Email
    @Column(name = "EMAIL_TO", length = 50, nullable = false)
    private String emailTo;

    // Tiêu đề email
    @Basic
    @Column(name = "EMAIL_SUBJECT", length = 500, nullable = false)
    private String emailSubject;

    // Nội dung email
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "EMAIL_CONTENT", nullable = false)
    private String emailContent;

    // Người gửi email
    @Basic
    @Column(name = "SEND_USER", length = 50)
    private String sendUser;

    // Ngày gửi email
    @Basic
    @Column(name = "SEND_DATE")
    private Date sendDate;

    // Thông tin lỗi nếu có
    @Basic
    @Column(name = "SEND_ERROR", length = 5000)
    private String sendError;

    // Tên tệp tin mẫu
    @Basic
    @Column(name = "FILE_TEMPLATE", length = 50)
    private String fileTemplate;

    // Trạng thái gửi email (0 - Chưa gửi; 1 - Đã gửi; 2 - Gửi lỗi)
    @Basic
    @Column(name = "STATUS")
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getSendError() {
        return sendError;
    }

    public void setSendError(String sendError) {
        this.sendError = sendError;
    }

    public String getFileTemplate() {
        return fileTemplate;
    }

    public void setFileTemplate(String fileTemplate) {
        this.fileTemplate = fileTemplate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
