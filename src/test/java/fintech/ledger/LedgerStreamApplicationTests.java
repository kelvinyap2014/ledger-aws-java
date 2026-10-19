package fintech.ledger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import fintech.ledger.config.TestAwsConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAwsConfig.class)
class LedgerStreamApplicationTests {
	@Test
	void contextLoads() {
	}
}
