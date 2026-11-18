package ru.peacepie.plugin.echo;

import ru.peacepie.plugin.api.PeacepieHandler;

public class EchoHandler implements PeacepieHandler {

    @Override
    public void handle(String msg) {
        System.out.println("[EchoPlugin/Echo] Received message: " + msg);
    }
}
