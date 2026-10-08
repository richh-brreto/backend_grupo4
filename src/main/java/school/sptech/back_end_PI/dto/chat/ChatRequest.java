package school.sptech.back_end_PI.dto.chat;

public record ChatRequest(TipoChat tipo, String mensagem){
    public void validar() {
        if (tipo == null || (tipo == TipoChat.PERGUNTA && (mensagem == null || mensagem.isBlank()))) {
            throw new IllegalArgumentException("Informe o tipo e a pergunta.");
        }
    }
}

// pra relatorio, o prompt ja é pronto entao é {"tipo": "RELATORIO"} ent o prompt ja é pronto
// pra pergunta, o tipo vem como pergunta e a mensagem, portanto o chat fica livre

