package app.expert.constants;

import app.base.objects.IGEnum;
import app.expert.services.MailSenderService;

public enum ExpertErrors implements IGEnum {

    REGION_NOT_FOUND("Регион не найден"),
    REGION_ALREADY_EXISTS("Регион уже существует"),

    STATUS_NOT_FOUND("Статус не найден"),
    STATUS_ALREADY_EXISTS("Статус уже существует"),

    SECTION_NOT_FOUND("Раздел не найден"),
    SECTION_ALREADY_EXISTS("Раздел уже существует"), 
    
    CLIENT_TYPE_ALREADY_EXISTS("Такой тип клиента уже существует"), 
    CLIENT_TYPE_NOT_FOUND("Тип клиента не найден"), 
    
    SERVICE_ALREADY_EXISTS("Такой сервис уже существует"),
    SERVICE_NOT_FOUND("Сервис не найден"), 
    
    EVENT_TYPE_ALREADY_EXIST("Такой тип события уже существует"), 
    EVENT_TYPE_NOT_FOUND("Тип события не найден"),
    
    CLIENT_ALREADY_EXISTS("Такой клиент уже существует"), 
    CLIENT_NOT_FOUND("Клиент не найден"), 
    
    MESSAGE_TEMPLATE_NOT_FOUND("Шаблон не найден"),
    
    ROUTE_TYPE_NOT_FOUND("Маршрут не найден"),
    
    DUPLICATE_CLIENT_EMAIL("Клиент с таким e-mail уже существует"),
    DUPLICATE_CLIENT_PHONE("Клиент с таким телефоном уже существует"),
    
    ROUTE_NOT_FOUND("Путь ограничения прав доступа менеджера не найден"),
    
    MANAGER_ROLE_NOT_FOUND("Роль не найдена"),
    MANAGER_ROLE_ALREADY_EXISTS("Такая роль уже существует"), 
    
    MANAGER_ROLE_ROUTE_ALREADY_EXISTS("Такое отношенеи уже существует"),
    MANAGER_ROLE_ROUTE_NOT_FOUND("Такое отношение не найдено"),
    
    MANAGER_NOT_FOUND("Такого сотрудника не существует"),
    MANAGER_ALREADY_EXISTS("Такой сотрудник уже существует"), 
    
    MANAGERS_MANAGER_ROLE_NOT_FOUND("Роль менеджера не найдена"),
    MANAGERS_MANAGER_ROLE_ALREADY_EXISTS("Роль менеджера уже существует"),
    
    SUBJECT_ALREADY_EXISTS("Тема с таким кодом уже существует"),
    SUBJECT_NOT_FOUND_ERROR("Тема не найдена"), 
    
    REQUEST_FIELD_ALREADY_EXIST("Форма запроса уже существует"),
    REQUEST_FIELD_NOT_FOUND("Форма запроса не найдена"),
    RQ_FIELD_REQUEST_PARAMETER_NOT_FOUND("Одно из полей subject или subSubject должны быть заданы"),
    RQ_FIELD_AMBIGUOUS_REQUEST_PARAMETER("Должен быть указан только один параметр:subject либо subSubject"),
    
    REQUEST_FIELD_VALUE_ALREADY_EXIST("Значение поля с таким id уже существует"),
    REQUEST_FIELD_VALUE_NOT_FOUND("Значение поля по id не найдено"), 
    
    CALL_NOT_FOUND("Звонок не найден"),
    CALL_ALREADY_EXISTS("Звонок с таким идентификатором уже существует"),
    CALL_WAS_COMPLETED("Звонок с таким идентификатором уже завершен"),
    
    ADMISSION_SUBJECT_ALREADY_EXISTS("Такая тема обращения уже существует"),
    ADMISSION_SUBJECT_NOT_FOUND("Тема обращения не найдена"),

    PAGE_NOT_FOUND("Запрашиваемая страница не найдена"),
    BAD_CREDENTIALS("Неверные данные авторизации"),

    SESSION_NOT_FOUND("Сессия не найдена"),

    SESSION_EXPIRED("Сессия истекла"),

    UNAUTHORIZED("Вы не авторизованы"),
    PATH_NOT_FOUND("Путь не найден"),
    ACCESS_DENIED("У вас нет доступа"),

    ADMISSION_ALREADY_EXISTS("Обращение уже существует"),
    ADMISSION_NOT_FOUND("Обращение не найдено"),
    
    EVENT_NOT_FOUND("Событие не найдено"),
    
    MANAGER_SESSION_NOT_FOUND("Сессия менеджера не найдена"),
    
    NOTIFICATION_NOT_FOUND("Напоминание не найдено"),
    
    ADMISSION_EVENT_TYPE_NOT_FOUND("Тип события для обращения не найден"),
    ADMISSION_EVENT_NOT_FOUND("События обращения не найдены"),
    WRONG_ADMISSION_EVENT_TYPE("Неверный тип события"),
    
    REQUEST_NOT_FOUND("Запрос не найден"), 
    
    REGION_PHONE_NUMBER_NOT_FOUND("Регион не найден"),

    BAD_SYSTEM("Неверный источник запроса"),

    RQ_FIELD_SUBJECT_ALREADY_EXISTS ("Поле запроса для темы уже существует"),
    RQ_FIELD_SUBJECT_NOT_FOUND("Поле запроса для темы не найдено"),

    RQ_FIELD_SUB_SUBJECT_NOT_FOUND("Поле запроса для подтемы не найдено"),

    SUB_SUBJECT_NOT_FOUND("Подтематика не найдена"),

    DEF_PHONE_NOT_FOUND ("Номер мобильного телефона не найден"),

    MANAGER_DISMISSAL_DATE("Дата увольнения сотрудника наступит раньше чем выбранный период"),

    GROUP_NOT_FOUND("Такой группы не существует"),
    GROUP_ALREADY_EXISTS("Группа уже существует"),
    GROUP_DISABLED("Группа недоступна"),

    CONFIG_NOT_FOUND("Конфигурации не существует"),
    CONFIG_ALREADY_EXISTS("Конфигурация уже существует"),

    SUBJECT_FOR_ALL("Тематика открыта для всех"),

    REQUEST_EMAIL_NOT_FOUND("Письмо не найдено"),

    EXCEEDED_FILE_SIZE("Размер файла превышает "
            + MailSenderService.MAX_FILE_SIZE/(1024 * 1024) + "MB"),
    EXCEEDED_FILES_SIZE("Общий размер файлов превышает "
            + MailSenderService.MAX_ALL_FILES_SIZE/(1024 * 1024) + "MB"),
    LETTER_NOT_DELIVERED("Письмо не доставленно."),

    MANAGER_NOT_WORK_TODAY("Сотрудник сегодня не работает"),

    NOT_FOUND_MANAGERS_FOR_REQUESTS("Сотрудники которые могут обработать запросы не найдены"),

    FIELD_DOES_NOT_SUPPORT_VALUES("Тип поля не поддерживает значения"),
    FIELD_REQUIRES_VALUES("Для данного типа поля нужно указать значения"),

    EXCEEDED_REGION_PHONE_NUMBERS_SIZE_FILE("Размер файла не должен превышать 20MB"),

    REQUEST_ALREADY_CLOSED("Запрос уже закрыт"),
    WRONG_CLOSE_STATUS("Выбранный пункт не соответствует маршруту запроса");

    private String message;

    private ExpertErrors(String message) {
        this.message = message;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return message;
    }
}
