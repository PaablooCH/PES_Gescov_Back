package com.gescov.webserver.api;

import com.gescov.webserver.model.School;
import com.gescov.webserver.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("api/schools")
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
        return schoolService.getSchoolByID(id);
    }

    @GetMapping(path = "/name/{schoolName}")
    public School getSchoolByName(@PathVariable("schoolName") String schoolName) {
        return schoolService.getSchoolByName(schoolName);
    }

    @DeleteMapping(path = "{id}")
    public void deleteSchoolById(@PathVariable("id") String id, @NotNull @RequestParam("creatorID") String creatorID) {
        schoolService.deleteSchool(id, creatorID);
    }

    @DeleteMapping(path = "{id}/admin")
    public void deleteAdmin(@PathVariable("id") String id, @NotNull @RequestParam("creatorID") String creatorID, @NotNull @RequestParam("adminID") String adminID) {
        schoolService.deleteAdmin(id, creatorID, adminID);
    }

    @PutMapping
    public School updateSchool(@NotNull @RequestParam("id") String id, @NotNull @RequestParam("name") String name,
                             @NotNull @RequestParam("latitude") float latitude, @NotNull @RequestParam ("longitude") float longitude,
                               @NotNull @RequestParam("phone") String phone, @NotNull @RequestParam String website,
                             @NotNull @RequestParam ("address") String address) {
        return schoolService.updateSchool(id, name, latitude, longitude, phone, website, address);
    }

    @PutMapping(path = "{specific}")
    public void updateSchool(@PathVariable("specific") String specific, @NotNull @RequestParam("id") String id, @NotNull @RequestParam("update") String update) {
        if (specific.equals("name")) schoolService.updateSchoolName(id, update);
        else if (specific.equals("state")) schoolService.updateSchoolState(id, update);
    }

    @PutMapping(path = "{id}/updateName")
    public void updateSchoolName(@PathVariable("id") String id, @NotNull @RequestParam("name") String name) {
        schoolService.updateSchoolName(id, name);
    }

    @PutMapping(path = "{id}/updateState")
    public void updateSchoolState(@PathVariable("id") String id, @NotNull @RequestParam("state") String state) {
        schoolService.updateSchoolState(id, state);
    }

    @PutMapping(path = "{id}/updateAdmin")
    public void updateSchoolAdmin(@PathVariable("id") String id, @NotNull @RequestParam("admin") String adminID,
                                  @NotNull @RequestParam("newAdmin") String newAdminID) {
        schoolService.addAdministrator(id, adminID, newAdminID);
    }

    @GetMapping(path = "/scores")
    public List<Pair<School, Integer>> getSchoolsAndNumInfected() {
        return schoolService.getSchoolsAndNumInfected();
    }

    @GetMapping(path = "/{id}/tracking")
    public List<Pair<LocalDate, Integer>> getPunctuation(@PathVariable("id") String id) {
        return schoolService.getPunctuation(id);
    }

    @PostMapping(path = "/entry")
    public String checkEntryCode(@RequestParam("schoolID") String schoolID, @NotNull @RequestParam("code") String code, @NotNull @RequestParam("userID") String userID) {
        return schoolService.checkEntryCode(schoolID, userID, code);
    }

}
