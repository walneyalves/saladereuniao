package com.gitlab.walneyalves.meeting_rooms.domain.filter;

import com.gitlab.walneyalves.meeting_rooms.model.Meeting;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public record MeetingFilter(@Nullable LocalDateTime startDate,
                            @Nullable LocalDateTime endDate,
                            @Nullable Meeting.State... states) { }