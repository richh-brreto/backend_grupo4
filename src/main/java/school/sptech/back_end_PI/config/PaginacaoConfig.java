package school.sptech.back_end_PI.config;

import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaginacaoConfig {

    // Impede que o cliente peça páginas gigantes (ex: ?size=100000); acima disso o Spring usa 50
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer limitarTamanhoPagina() {
        return resolver -> resolver.setMaxPageSize(50);
    }
}
