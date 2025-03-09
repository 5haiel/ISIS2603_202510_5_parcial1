package uniandes.dse.examen1.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class CourseEntity {

    @PodamExclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique code for the course
     */
    private String courseCode;

    /**
     * The standard name for the course
     */
    private String name;

    /**
     * The number of credits for the course: a number between 1 and 9
     */
    private Integer credits;

    /**
     * A list with the students that have been enrolled in this course.
     * No student should appear more than once in this list
     */
    @PodamExclude
    @ManyToMany(mappedBy = "courses")
    private List<StudentEntity> students = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "course")
    private List<RecordEntity> records = new ArrayList<>();
}
