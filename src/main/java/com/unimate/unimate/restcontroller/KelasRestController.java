package com.unimate.unimate.restcontroller;

import com.unimate.unimate.aspect.ValidateToken;
import com.unimate.unimate.dto.*;
import com.unimate.unimate.entity.Account;
import com.unimate.unimate.entity.Kelas;
import com.unimate.unimate.entity.KelasGuru;
import com.unimate.unimate.entity.KelasSiswa;
import com.unimate.unimate.enums.RoleEnum;
import com.unimate.unimate.exception.KelasNotFoundException;
import com.unimate.unimate.repository.RoleRepository;
import com.unimate.unimate.service.AccountService;
import com.unimate.unimate.service.KelasGuruService;
import com.unimate.unimate.service.KelasService;
import com.unimate.unimate.service.KelasSiswaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kelas")
public class KelasRestController {
    
    private final KelasService kelasService;
    private final KelasSiswaService kelasSiswaService;
    private final KelasGuruService kelasGuruService;
    private final AccountService accountService;
    private static final String JWT_HEADER = "Authorization";

    @Autowired
    public KelasRestController(KelasService kelasService, KelasSiswaService kelasSiswaService, KelasGuruService kelasGuruService, AccountService accountService) {
        this.kelasService = kelasService;
        this.kelasSiswaService = kelasSiswaService;
        this.kelasGuruService = kelasGuruService;
        this.accountService = accountService;
    }

    @GetMapping("/get-all")
    public List<KelasDTO> getAllKelas() {
        List<KelasDTO> listKelas = new ArrayList<>();
        for (Kelas kelas : kelasService.getAllKelas()){
            KelasDTO kelasBaru = new KelasDTO();
            kelasBaru.setId(kelas.getId());
            kelasBaru.setCategory(kelas.getCategory());
            kelasBaru.setName(kelas.getName());
            kelasBaru.setPrice(kelas.getPrice());
            kelasBaru.setCover(kelas.getClassPicture());
            listKelas.add(kelasBaru);
        }
        return listKelas;
    }

    @GetMapping("/get-bimbinganbeasiswa")
    public List<KelasDTO> getKelasBimbinganBeasiswa() {
        List<KelasDTO> listKelas = new ArrayList<>();
        for (Kelas kelas : kelasService.getAllKelas()){
            if (kelas.getCategory().equals("Bimbingan Beasiswa")) {
                KelasDTO kelasBaru = new KelasDTO();
                kelasBaru.setId(kelas.getId());
                kelasBaru.setCategory(kelas.getCategory());
                kelasBaru.setName(kelas.getName());
                kelasBaru.setPrice(kelas.getPrice());
                kelasBaru.setCover(kelas.getClassPicture());
                listKelas.add(kelasBaru);
            }
           
        }
        return listKelas;
    }

    
    @GetMapping("/get-persiapantes")
    public List<KelasDTO> getPersiapanTes() {
        List<KelasDTO> listKelas = new ArrayList<>();
        for (Kelas kelas : kelasService.getAllKelas()){
            if (kelas.getCategory().equals("Persiapan Tes")) {
                KelasDTO kelasBaru = new KelasDTO();
                kelasBaru.setId(kelas.getId());
                kelasBaru.setCategory(kelas.getCategory());
                kelasBaru.setName(kelas.getName());
                kelasBaru.setPrice(kelas.getPrice());
                kelasBaru.setCover(kelas.getClassPicture());
                listKelas.add(kelasBaru);
            }
           
        }
        return listKelas;
    }

    @GetMapping("/get-webinar")
    public List<KelasDTO> getWebinar() {
        List<KelasDTO> listKelas = new ArrayList<>();
        for (Kelas kelas : kelasService.getAllKelas()){
            if (kelas.getCategory().equals("Webinar")) {
                KelasDTO kelasBaru = new KelasDTO();
                kelasBaru.setId(kelas.getId());
                kelasBaru.setCategory(kelas.getCategory());
                kelasBaru.setName(kelas.getName());
                kelasBaru.setPrice(kelas.getPrice());
                kelasBaru.setCover(kelas.getClassPicture());
                listKelas.add(kelasBaru);
            }
           
        }
        return listKelas;
    }

