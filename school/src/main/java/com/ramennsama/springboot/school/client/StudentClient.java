package com.ramennsama.springboot.school.client;

import com.ramennsama.springboot.school.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

// dùng để gọi sang service khác
@FeignClient(
        name = "student-service",
        url = "${application.config.students-url}"
)
//@Component
public interface StudentClient {

    @GetMapping("/api/v1/students/school/{school-id}")
    List<Student> findAllStudentsBySchool(
            @PathVariable("school-id") Integer schoolId
    );
}