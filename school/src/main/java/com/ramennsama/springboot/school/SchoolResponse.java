package com.ramennsama.springboot.school;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolResponse {

    private String name;
    private String email;
    List<Student> students;
}