    @GetMapping("/get-all-name-only")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.TOP_LEVEL, RoleEnum.CUSTOMER_SERVICE})
    public ResponseEntity<?> getAllKelasNameOnly() {
        return ResponseEntity.ok(kelasService.getAllKelasNames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findKelasById(@PathVariable("id") Long id) {
        Kelas kelas = kelasService.getKelasById(id);
        if (kelas == null) {
            throw new KelasNotFoundException();
        }
        return ResponseEntity.ok(kelas);
    }

    @PostMapping("/create")
    @ValidateToken(RoleEnum.ADMIN)
    public ResponseEntity<Kelas> createKelas( @Valid @ModelAttribute CreateKelasDTO createKelasDTO, 
    @RequestParam("file") MultipartFile file,  @RequestParam("file2") MultipartFile file2 ) {
        Kelas kelas = kelasService.createKelas(createKelasDTO);
        ResponseEntity<Map> kelasImg = kelasService.uploadImageClass(file, kelas.getId());
        ResponseEntity<Map> kelasImg2 = kelasService.uploadCoverClass(file2, kelas.getId());
        return ResponseEntity.ok(kelas);
    }

    @PutMapping("/update")
    @ValidateToken(RoleEnum.ADMIN)
    public ResponseEntity<Kelas> updateKelas(@Valid @RequestBody UpdateKelasDTO updateKelasDTO) {
        Kelas kelas = kelasService.updateKelas(updateKelasDTO);
        return ResponseEntity.ok(kelas);
    }

    @DeleteMapping("/{id}")
    @ValidateToken(RoleEnum.ADMIN)
    public ResponseEntity<String> deleteKelas(@PathVariable("id") Long id) {
        Kelas kelas = kelasService.getKelasById(id);
        if (kelas == null) {
            throw new KelasNotFoundException();
        }


        kelasService.deleteKelas(kelas);

        return ResponseEntity.ok("Kelas has been deleted successfully.");


    }



    @PostMapping("/disenroll")
    @ValidateToken(RoleEnum.ADMIN)
    public ResponseEntity<?> disenrollStudent(@RequestBody KelasSiswaDTO kelasSiswaDTO) {
        kelasSiswaService.disenrollStudent(kelasSiswaDTO);
        return ResponseEntity.ok("Siswa has been successfully disenrolled from Kelas");
    }

    @GetMapping("/enrolled-students/{id}")
    @ValidateToken(RoleEnum.ADMIN)
    public ResponseEntity<?> getEnrolledStudents(@PathVariable("id") Long kelasId) {
        List<Account> enrolledStudents = kelasSiswaService.getAllStudentsInAClass(kelasId);
        return ResponseEntity.ok(enrolledStudents);
    }

    @GetMapping("/classes-enrolled")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public ResponseEntity<?> getClassesEnrolled(HttpServletRequest request) {
        String requestToken = request.getHeader(JWT_HEADER).substring(7);
        Account account = accountService.getAccountFromJwt(requestToken);
        List<Kelas> classesEnrolled = kelasSiswaService.getAllKelasEnrolledByStudent(account);
        return ResponseEntity.ok(classesEnrolled);
    }

    @GetMapping("/is-enrolled")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.TOP_LEVEL, RoleEnum.CUSTOMER_SERVICE})
    public ResponseEntity<?> isStudentEnrolled(HttpServletRequest request,  @RequestParam(required = true, name = "kelasId") Long kelasId) {
        String requestToken = request.getHeader(JWT_HEADER).substring(7);
        Account account = accountService.getAccountFromJwt(requestToken);
        Boolean isEnrolled = kelasSiswaService.isStudentEnrolledInAClass(kelasId, account.getId());
        return ResponseEntity.ok(isEnrolled);
    }


    @PostMapping("/rating")
    @ValidateToken(RoleEnum.ADMIN)
    public ResponseEntity<?> setRatingFromSiswa(@RequestBody KelasRatingDTO kelasRatingDTO) {
        // Siswa gives new rating
        KelasSiswa kelasSiswa = kelasSiswaService.setKelasSiswaRating(kelasRatingDTO);

        // Kelas recalculates its ratings
        List<KelasSiswa> kelasSiswaList = kelasSiswaService.findKelasSiswaListByKelasId(kelasRatingDTO.getKelasId());


        int ratings = 0;
        int ratingsCount = 0;
        for (KelasSiswa ks :
                kelasSiswaList) {
            if (ks.getRating() == null) {
                continue;
            }
            ratings += ks.getRating();
            ratingsCount++;
        }

        Kelas kelas = kelasService.updateRating(kelasRatingDTO.getKelasId(), (float) ratings / (float) ratingsCount);

        return ResponseEntity.ok(kelas);

    }

    @PostMapping("/assign-teacher")
    @ValidateToken({RoleEnum.TEACHER, RoleEnum.ADMIN})
    public ResponseEntity<?> assignTeacherToKelas(@Valid @RequestBody KelasGuruDTO kelasGuruDTO) {
        KelasGuru kelasGuru = kelasGuruService.assignGuru(kelasGuruDTO);
        return ResponseEntity.ok(kelasGuru);
    }

    @PostMapping("/unassign-teacher")
    public ResponseEntity<?> unassignTeacherFromKelas(@Valid @RequestBody KelasGuruDTO kelasGuruDTO) {
        kelasGuruService.unassignGuru(kelasGuruDTO);
        return ResponseEntity.ok("Teacher has been successfully unassigned from Kelas");
    }

    @GetMapping("/kelas-guru/find-all/{id}")
    @ValidateToken({RoleEnum.ADMIN,RoleEnum.TEACHER})
    public ResponseEntity<?> getKelasGuruListByKelasId(@PathVariable("id") Long kelasId) {
        List<KelasGuru> kelasGuruList = kelasGuruService.findKelasGuruListByKelasId(kelasId);
        return ResponseEntity.ok(kelasGuruList);
    }

    @GetMapping("/kelas-guru/{kelasId}/{guruId}")
    @ValidateToken({RoleEnum.ADMIN,RoleEnum.TEACHER})
    public ResponseEntity<?> getKelasGuruListByKelasId(@PathVariable("kelasId") Long kelasId, @PathVariable("guruId") Long guruId) {
        KelasGuru kelasGuru = kelasGuruService.findKelasGuruByKelasIdAndGuruId(kelasId, guruId);
        return ResponseEntity.ok(kelasGuru);
    }

    @GetMapping("/teachers-in-class/{id}")
    @ValidateToken({RoleEnum.ADMIN,RoleEnum.TEACHER})
    public ResponseEntity<?> getAllGuruInAClass(@PathVariable("id") Long kelasId) {
        List<Account> guruList = kelasGuruService.findAllGuruInAClass(kelasId);
        return ResponseEntity.ok(guruList);
    }

    @GetMapping("/classes-taught")
    @ValidateToken({RoleEnum.STUDENT, RoleEnum.ADMIN, RoleEnum.TEACHER, RoleEnum.CUSTOMER_SERVICE, RoleEnum.TOP_LEVEL})
    public ResponseEntity<?> getAllKelasTaughtByAGuru(HttpServletRequest request) {
        String requestToken = request.getHeader(JWT_HEADER).substring(7);
        Account account = accountService.getAccountFromJwt(requestToken);
        List<Kelas> kelasList = kelasGuruService.findAllKelasTaughtByAGuru(account.getId());
        return ResponseEntity.ok(kelasList);
    }

}
