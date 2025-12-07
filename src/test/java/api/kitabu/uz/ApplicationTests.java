package api.kitabu.uz;

import api.kitabu.uz.util.MD5util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(UUID.randomUUID());
		System.out.println(MD5util.encode("123456"));
	}

}
