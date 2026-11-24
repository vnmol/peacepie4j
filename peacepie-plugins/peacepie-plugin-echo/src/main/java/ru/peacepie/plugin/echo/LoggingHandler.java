package ru.peacepie.plugin.echo;

import java.time.Instant;

import ru.peacepie.plugin.api.PeacepieHandler;
import ru.peacepie.plugin.api.PeacepieMessage;

/** Пример третьего обработчика в том же плагине. */
public class LoggingHandler implements PeacepieHandler {

    @Override
    public void handle(PeacepieMessage msg) {
        System.out.println("[EchoPlugin/Logging] " + Instant.now() + " message='" + msg + "'");
    }
}
