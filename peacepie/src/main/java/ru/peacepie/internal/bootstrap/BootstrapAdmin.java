package ru.peacepie.internal.bootstrap;

import java.io.File;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import ru.peacepie.internal.pekko.PekkoAdmin;
import ru.peacepie.plugin.api.PeacepieMessage;


public class BootstrapAdmin {
	
	public static void startup(PekkoAdmin pekkoAdmin) throws Exception {
        File groovyFile = new File(System.getProperty("config.path"));
        Binding binding = new Binding();
        binding.setVariable("pekkoAdmin", pekkoAdmin);
        binding.setVariable("PeacepieMessage", PeacepieMessage.class);
        GroovyShell shell = new GroovyShell(
            BootstrapAdmin.class.getClassLoader(),
            binding
        );
        shell.evaluate(groovyFile);
    }
}
