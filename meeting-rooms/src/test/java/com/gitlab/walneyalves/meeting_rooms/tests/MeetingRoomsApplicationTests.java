package com.gitlab.walneyalves.meeting_rooms.tests;

import com.gitlab.walneyalves.meeting_rooms.configuration.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MeetingRoomsApplicationTests {

	@Test
	void contextLoads() {
	}

}
