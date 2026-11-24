package ru.peacepie.plugin.echo;

import ru.peacepie.plugin.api.PeacepieHandler;
import ru.peacepie.plugin.api.PeacepieMessage;

/** Пример второго обработчика в том же плагине. */
public class StatisticsHandler implements PeacepieHandler {

    @Override
    public void handle(PeacepieMessage msg) {
        System.out.println("[EchoPlugin/Statistics] Message length: " + msg.toString().length());
    }
}
