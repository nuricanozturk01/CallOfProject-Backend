package callofproject.dev.service.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig
{
    @Bean
    public ExecutorService provideThreadPool()
    {
        return Executors.newCachedThreadPool();
    }
}
