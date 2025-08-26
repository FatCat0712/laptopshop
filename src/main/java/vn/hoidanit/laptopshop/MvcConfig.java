package vn.hoidanit.laptopshop;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectPath = "images/avatar/";
        String absolutePath = new File(projectPath).getAbsolutePath();
        Path dirPath = Paths.get(absolutePath);
        String systemPath = dirPath.toFile().getAbsolutePath();



        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/avatar/**").addResourceLocations("file:" + systemPath + "/");
        registry.addResourceHandler("/client/**").addResourceLocations("classpath:/templates/client/");
    }
}
