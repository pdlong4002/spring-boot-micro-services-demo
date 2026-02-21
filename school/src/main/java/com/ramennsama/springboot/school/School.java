package com.ramennsama.springboot.school;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class School {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String email;

}
