package school.sptech.back_end_PI.mapper;

import school.sptech.back_end_PI.dto.aluno.AlunoResponse;
import school.sptech.back_end_PI.dto.contrato.ContratoResponse;
import school.sptech.back_end_PI.dto.horario.HorarioResponse;
import school.sptech.back_end_PI.dto.professor.ProfessorResponse;
import school.sptech.back_end_PI.dto.turma.TurmaResponse;
import school.sptech.back_end_PI.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContratoMapper {

    public static ContratoResponse toResponse(Contrato contrato) {
        if (contrato == null) {
            return null;
        }

        ContratoResponse response = new ContratoResponse();

        response.setId(contrato.getId());
        response.setTipo(contrato.getTipo());
        response.setDataInicio(contrato.getDataInicio());
        response.setDataFim(contrato.getDataFim());

        // 1. Mapeia Aluno (Obrigatório em ambos os tipos no novo modelo)
        Aluno aluno = contrato.getAluno();
        if (aluno != null) {
            AlunoResponse alunoResponse = new AlunoResponse();
            alunoResponse.setId(aluno.getId()); // ajuste o getter de ID conforme sua classe Aluno
            alunoResponse.setNome(aluno.getNome());
            alunoResponse.setEmail(aluno.getEmail());
            alunoResponse.setTelefone(aluno.getTelefone());
            alunoResponse.setNivel(aluno.getNivel());
            response.setAluno(alunoResponse);
        }

        // 2. Mapeia Professor (Presente se for contrato Individual)
        Professor professor = contrato.getProfessor();
        if (professor != null) {
            ProfessorResponse professorResponse = new ProfessorResponse();
            professorResponse.setId(professor.getId());
            professorResponse.setNome(professor.getNome());
            professorResponse.setEmail(professor.getEmail());
            professorResponse.setTelefone(professor.getTelefone());
            response.setProfessor(professorResponse);
        }

        // 3. Mapeia Turma (Presente se for contrato em Grupo - Resolvendo o débito técnico)
        Turma turma = contrato.getTurma();
        if (turma != null) {
            TurmaResponse turmaResponse = new TurmaResponse();
            turmaResponse.setId(turma.getId()); // ajuste o getter de ID conforme sua classe Turma
            turmaResponse.setNome(turma.getNome());
            turmaResponse.setNivel(turma.getNivel());
            turmaResponse.setLimiteAlunos(turma.getLimiteAlunos());
            turmaResponse.setTipo(turma.getTipo());
            response.setTurma(turmaResponse);
        }

        // 4. Mapeia a Lista de Horários (Presente se for contrato Individual)
        List<Horario> horarios = contrato.getHorarios();
        if (horarios != null && !horarios.isEmpty()) {
            List<HorarioResponse> horariosResponse = horarios.stream()
                    .map(horario -> {
                        HorarioResponse hRes = new HorarioResponse();
                        hRes.setId(horario.getId()); // ajuste o getter de ID conforme sua classe Horario
                        hRes.setDiaSemana(horario.getDiaSemana());
                        hRes.setHoraInicio(horario.getHoraInicio());
                        hRes.setHoraFim(horario.getHoraFim());
                        return hRes;
                    })
                    .collect(Collectors.toList());
            response.setHorarios(horariosResponse);
        } else {
            response.setHorarios(new ArrayList<>()); // Evita retornar null na lista do JSON
        }

        return response;
    }
}