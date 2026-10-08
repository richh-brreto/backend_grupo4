package school.sptech.back_end_PI.services.relatorio;

import org.springframework.stereotype.Component;
import school.sptech.back_end_PI.dto.csv.ContextoCsv;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RelatorioFileContext { // AtomicReference atualiza alguma coisa de forma atomica entre threads - sem ter q dar synchronized
   // tem q usar isso pq o tem duas coisas chamando, o cron e as consultas assim nunca tem um meio contexto
    private final AtomicReference<ContextoCsv> atual = new AtomicReference<>();

    public Optional<ContextoCsv> obterValido() {
        return Optional.ofNullable(atual.get()).filter(ContextoCsv::valido);
    }

    public Optional<ContextoCsv> obterAtual(){
        return Optional.ofNullable(atual.get());
    }

    public void atualizar (ContextoCsv novo){
        atual.set(novo);
    }
}

