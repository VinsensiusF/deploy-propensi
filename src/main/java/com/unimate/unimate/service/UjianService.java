package com.unimate.unimate.service;

import com.unimate.unimate.dto.UjianDTO;
import com.unimate.unimate.dto.UpdateUjianDTO;
import com.unimate.unimate.entity.Kelas;
import com.unimate.unimate.entity.Partnership;
import com.unimate.unimate.entity.Ujian;

import java.util.List;

public interface UjianService {
    void saveUjian(Ujian ujian);

    List<Ujian> getAllUjian();

    Ujian getUjianById(Long id);

    Ujian createUjian (UjianDTO ujianDTO);

    Ujian updateUjian (UpdateUjianDTO updateUjianDTO);

    void deleteUjian(Ujian ujian);

    List<Ujian> findUjianListByGuruId(Long guruId);

    List<Ujian> findUjianListByKelasId(Long kelasId);
}
