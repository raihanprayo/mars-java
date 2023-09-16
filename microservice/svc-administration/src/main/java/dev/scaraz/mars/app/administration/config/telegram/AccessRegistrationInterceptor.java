package dev.scaraz.mars.app.administration.config.telegram;

import dev.scaraz.mars.app.administration.telegram.user.UserRegistrationFlow;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.model.TelegramHandler;
import dev.scaraz.mars.telegram.model.TelegramInterceptor;
import dev.scaraz.mars.telegram.util.enums.HandlerType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class AccessRegistrationInterceptor implements TelegramInterceptor {
    public static final String MESSAGE = "silahkan melengkapi registrasi anda terlebih dahulu";
    public static final String MESSAGE_WAIT_LIST = "silahkan menunggu registrasi anda diterima";

    private final UserRegistrationFlow registrationFlow;

    @Override
    public boolean intercept(HandlerType type, Update update, TelegramHandler handler) {
        Class<?> beanClass = handler.getBeanClass();
        Method method = handler.getMethod();

        RegistrationRestriction classAnnotation = AnnotatedElementUtils.findMergedAnnotation(beanClass, RegistrationRestriction.class);
        RegistrationRestriction methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, RegistrationRestriction.class);

        if (hasAnnotation(classAnnotation, methodAnnotation)) {
            Long userId = TelegramContextHolder.getUserId();
            if (registrationFlow.isInRegistration(userId))
                throw BadRequestException.args(MESSAGE);
            if (registrationFlow.isInApprovalWaitList(userId))
                throw BadRequestException.args(MESSAGE);
        }
        return true;
    }

    private boolean hasAnnotation(RegistrationRestriction classAnnotation, RegistrationRestriction methodAnnotation) {
        RegistrationRestriction restriction = classAnnotation == null ? methodAnnotation : classAnnotation;
        return restriction != null;
    }

}
