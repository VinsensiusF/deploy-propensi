package com.unimate.unimate.service.impl;

import com.unimate.unimate.dto.CreateKelasDTO;
import com.unimate.unimate.dto.KelasNameOnly;
import com.unimate.unimate.dto.SetTeacherDTO;
import com.unimate.unimate.dto.UpdateKelasDTO;
import com.unimate.unimate.entity.Account;
import com.unimate.unimate.entity.Kelas;
import com.unimate.unimate.exception.AccountNotFoundException;
import com.unimate.unimate.exception.KelasNotFoundException;
import com.unimate.unimate.repository.KelasRepository;
import com.unimate.unimate.service.AccountService;
import com.unimate.unimate.service.KelasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KelasServiceImpl implements KelasService {
    private final KelasRepository kelasRepository;
    private final AccountService accountService;

    @Autowired
    public KelasServiceImpl(KelasRepository kelasRepository, AccountService accountService){
        this.kelasRepository = kelasRepository;
        this.accountService = accountService;
    }

    @Override
    public void saveKelas(Kelas kelas) {
        kelasRepository.save(kelas);
    }

    @Override
    public List<Kelas> getAllKelas() {
        return kelasRepository.findAll().stream()
        .filter(kelas -> !kelas.isDeleted()) // Filter out soft deleted kelas
        .collect(Collectors.toList());
    }

    @Override
    public Kelas getKelasById(Long id) {
        return kelasRepository.findKelasById(id);
    }

    @Override
    public Kelas createKelas(CreateKelasDTO createKelasDTO) {
        Kelas kelas = new Kelas();

        kelas.setName(createKelasDTO.getName());
        kelas.setRating(0f);
        kelas.setCategory(createKelasDTO.getCategory());
        kelas.setDescription(createKelasDTO.getDescription());
        kelas.setBenefits(createKelasDTO.getBenefits());
        kelas.setSyllabuses(createKelasDTO.getSyllabuses());
        kelas.setPrice(createKelasDTO.getPrice());
        saveKelas(kelas);
        return kelas;
    }

    // TODO implement update siswa in kelas
    // TODO implement many to many siswa in kelas
    @Override
    public Kelas updateKelas(UpdateKelasDTO updateKelasDTO) {
        Kelas kelas = getKelasById(updateKelasDTO.getId());
        if (kelas == null) {
            throw new KelasNotFoundException();
        }

        // disabled rating update by default
        // kelas.setRating(updateKelasDTO.getRating());
        kelas.setName(updateKelasDTO.getName());
        kelas.setCategory(updateKelasDTO.getCategory());
        kelas.setDescription(updateKelasDTO.getDescription());
        kelas.setPrice(updateKelasDTO.getPrice());
        kelas.setBenefits(updateKelasDTO.getBenefits());
        kelas.setSyllabuses(updateKelasDTO.getSyllabuses());
    
        saveKelas(kelas);
        return kelas;

    }

    @Override
    public void deleteKelas(Kelas kelas) {
        kelas.softDelete();
        kelasRepository.save(kelas);
    }

    @Override
    public Kelas updateRating(Long kelasId, Float newRating) {
        Kelas kelas = kelasRepository.findKelasById(kelasId);
        if (kelas == null) {
            throw new KelasNotFoundException();
        }
        kelas.setRating(newRating);
        kelasRepository.save(kelas);
        return kelas;
    }

    @Override
    public List<KelasNameOnly> getAllKelasNames() {
        return kelasRepository.findKelasNameByIdNotNull();
    }


    @Override
    public Long getCountClass() {
        return kelasRepository.countAllClass();
    }
}
