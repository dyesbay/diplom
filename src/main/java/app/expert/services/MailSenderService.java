package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.services.GContextService;
import app.expert.constants.ExpertErrors;
import app.expert.constants.ExternalServicesCodes;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.request.RequestCache;
import app.expert.db.statics.integrations_configs.IntegrationConfig;
import app.expert.db.statics.letters.Letter;
import app.expert.db.statics.letters.LetterCache;
import app.expert.models.letter.RqLetter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@RequiredArgsConstructor
@Service
public class MailSenderService {

    public final static int MAX_FILE_SIZE = 5 * 1024 * 1024;
    public final static int MAX_ALL_FILES_SIZE = 25 * 1024 * 1024;

    private final IntegrationConfigService confService;
    private final GContextService context;
    private final LetterCache cache;
    private final ManagerCache managerCache;
    private final RequestCache requestCache;
    private final EventService eventService;

    /**
     * Отправляет электронную почту,
     * сохраняет копию письма в базе(без вложенных фалов)
     * @param rq - request model
     */
    public Letter saveAndSendEmail(RqLetter rq) throws GNotAllowed, GNotFound, IOException, GBadRequest, MessagingException, GSystemError {
        requestCache.find(rq.getRequest());
        if(rq.getAttachments() != null && rq.getAttachments().length > 0) {
            checkFileSize(rq);
            sendMIMEMessage(rq);
        }
        else
            sendSimpleMessage(rq);
        Letter letter = saveLetter(rq);
        eventService.createEmailSentEvent(rq.getRequest(), letter);
       return saveLetter(rq);
    }

    /**
     * Отправка обычного письма без вложений
     * @param rq - request model
     * @throws GNotAllowed - проблемы с JavaMailSender
     * @throws GNotFound - не найдены конфигурации
     * для сервиса отправки электронной почты
     */
    private void sendSimpleMessage(RqLetter rq) throws GNotAllowed, GNotFound {
        JavaMailSender sender = getMailSender();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(rq.getEmail());
        message.setSubject(rq.getSubject());
        message.setText(rq.getBody());
        sender.send(message);
    }

    /**
     * Отправка письма с вложениями
     * @param rq - request model
     * @throws GNotAllowed - проблемы с JavaMailSender
     * @throws GNotFound - не найдены конфигурации
     * для сервиса отправки электронной почты
     */
    private void sendMIMEMessage(RqLetter rq) throws GNotAllowed, GNotFound, MessagingException, GBadRequest {
        JavaMailSender sender = getMailSender();
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(rq.getEmail());
        helper.setText(rq.getBody());
        helper.setSubject(rq.getSubject());
        for (MultipartFile file : rq.getAttachments()){
            helper.addAttachment(file.getOriginalFilename(), file);
        }
        try {
            sender.send(helper.getMimeMessage());
        } catch (MailException e) {
            throw new GBadRequest(ExpertErrors.LETTER_NOT_DELIVERED, e);
        }
    }

    private Letter saveLetter(RqLetter rq) throws GNotAllowed, GNotFound {
        Manager manager = managerCache.find(context.getUser());
        return cache.save(rq.getLetter(manager));
    }

    /**
     * Поиск конфигураций для SMTP сервера в базе,
     * настройка JavaMailSender`а
     * @return - JavaMailSender - класс для отправки электронной почты
     */
    private JavaMailSender getMailSender() throws GNotAllowed, GNotFound {
        //Находим в базе конфиги для отправки письма
        IntegrationConfig conf = confService.getIntegrationConfig(ExternalServicesCodes.EMAIL_SERVER);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        //освновные настройки из базы
        mailSender.setHost(conf.getServer());
        mailSender.setPort(Integer.parseInt(conf.getPort()));
        mailSender.setUsername(conf.getUsername());
        mailSender.setPassword(conf.getKey());

        //Допонительные настройки, зависят от требований SMTP сервера
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    /**
     * Проверка размера фалов: отдельно взятого файла,
     * всех файлов разом
     * @param rq - request model
     * @throws IOException - проблемы со считыванием файлов
     */
    private void checkFileSize(RqLetter rq) throws IOException, GBadRequest {
        double mbCount = 0;
        for(MultipartFile file : rq.getAttachments()) {
            if (file.getBytes().length > MAX_FILE_SIZE)
                throw new GBadRequest(ExpertErrors.EXCEEDED_FILE_SIZE);
            mbCount += file.getBytes().length;
        }
        if(mbCount > MAX_ALL_FILES_SIZE)
            throw new GBadRequest(ExpertErrors.EXCEEDED_FILES_SIZE);
    }
}
