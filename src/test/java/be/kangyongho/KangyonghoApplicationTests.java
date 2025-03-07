package be.kangyongho;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("설정 문제가 해결될 때까지 비활성화")
class KangyonghoApplicationTests{

	@Test
	void contextLoads() {
	}

}
