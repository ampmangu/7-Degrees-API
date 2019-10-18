package com.ampmangu.degrees;

import com.ampmangu.degrees.config.DefaultProfileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static com.ampmangu.degrees.config.Constants.*;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class})
public class Application implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Environment env;

    public static String API_KEY;
    public static String MASTER_TOKEN;

    public Application(Environment env) {
        this.env = env;
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        if (MOVIE_DB_API_KEY.equalsIgnoreCase("unset") && SYSTEM_MOVIE_DB_API_KEY == null) {
            log.error("FATAL ERROR, API KEY IS NOT SET");
            return;
        }
        if (!MOVIE_DB_API_KEY.equalsIgnoreCase("unset")) {
            API_KEY = MOVIE_DB_API_KEY;
        } else if (SYSTEM_MOVIE_DB_API_KEY != null) {
            API_KEY = SYSTEM_MOVIE_DB_API_KEY;
        }

        if (JWT_MASTER_TOKEN.equalsIgnoreCase("unset") && JWT_MASTER_TOKEN_SYSTEM == null) {
            log.error("JWT TOKEN NOT SET");
            return;
        }
        if (!JWT_MASTER_TOKEN.equalsIgnoreCase("unset")) {
            MASTER_TOKEN = JWT_MASTER_TOKEN;
        } else if (JWT_MASTER_TOKEN_SYSTEM != null) {
            MASTER_TOKEN = JWT_MASTER_TOKEN_SYSTEM;
        }
        SpringApplication app = new SpringApplication(Application.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}{}\n\t" +
                        "External: \t{}://{}:{}{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                env.getActiveProfiles());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
    }
}