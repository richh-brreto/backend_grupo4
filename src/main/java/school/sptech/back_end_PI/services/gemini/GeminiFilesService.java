package school.sptech.back_end_PI.services.gemini;

import com.google.genai.types.File;
import com.google.genai.types.FileState;
import com.google.genai.types.UploadFileConfig;
import school.sptech.back_end_PI.dto.csv.UploadedCsv;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class GeminiFilesService {
    private static final String MIME_CSV = "text/csv";
    private static final int MAX_TENTATIVAS = 5;

    private final GeminiClientProvider geminiClientProvider;

    public GeminiFilesService(GeminiClientProvider geminiClientProvider) {
        this.geminiClientProvider = geminiClientProvider;
    }

    public UploadedCsv uploadCsv(String csv, String displayName) {
        UploadFileConfig config = UploadFileConfig.builder()
                .mimeType(MIME_CSV)
                .displayName(displayName)
                .build();

        File file = geminiClientProvider.get().files.upload(csv.getBytes(StandardCharsets.UTF_8), config);
        return aguardarAtivo(file);
    }

    public File get(String fileName) {
        return geminiClientProvider.get().files.get(fileName, null);
    }

    public void delete (String fileName){
        geminiClientProvider.get().files.delete(fileName, null);
    }

    private UploadedCsv aguardarAtivo(File file) {
        File atual = file;
        for(int i = 0; i < MAX_TENTATIVAS && estaProcessando(atual); i ++){
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            atual = geminiClientProvider.get().files.get(atual.name().orElseThrow(), null);
        }

        if(estaProcessando(atual) || atual.state().isEmpty()) {
            throw new IllegalStateException("CSV não ficou ativo a tempo");
        }

        if(atual.state().get().knownEnum() != FileState.Known.ACTIVE) {
            throw new IllegalStateException("Falha no processamento do CSV no Files API");
        }

        return new UploadedCsv( // a api aceita name ou uri, entao ja chama os dois de uma vez
                atual.name().orElseThrow(),
                atual.uri().orElseThrow(),
                atual.mimeType().orElse(MIME_CSV),
                atual.expirationTime().orElse(null));
    }

    private boolean estaProcessando(File file) {
        return file.state().map(s -> s.knownEnum() == FileState.Known.PROCESSING).orElse(true);
    }
}

