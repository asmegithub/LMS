package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorEnrollmentSummaryDTO {
    private int totalEnrollments;
    private int totalStudents;
    private int totalCourses;
}
