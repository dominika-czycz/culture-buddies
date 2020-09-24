package pl.coderslab.cultureBuddies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.coderslab.cultureBuddies.buddies.BuddyConverter;
import pl.coderslab.cultureBuddies.city.CityConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("home");
        registry.addViewController("/").setViewName("home");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(getCityConverter());
        registry.addConverter(getBuddyConverter());
    }

    @Bean
    public CityConverter getCityConverter() {
        return new CityConverter();
    }

    @Bean
    public BuddyConverter getBuddyConverter() {
        return new BuddyConverter();
    }

}
