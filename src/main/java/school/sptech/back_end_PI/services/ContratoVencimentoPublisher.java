package school.sptech.back_end_PI.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.config.RabbitMqConfiguration;
import school.sptech.back_end_PI.dto.contrato.ContratoVencimentoMensagem;

@Service
public class ContratoVencimentoPublisher {

    private static final Logger log = LoggerFactory.getLogger(ContratoVencimentoPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publicar(ContratoVencimentoMensagem mensagem) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfiguration.EXCHANGE_CONTRATOS,
                RabbitMqConfiguration.ROUTING_KEY_VENCIMENTO,
                mensagem
        );
        log.info("Contrato {} publicado na fila de vencimento (vence em {} dias)",
                mensagem.contratoId(), mensagem.diasParaVencer());
    }
}
