package school.sptech.back_end_PI.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    public static final String EXCHANGE_CONTRATOS = "contratos.exchange";
    public static final String FILA_CONTRATOS_VENCIMENTO = "contratos.vencimento.queue";
    public static final String ROUTING_KEY_VENCIMENTO = "contrato.vencimento";

    @Bean
    public TopicExchange contratosExchange() {
        return new TopicExchange(EXCHANGE_CONTRATOS, true, false);
    }

    @Bean
    public Queue contratosVencimentoQueue() {
        return QueueBuilder.durable(FILA_CONTRATOS_VENCIMENTO)
                .withArgument("x-dead-letter-exchange", "mensageria.aula.dlx")
                .build();
    }

    @Bean
    public Binding vencimentoBinding(Queue contratosVencimentoQueue, TopicExchange contratosExchange) {
        return BindingBuilder.bind(contratosVencimentoQueue)
                .to(contratosExchange)
                .with(ROUTING_KEY_VENCIMENTO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
