package zimji.hieuboy.oauth2.modules.email;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import zimji.hieuboy.oauth2.configs.AppProperties;
import zimji.hieuboy.oauth2.exceptions.BadRequestException;
import zimji.hieuboy.oauth2.modules.auth.payload.ScopeRequest;
import zimji.hieuboy.oauth2.utils.BeanUtils;
import zimji.hieuboy.oauth2.utils.Common;
import zimji.hieuboy.oauth2.utils.RequestClientInfo;
import zimji.hieuboy.oauth2.utils.TOTP;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 20/08/2020 - 14:59
 */

@Service
public class EmailOutboxService {

    private static final Logger logger = LoggerFactory.getLogger(EmailOutboxService.class);

    @Autowired(required = false)
    private HttpServletRequest request;

    private JavaMailSender javaMailSender;

    private Configuration freeMarkerConfig;

    private IEmailOutboxRepository emailOutboxRepository;

    @Autowired
    public EmailOutboxService(JavaMailSender javaMailSender,
                              Configuration freeMarkerConfig,
                              IEmailOutboxRepository emailOutboxRepository) {
        this.javaMailSender = javaMailSender;
        this.freeMarkerConfig = freeMarkerConfig;
        this.emailOutboxRepository = emailOutboxRepository;
    }

    public void insertItem(String emailTo,
                           String emailSubject,
                           String fileNameEmailTemplate,
                           Map<String, String> mapEmailTemplate) throws IOException, TemplateException {
        AppProperties appProperties = BeanUtils.getAppProperties();
        ScopeRequest scopeRequest = BeanUtils.getBean(ScopeRequest.class);
        EmailOutboxEntity emailOutboxEntity = new EmailOutboxEntity();
        if (mapEmailTemplate == null) {
            mapEmailTemplate = new HashMap<>();
        }
        if (!mapEmailTemplate.containsKey("CURRENT_APP")) {
            mapEmailTemplate.put("CURRENT_APP", "ZimJi");
        }
        if (!mapEmailTemplate.containsKey("CURRENT_DATE")) {
            mapEmailTemplate.put("CURRENT_DATE", Common.convertDate2String(new Date(), "dd/MM/yyyy HH:mm:ss"));
        }
        if (!mapEmailTemplate.containsKey("CURRENT_IP")) {
            mapEmailTemplate.put("CURRENT_IP", RequestClientInfo.getInstance().getClientIpAddr(request));
        }
        if (!mapEmailTemplate.containsKey("CURRENT_SIGNATURE")) {
            mapEmailTemplate.put("CURRENT_SIGNATURE", "Thanks and best regards.");
        }
        if (!StringUtils.isEmpty(fileNameEmailTemplate)) {
            freeMarkerConfig.setClassForTemplateLoading(this.getClass(), appProperties.getEmailConfig().folderEmailDefault());
            Template template = freeMarkerConfig.getTemplate(fileNameEmailTemplate);
            emailOutboxEntity.emailTo(emailTo);
            emailOutboxEntity.emailSubject(emailSubject);
            emailOutboxEntity.emailContent(FreeMarkerTemplateUtils.processTemplateIntoString(template, mapEmailTemplate));
            emailOutboxEntity.sendDate(new Date());
            emailOutboxEntity.sendUser(scopeRequest.uid() != null ? scopeRequest.uid() : "[SYSTEM]");
            emailOutboxEntity.status(0);
            emailOutboxEntity.fileTemplate(fileNameEmailTemplate);
            emailOutboxRepository.save(emailOutboxEntity);
            logger.info("[SUCCESS] Lưu thành công dữ liệu Email với địa chỉ Email [{}]", emailTo);
        } else {
            logger.error("[ERROR] Không tìm thấy tên File Template Email");
            throw new BadRequestException("[ERROR] Không tìm thấy tên File Template Email");
        }
    }

    public void excute(String totp) throws InterruptedException {
        AppProperties appProperties = BeanUtils.getAppProperties();
        if (!TOTP.getInstance().checkTOTP("EMAIL_EXCUTE", totp,
                appProperties.getAccount().getExpirationInMs(),
                appProperties.getAccount().getCodeDigits())) {
            throw new BadRequestException(String.format("Xác thực [%s] thất bại.", totp));
        }
        Page<EmailOutboxEntity> pageTemp = emailOutboxRepository.findAllEmail(PageRequest.of(0, 10));
        List<EmailOutboxEntity> emailOutboxEntities = pageTemp.getContent();
        if (emailOutboxEntities.isEmpty()) {
            return;
        }
        ScopeRequest scopeRequest = null;
        for (EmailOutboxEntity emailOutboxEntity : emailOutboxEntities) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setText(emailOutboxEntity.emailContent(), true);
                helper.setSubject(emailOutboxEntity.emailSubject());
                javaMailSender.send(message);
                emailOutboxEntity.status(1);
                logger.info("[SUCCESS] Gửi email đến địa chỉ [{}] thành công.", emailOutboxEntity.emailTo());
            } catch (Exception e) {
                emailOutboxEntity.sendError(Common.getStackTrace(e));
                emailOutboxEntity.status(2);
                logger.error("[ERROR] Gửi email đến địa chỉ [{}] thất bại.", emailOutboxEntity.emailTo(), e);
            }
            emailOutboxRepository.save(emailOutboxEntity);
            Thread.sleep(60000); // Ngủ  60000 ms = 1 phút
        }
    }

}
