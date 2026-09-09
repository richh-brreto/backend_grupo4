package school.sptech.back_end_PI.services;

import org.springframework.stereotype.Service;
import school.sptech.back_end_PI.dto.horario.HorarioResponse;
import school.sptech.back_end_PI.entity.Horario;
import school.sptech.back_end_PI.mapper.HorarioMapper;
import school.sptech.back_end_PI.repository.HorarioRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class HorarioService {

    private static final List<String> ORDEM_DIAS = List.of(
            "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira",
            "Sexta-feira", "Sábado", "Domingo"
    );

    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<HorarioResponse> listarTodos() {
        Comparator<Horario> porDiaEHora = Comparator
                .comparingInt((Horario h) -> {
                    int indice = ORDEM_DIAS.indexOf(h.getDiaSemana());
                    return indice < 0 ? ORDEM_DIAS.size() : indice;
                })
                .thenComparing(Horario::getHoraInicio);

        List<Horario> horarios = horarioRepository.findAll().stream()
                .sorted(porDiaEHora)
                .toList();

        return HorarioMapper.toResponseList(horarios);
    }
}
