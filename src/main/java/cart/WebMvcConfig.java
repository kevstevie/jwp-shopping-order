package cart;

import cart.dao.MemberDao;
import cart.ui.MemberArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final MemberDao memberDao;

    public WebMvcConfig(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberArgumentResolver(memberDao));
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://solo5star.github.io/react-shopping-cart-prod",
                        "https://naveowo.github.io/react-shopping-cart-prod",
                        "http://localhost:3000"
                )
                .allowedMethods("*")
                .exposedHeaders(HttpHeaders.LOCATION);
    }
}
