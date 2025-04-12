package uz.project.moviehivebot.configuration;

import lombok.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class MyConfig {

    @Bean
    DefaultBotOptions botOptions(){
        return new DefaultBotOptions();
    }

}
