package selenium.utility;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtility {

    private static final String[] LOG_PATH = {"config/scc-logback.xml"};

    public void initialLog() {
//    	System.setProperty("LOG_FILE", logFile);
        LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();
        for (String path : LOG_PATH) {
            try {
                configurator.doConfigure(path);
//                MDC.put("sessionId", RandomUtility.randomStr(15));
                log.info("Found config file: {}", path);
                log.info("================== Log initial successful. ==================");
                break;
            } catch (JoranException e) {
                log.error("Error configuring log from {}: {}", path, e.getMessage());
            }
        }
    }
}