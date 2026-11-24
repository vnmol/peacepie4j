
package ru.peacepie.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.peacepie.Peacepie;
import ru.peacepie.utils.PeacepieUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) throws Exception {
    	
    	PeacepieUtils.devInit();
    	
        Logger logger = LoggerFactory.getLogger(Main.class);

        logger.info("Main is started");

        Peacepie peacepie = new Peacepie().startup();

        AtomicBoolean stopping = new AtomicBoolean(false);

        Runnable shutdown = () -> {
            if (stopping.compareAndSet(false, true)) {
                logger.info("Main is stopping");

                peacepie.shutdown();

                logger.info("Main is finished");
            }
        };

        Runtime.getRuntime().addShutdownHook(
                new Thread(shutdown, "peacepie-shutdown")
        );
        
        System.out.println("ENTER");

        logger.info("Enter 'stop' to shutdown Peacepie");

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(System.in))) {

            String command;

            while ((command = reader.readLine()) != null) {

                if ("stop".equalsIgnoreCase(command.trim())) {
                    logger.info("Stop command received");
                    shutdown.run();
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Error reading console input", e);
            shutdown.run();
        }
    }
}
