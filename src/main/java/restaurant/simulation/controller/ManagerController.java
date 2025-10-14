package restaurant.simulation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import restaurant.simulation.service.ManagerService;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

	private final ManagerService managerService;

	// Constructor Injection
	public ManagerController(ManagerService managerService) {
		this.managerService = managerService;
	}

	@PostMapping("/start")
	public ResponseEntity<String> simulateRestaurantDay(@RequestParam(defaultValue = "3") int chefs) {
		managerService.simulateRestaurantDay(chefs);
		return ResponseEntity.ok("Simulation started with " + chefs + " chefs.");
	}

	@PostMapping("/close")
	public ResponseEntity<String> closeRestaurant() {
		managerService.closeRestaurant();
		return ResponseEntity.ok("Restaurant closed.");
	}

	@GetMapping("/report")
	public ResponseEntity<String> getReport() {
		return ResponseEntity.ok(managerService.getReport());
	}
}
