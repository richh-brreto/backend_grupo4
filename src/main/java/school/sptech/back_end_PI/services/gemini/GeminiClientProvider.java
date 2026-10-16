package school.sptech.back_end_PI.services.gemini;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiClientProvider {
    private final String apiKey;
    private volatile Client client;

    public GeminiClientProvider(@Value("${gemini.api.key}") String apiKey){
        this.apiKey = apiKey;
    }

    public Client get() {
        if(apiKey == null || apiKey.isBlank()){
            throw new IllegalArgumentException("Chave API não configurada");
        }

        if(client == null) {
            synchronized (this) {
                if (client == null) {
                    client = Client.builder().apiKey(apiKey).build();
                }
            }
        }

        return client;
    }

    public boolean chaveConfigurada() {
        return apiKey != null && !apiKey.isBlank();
    }
}
