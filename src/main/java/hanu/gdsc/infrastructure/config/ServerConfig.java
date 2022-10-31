package hanu.gdsc.infrastructure.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class ServerConfig {
    @Value("server.ip")
    private String ipAddress;
}
