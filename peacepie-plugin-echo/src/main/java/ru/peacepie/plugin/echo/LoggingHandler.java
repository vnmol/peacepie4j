package ru.peacepie.plugin.echo;

import java.time.Instant;

import ru.peacepie.plugin.api.PeacepieHandler;

/** Пример третьего обработчика в том же плагине. */
public class LoggingHandler implements PeacepieHandler {

    @Override
    public void handle(String msg) {
        System.out.println("[EchoPlugin/Logging] " + Instant.now() + " message='" + msg + "'");
    }
}
