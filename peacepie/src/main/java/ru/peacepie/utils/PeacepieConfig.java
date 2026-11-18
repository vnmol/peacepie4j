package ru.peacepie.utils;

import java.time.Duration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public final class PeacepieConfig {
	
	public static final PeacepieConfig CONFIG = new PeacepieConfig();

    private final Config config;

    public PeacepieConfig() {
        this.config = ConfigFactory.load();
    }

    public String clusterName() {
        return config.getString("peacepie.cluster-name");
    }

    public Duration startupTimeout() {
        return config.getDuration("pekko.cluster.startup-timeout");
    }
    
    public Config asConfig() {
        return config;
    }
}