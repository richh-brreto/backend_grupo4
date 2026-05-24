package school.sptech.back_end_PI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.contrato.ContratoRequest;
import school.sptech.back_end_PI.dto.contrato.ContratoResponse;
import school.sptech.back_end_PI.services.ContratoService;

import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<ContratoResponse> atualizarContrato(@PathVariable Long id, @RequestBody ContratoRequest request){
        ContratoResponse contratoAtualizado = service.atualizarContrato(id,request);
        return ResponseEntity.status(200).body(contratoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ContratoResponse> deletarContrato(@PathVariable Long id){
        service.deletarContrato(id);
        return ResponseEntity.status(204).build();
    }

    @GetMapping()
    public ResponseEntity<List<ContratoResponse>> listarContratos(){
        List<ContratoResponse> response = service.listarTodosContratos();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> buscarContratoPorId(@PathVariable Long id){
        ContratoResponse response = service.buscarContratoPorId(id);
        return ResponseEntity.status(200).body(response);
    }

}
