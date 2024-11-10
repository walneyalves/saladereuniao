package com.gitlab.walneyalves.meeting_rooms;

import com.gitlab.walneyalves.meeting_rooms.configuration.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestMeetingRoomsApplication {

	public static void main(String[] args) {
		SpringApplication
				.from(MeetingRoomsApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}