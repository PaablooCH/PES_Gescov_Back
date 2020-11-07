package com.gescov.webserver.api;

import com.gescov.webserver.model.School;
import com.gescov.webserver.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("api/school")
@RestController
public class SchoolController {

    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }


    @PostMapping
    public School addSchool(@RequestBody School school) {
        return schoolService.addSchool(school);
    }

    @GetMapping
    public List<School> getAllSchools() {
        return schoolService.getAllSchools();
    }

    @GetMapping(path = "/id/{id}")
    public School getSchoolById(@PathVariable("id") String id) {
        return schoolService.getSchoolById(id).orElse(null);
    }

    @GetMapping(path = "/name/{schoolName}")
    public School getSchoolByName(@PathVariable("schoolName") String schoolName) {
        return schoolService.getSchoolByName(schoolName);
    }

    @DeleteMapping(path = "{id}")
    public void deletePersonById(@PathVariable("id") String id) {
        schoolService.deleteSchool(id);
    }

    @PutMapping(path = "{specific}")
    public void updateSchoolName(@PathVariable("specific") String specific, @NotNull @RequestParam("id") String id, @NotNull @RequestParam("update") String update) {
        if (specific.equals("name")) schoolService.updateSchoolName(id, update);
        else if (specific.equals("state")) schoolService.updateSchoolState(id, update);
    }

}


