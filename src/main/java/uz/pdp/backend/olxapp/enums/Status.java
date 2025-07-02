package uz.pdp.backend.olxapp.enums;

public enum Status {

    DRAFT,          // Черновик (пользователь создал, но не отправил на модерацию)
    PENDING_REVIEW, // На модерации (ожидает проверки)
    ACTIVE,         // Активно (одобрено и видимо всем)
    REJECTED,       // Отклонено модератором
    SOLD,           // Продано
    INACTIVE;        // Истек срок действия

}
