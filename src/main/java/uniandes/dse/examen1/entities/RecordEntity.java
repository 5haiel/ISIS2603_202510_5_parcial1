package uniandes.dse.examen1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class RecordEntity {

    @PodamExclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The final grade for a course. It must be a number between 1.5 and 5.0
     */
    private Double finalGrade;

    /**
     * The semester when the student took the course
     */
    private String semester;

    // TODO
    @PodamExclude
    @ManyToOne
    private StudentEntity student;

}
