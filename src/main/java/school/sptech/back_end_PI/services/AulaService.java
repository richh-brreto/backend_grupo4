package school.sptech.back_end_PI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.entity.Aula;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.repository.AulaRepository;
import school.sptech.back_end_PI.repository.ContratoRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class AulaService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private AulaRepository aulaRepository;


    public void gerarAulasMesSubsequente() {

        System.out.println("Iniciando geração automática de aulas...");

        try {

            List<Contrato> contratos = contratoRepository.findAll();

            YearMonth proximoMes = YearMonth.now().plusMonths(1);
            LocalDate inicioMes = proximoMes.atDay(1);
            LocalDate fimMes = proximoMes.atEndOfMonth();

            List<Aula> aulasGeradas = new ArrayList<>();

            for (Contrato contrato : contratos) {

                if (contrato.getProfessor() == null ||
                        contrato.getProfessor().getHorarios() == null) {
                    continue;
                }

                LocalDate inicioContrato = contrato.getDataInicio();
                LocalDate fimContrato = contrato.getDataFim();
                for (Horario horario : contrato.getProfessor().getHorarios()) {

                    DayOfWeek diaSemana = converterDiaSemana(horario.getDiaSemana());

                    LocalDate dataAtual = inicioMes;

                    while (!dataAtual.isAfter(fimMes)) {

                        boolean dentroDoContrato =
                                !dataAtual.isBefore(inicioContrato) &&
                                        !dataAtual.isAfter(fimContrato);

                        if (dataAtual.getDayOfWeek().equals(diaSemana) && dentroDoContrato) {

                            Aula aula = new Aula();

                            aula.setData(dataAtual);
                            aula.setHoraInicio(horario.getHoraInicio());
                            aula.setHoraFim(horario.getHoraFim());
                            aula.setStatus("AGENDADA");
                            aula.setPresenca(false);
                            aula.setContrato(contrato);
                            aulasGeradas.add(aula);
                        }

                        dataAtual = dataAtual.plusDays(1);
                    }
                }
            }

            aulaRepository.saveAll(aulasGeradas);
            System.out.println("Aulas geradas com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DayOfWeek converterDiaSemana(String diaSemana) {

        return switch (diaSemana.toUpperCase()) {
            case "SEGUNDA", "SEGUNDA-FEIRA" -> DayOfWeek.MONDAY;
            case "TERCA", "TERÇA", "TERCA-FEIRA", "TERÇA-FEIRA" -> DayOfWeek.TUESDAY;
            case "QUARTA", "QUARTA-FEIRA" -> DayOfWeek.WEDNESDAY;
            case "QUINTA", "QUINTA-FEIRA" -> DayOfWeek.THURSDAY;
            case "SEXTA", "SEXTA-FEIRA" -> DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Dia da semana inválido: " + diaSemana);
        };
    }
}