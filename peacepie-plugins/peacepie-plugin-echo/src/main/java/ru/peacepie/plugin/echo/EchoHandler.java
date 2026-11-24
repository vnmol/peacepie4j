package ru.peacepie.plugin.echo;

import ru.peacepie.plugin.api.PeacepieHandler;
import ru.peacepie.plugin.api.PeacepieMessage;

public class EchoHandler implements PeacepieHandler {

    @Override
    public void handle(PeacepieMessage msg) {
        System.out.println("[EchoPlugin/Echo] Received message: " + msg);
    }
}
