package kr.hhplus.be.server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.DummyDateGenerator;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DummyController {

	private final DummyDateGenerator dummyDateGenerator;

	@PostMapping("/dummy")
	public ResponseEntity<Void> generateDummy() {
		dummyDateGenerator.generateDummyData();
		return ResponseEntity.ok(null);
	}
}
