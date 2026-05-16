package school.sptech.back_end_PI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.back_end_PI.dto.ContratoRequest;
import school.sptech.back_end_PI.dto.ContratoResponse;
import school.sptech.back_end_PI.entity.Contrato;
import school.sptech.back_end_PI.services.ContratoService;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService service;

    public ContratoController(ContratoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> criarContrato(@RequestBody ContratoRequest request){
        ContratoResponse contratoCriado = service.criarContrato(request);
        return ResponseEntity.status(201).body(contratoCriado);
    }

}
