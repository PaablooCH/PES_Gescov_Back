package com.gescov.webserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@CompoundIndex(name="classAtSchoolExists", def = "{'name' : 1, 'schoolID' : 1}", unique = true)
@Document(collection = "classrooms")
public class Classroom {

    @Id
    private String id;

    @NotNull(message = "Classrooms' name must not be null")
    private String name;

    @Min(1)
    private int numRows;

    @Min(1)
    private int numCols;

    @NotNull(message = "Classrooms' schoolID must not be null")
    private String schoolID;

    private List<Schedule> scheduleList;

    public Classroom(@JsonProperty("id") String id,
                     @JsonProperty("name") String name,
                     @JsonProperty("numRows") int numRows,
                     @JsonProperty("numCols") int numCols,
                     @JsonProperty("schoolID") String schoolID) {
        this.id = id;
        this.name = name;
        this.numRows = numRows;
        this.numCols = numCols;
        this.schoolID = schoolID;
        this.scheduleList = new ArrayList<>();
    }

    void addSchedule(Schedule schedule){
        scheduleList.add(schedule);
    }

}
