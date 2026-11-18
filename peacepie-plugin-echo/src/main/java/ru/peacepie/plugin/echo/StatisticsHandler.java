package ru.peacepie.plugin.echo;

import ru.peacepie.plugin.api.PeacepieHandler;

/** Пример второго обработчика в том же плагине. */
public class StatisticsHandler implements PeacepieHandler {

    @Override
    public void handle(String msg) {
        System.out.println("[EchoPlugin/Statistics] Message length: " + msg.length());
    }
}
