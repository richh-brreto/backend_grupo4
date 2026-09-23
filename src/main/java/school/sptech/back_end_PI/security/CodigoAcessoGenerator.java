package school.sptech.back_end_PI.security;

import java.security.SecureRandom;

/**
 * Gera códigos de acesso curtos, mas seguros (alfabeto sem caracteres ambíguos),
 * usados no fluxo de primeiro acesso de alunos e professores.
 */
public final class CodigoAcessoGenerator {

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodigoAcessoGenerator() {
    }

    public static String gerar() {
        StringBuilder codigo = new StringBuilder(TAMANHO);
        for (int i = 0; i < TAMANHO; i++) {
            codigo.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
        }
        return codigo.toString();
    }
}