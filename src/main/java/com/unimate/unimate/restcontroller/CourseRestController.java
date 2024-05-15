package com.unimate.unimate.restcontroller;

import com.unimate.unimate.aspect.ValidateToken;
import com.unimate.unimate.dto.CreateCourseContentDTO;
import com.unimate.unimate.dto.CreateCourseDTO;
import com.unimate.unimate.dto.UpdateCourseContentDTO;
import com.unimate.unimate.dto.UpdateCourseDTO;
import com.unimate.unimate.entity.Course;
import com.unimate.unimate.entity.CourseContent;
import com.unimate.unimate.enums.RoleEnum;
import com.unimate.unimate.exception.CourseContentNotFoundException;
import com.unimate.unimate.exception.CourseNotFoundException;
import com.unimate.unimate.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseRestController {
    private final CourseService courseService;

    @Autowired
    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    // To get all Courses of a Class, by Kelas ID
    @GetMapping("/class-courses/{kelasId}")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.TOP_LEVEL, RoleEnum.CUSTOMER_SERVICE})
    public ResponseEntity<List<Course>> getCoursesByKelasId(@PathVariable("kelasId") Long kelasId) {
        List<Course> coursesInAClass = courseService.getCourseByKelasId(kelasId);
        return ResponseEntity.ok(coursesInAClass);
    }

    // To get a single Course by its ID
    @GetMapping("/{id}")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.TOP_LEVEL, RoleEnum.CUSTOMER_SERVICE})
    public ResponseEntity<?> getCourseById(@PathVariable("id") Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        return ResponseEntity.ok(course);
    }

    // To get all CourseContents of a Course, by Course ID
    @GetMapping("/content/list/{courseId}")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.TOP_LEVEL, RoleEnum.CUSTOMER_SERVICE})
    public ResponseEntity<?> getCourseContentsByCourseId(@PathVariable("courseId") Long courseId) {
        List<CourseContent> courseContents = courseService.getCourseContentByCourseId(courseId);
        return ResponseEntity.ok(courseContents);
    }

    // To get a single CourseContent by its ID
    @GetMapping("/content/{id}")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.TOP_LEVEL, RoleEnum.CUSTOMER_SERVICE})
    public ResponseEntity<?> getCourseContentById(@PathVariable("id") Long id) {
        CourseContent courseContent = courseService.getCourseContentById(id);
        if (courseContent == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseContent not found.");
        return ResponseEntity.ok(courseContent);
    }

    @PostMapping("/create")
    @ValidateToken({RoleEnum.ADMIN ,RoleEnum.TEACHER})
    public ResponseEntity<?> createCourse(@Valid @RequestBody CreateCourseDTO createCourseDTO) {
        Course course = courseService.createCourse(createCourseDTO);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/content/create")
    @ValidateToken({RoleEnum.ADMIN ,RoleEnum.TEACHER})
    public ResponseEntity<?> createCourseContent(@Valid @RequestBody CreateCourseContentDTO createCourseContentDTO) {
        CourseContent courseContent = courseService.createCourseContent(createCourseContentDTO);
        return ResponseEntity.ok(courseContent);
    }

    @PutMapping("/update")
    @ValidateToken({RoleEnum.ADMIN ,RoleEnum.TEACHER})
    public ResponseEntity<?> updateCourse(@Valid @RequestBody UpdateCourseDTO updateCourseDTO) {
        Course course = courseService.updateCourse(updateCourseDTO);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/content/update")
    @ValidateToken({RoleEnum.ADMIN ,RoleEnum.TEACHER})
    public ResponseEntity<?> updateCourseContent(@Valid @RequestBody UpdateCourseContentDTO updateCourseContentDTO) {
        CourseContent courseContent = courseService.updateCourseContent(updateCourseContentDTO);
        return ResponseEntity.ok(courseContent);
    }

    @DeleteMapping("/{id}")
    @ValidateToken({RoleEnum.ADMIN ,RoleEnum.TEACHER})
    public ResponseEntity<?> deleteCourse(@PathVariable("id") Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            throw new CourseNotFoundException();
        }
        courseService.deleteCourse(course);
        return ResponseEntity.ok("Course has been successfully deleted");
    }

    @DeleteMapping("/content/{id}")
    @ValidateToken({RoleEnum.ADMIN ,RoleEnum.TEACHER})
    public ResponseEntity<?> deleteCourseContent(@PathVariable("id") Long id) {
        CourseContent courseContent = courseService.getCourseContentById(id);
        if (courseContent == null) {
            throw new CourseContentNotFoundException();
        }
        courseService.deleteCourseContent(courseContent);
        return ResponseEntity.ok("CourseContent has been successfully deleted");
    }




}
