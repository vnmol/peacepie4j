package ru.peacepie.internal.felix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;

import ru.peacepie.plugin.api.PeacepieHandler;
import ru.peacepie.plugin.api.PeacepieMessage;


public final class HandlerRegistry {

    public static final String HANDLERS_HEADER = "Peacepie-Handlers";

    private final List<RegisteredHandler> handlers = new ArrayList<>();

    public void registerBundle(Bundle bundle) throws ReflectiveOperationException {
        String header = bundle.getHeaders().get(HANDLERS_HEADER);
        if (header == null || header.isBlank()) {
            throw new IllegalStateException("Bundle " + bundle.getSymbolicName()
                    + " has no " + HANDLERS_HEADER + " manifest header");
        }

        String[] classNames = header.split(",");
        for (String rawName : classNames) {
            String className = rawName.trim();
            if (className.isEmpty()) {
                throw new IllegalStateException("Empty handler class name in bundle "
                        + bundle.getSymbolicName() + " header " + HANDLERS_HEADER);
            }

            Class<?> handlerType = bundle.loadClass(className);
            if (!PeacepieHandler.class.isAssignableFrom(handlerType)) {
                throw new IllegalStateException(className + " in "
                        + bundle.getSymbolicName() + " does not implement "
                        + PeacepieHandler.class.getName());
            }

            // Создаём новый объект для каждой записи в заголовке.
            PeacepieHandler instance = (PeacepieHandler) handlerType
                    .getConstructor().newInstance();
            handlers.add(new RegisteredHandler(bundle, className, instance));
            System.out.println("[Peacepie] Handler registered: "
                    + bundle.getSymbolicName() + " -> " + className);
        }
    }

    /** Вызывает каждый зарегистрированный обработчик; ошибка одного не мешает остальным. */
    public void dispatch(PeacepieMessage message) {
        for (RegisteredHandler registered : List.copyOf(handlers)) {
            System.out.println("[Peacepie] -> "
                    + registered.bundle().getSymbolicName() + ": "
                    + registered.className());
            try {
                registered.handler().handle(message);
            } catch (RuntimeException e) {
                System.err.println("[Peacepie] Handler failed: "
                        + registered.className() + ": " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    public List<RegisteredHandler> getHandlers() {
        return Collections.unmodifiableList(handlers);
    }

    public int size() {
        return handlers.size();
    }

    /** Удаляет ссылки на экземпляры перед остановкой их OSGi-бандлов. */
    public void clear() {
        handlers.clear();
    }

    public record RegisteredHandler(Bundle bundle, String className,
                                    PeacepieHandler handler) {
    }
}
