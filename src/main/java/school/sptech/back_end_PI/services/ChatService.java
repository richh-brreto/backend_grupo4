package school.sptech.back_end_PI.services;

import com.google.genai.errors.ClientException;
import com.google.genai.errors.GenAiIOException;
import school.sptech.back_end_PI.dto.chat.ChatRequest;
import school.sptech.back_end_PI.dto.chat.ChatResponse;
import school.sptech.back_end_PI.dto.chat.TipoChat;
import school.sptech.back_end_PI.dto.csv.UploadedCsv;
import school.sptech.back_end_PI.services.gemini.GeminiFilesService;
import school.sptech.back_end_PI.services.gemini.GeminiService;
import school.sptech.back_end_PI.dto.csv.ContextoCsv;
import school.sptech.back_end_PI.dto.csv.CsvSnapshot;
import school.sptech.back_end_PI.services.relatorio.RelatorioFileContext;
import school.sptech.back_end_PI.services.relatorio.RelatorioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final RelatorioService relatorioService;
    private final GeminiFilesService geminiFilesService;
    private final GeminiService geminiService;
    private final RelatorioFileContext relatorioFileContext;

    public ChatService(RelatorioService relatorioService, GeminiFilesService geminiFilesService, GeminiService geminiService, RelatorioFileContext relatorioFileContext) {
        this.relatorioService = relatorioService;
        this.geminiFilesService = geminiFilesService;
        this.geminiService = geminiService;
        this.relatorioFileContext = relatorioFileContext;
    }

    public ChatResponse conversar(ChatRequest request){
        request.validar();
        ContextoCsv contexto = garantirContexto();
        String prompt = montarPrompt(request, contexto);
        try {
            return new ChatResponse(geminiService.gerarAnaliseComArquivo(prompt, contexto),
                    LocalDateTime.now());
        } catch (ClientException | GenAiIOException e) {
            CsvSnapshot snapshot = relatorioService.extrairCsvPadrao();
            return new ChatResponse(geminiService.gerarAnaliseInline(prompt, snapshot.csv()),
                    LocalDateTime.now());
        }
    }

    // isso aq é o re-upload lazy - se da restart ou se o cron falha a consulta re upa, ai impede duas consultas
    private synchronized ContextoCsv garantirContexto() {
        return relatorioFileContext.obterValido().orElseGet(this::renovarContexto);
    }

    private ContextoCsv renovarContexto() {
        CsvSnapshot snapshot = relatorioService.extrairCsvPadrao();
        String displayName = "relatorio-dashboard-" + LocalDate.now();

        UploadedCsv novo = geminiFilesService.uploadCsv(snapshot.csv(), displayName);
        ContextoCsv novoContexto = montarContexto(novo, snapshot);

        relatorioFileContext.atualizar(novoContexto);
        return novoContexto;
    }

    private ContextoCsv montarContexto(UploadedCsv arquivo, CsvSnapshot snapshot){
        return ContextoCsv.de(arquivo, snapshot);
    }

    private String montarPrompt(ChatRequest request, ContextoCsv contexto){
        if(request.tipo() ==  TipoChat.RELATORIO){
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return """
                    Analise os dados da dashboard de professores referentes ao período de %s a %s
                    (arquivo CSV anexado) e escreva um relatório em Markdown contendo:
                    1. Resumo executivo; 
                    2. Pontos de atenção (sobrecarregados/subutilizados);
                    3. Distribuição da carga de aulas; 
                    4. Recomendações práticas.
                    Máximo 200 palavras, em prosa.
                    """.formatted(contexto.periodoInicio().format(fmt), contexto.periodoFim().format(fmt));
        }

        return request.mensagem() + "\n Responsa com base no CSV anexado";
    }
}
