package school.sptech.back_end_PI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.sptech.back_end_PI.dto.dashboard.DashboardResponse;
import school.sptech.back_end_PI.services.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/professores")
    @PreAuthorize("hasAuthority('PERM_TELA_DASHBOARD')")
    public ResponseEntity<DashboardResponse> getDashboardProfessores() {
        DashboardResponse resp = dashboardService.montarDashboardProfessores();
        return ResponseEntity.ok(resp);
    }
}
