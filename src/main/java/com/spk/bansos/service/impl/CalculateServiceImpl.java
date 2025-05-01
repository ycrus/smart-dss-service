package com.spk.bansos.service.impl;

import com.spk.bansos.model.*;
import com.spk.bansos.repo.*;
import com.spk.bansos.service.CalculateServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateServiceImpl implements CalculateServiceInterface {


    private final PenilaianRepo penilaianRepo;
    private final PeriodRepo periodRepo;
    private final ParameterRepo parameterRepo;
    private final CriteriaRepo criteriaRepo;
    private final ReceiverRepo receiverRepo;
    private final CalculateReceiverRepo calculateReceiverRepo;
    private final ProgramCriteriaRepo programCriteriaRepo;
    private final NilaiBobotRepo nilaiBobotRepo;
    private final NilaiUtilityRepo nilaiUtilityRepo;
    private final NilaiAkhirRepo nilaiAkhirRepo;
    private final RankingRepo rankingRepo;

    @Override
    public String startCalculate(Long id) {
        Optional<Penilaian> penilaianGet = penilaianRepo.findById(id);
        Penilaian penilaian = null;
        if(penilaianGet.isPresent())penilaian = penilaianGet.get();
        penilaian.setStatus("In Progres");
        penilaianRepo.save(penilaian);
        Optional<Period> period = periodRepo.findById(penilaian.getPeriodId());
        List<ProgramCriteria> program = programCriteriaRepo.findByProgramId(period.get().getProgramId());
        List<CalculateRecevier> calculateReceviers = calculateReceiverRepo.findByPenilaianId(id);
        List<Receiver> user = new ArrayList<>();
        List<String> criteria = new ArrayList<>();
        if(!program.isEmpty()){
            program.forEach(getData->{
                Optional<Criteria> getUser = criteriaRepo.findById(getData.getCriteriaId());
                getUser.ifPresent(value -> criteria.add(value.getTitle()));
            });
        }
        if(!calculateReceviers.isEmpty()){
            calculateReceviers.forEach(getData->{
                Optional<Receiver> getUser = receiverRepo.findById(getData.getReceiverId());
                getUser.ifPresent(user::add);
            });
        }

        if(!criteria.isEmpty() && !user.isEmpty()){
            setParameterWeight(id, user, criteria);
            setUtilityValue(id, user, criteria);
            setNilaiAkhir(id, user, criteria, period.get().getProgramId());
            setRanking(id, user, penilaian.getJumlahPenerima());
        }
        penilaian.setStatus("Done");
        penilaianRepo.save(penilaian);
        return null;
    }

    private void setParameterWeight(Long id, List<Receiver> user, List<String> criteria){
        user.forEach(a -> {
            NilaiBobot bobotUser = new NilaiBobot();
            bobotUser.setPenilaianId(id);
            bobotUser.setReceiverId(a.getId());
            criteria.forEach(b->{
                if(Objects.equals(b,"Penghasilan")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getPenghasilan());
                    parameter.ifPresent(value -> bobotUser.setPenghasilan(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Status Tempat Tinggal")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getStatusTempatTinggal());
                    parameter.ifPresent(value -> bobotUser.setStatusTempatTinggal(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Pekerjaan")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getPekerjaan());
                    parameter.ifPresent(value -> bobotUser.setPekerjaan(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Status Pernikahan Kepala keluarga")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getStatusPerkawinan());
                    parameter.ifPresent(value -> bobotUser.setStatusPerkawinan(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Jumlah Tanggungan")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getJumlahTanggungan());
                    parameter.ifPresent(value -> bobotUser.setJumlahTanggungan(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Keadaan Rumah")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getKeadaanRumah());
                    parameter.ifPresent(value -> bobotUser.setKeadaanRumah(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Bahan Bakar Masak")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getBahanBakarHarian());
                    parameter.ifPresent(value -> bobotUser.setBahanBakarHarian(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Disabilitas")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getDisabilitas());
                    parameter.ifPresent(value -> bobotUser.setDisabilitas(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Tingkat Pendidikan")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getPendidikan());
                    parameter.ifPresent(value -> bobotUser.setPendidikan(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Fasilitas MCK")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getFasilitasMck());
                    parameter.ifPresent(value -> bobotUser.setFasilitasMck(BigDecimal.valueOf(value.getParameterWeight())));
                }
                if(Objects.equals(b,"Kepemilikan Kendaraan")){
                    Optional<Parameter> parameter = parameterRepo.findByTitle(a.getKepemilikanKendaraan());
                    parameter.ifPresent(value -> bobotUser.setKepemilikanKendaraan(BigDecimal.valueOf(value.getParameterWeight())));
                }
            });
            nilaiBobotRepo.save(bobotUser);
        });
    }

    private void setUtilityValue(Long id, List<Receiver> user, List<String> criteria){
        user.forEach(a -> {
            NilaiUtility bobotUser = new NilaiUtility();
            bobotUser.setPenilaianId(id);
            bobotUser.setReceiverId(a.getId());
            criteria.forEach(b->{
                Optional<NilaiBobot> nilaiBobot = nilaiBobotRepo.findByReceiverIdAndPenilaianId(a.getId(), id);
                List<NilaiBobot> allNilai = nilaiBobotRepo.findByPenilaianId(id);
                if(Objects.equals(b,"Penghasilan")){

                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getPenghasilan()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getPenghasilan())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setPenghasilan(utility);
                }
                if(Objects.equals(b,"Status Tempat Tinggal")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getStatusTempatTinggal()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getStatusTempatTinggal())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setStatusTempatTinggal(utility);
                }
                if(Objects.equals(b,"Pekerjaan")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getPekerjaan()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getPekerjaan())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setPekerjaan(utility);
                }
                if(Objects.equals(b,"Status Pernikahan Kepala keluarga")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getStatusPerkawinan()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getStatusPerkawinan())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setStatusPerkawinan(utility);
                }
                if(Objects.equals(b,"Jumlah Tanggungan")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getJumlahTanggungan()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getJumlahTanggungan())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setJumlahTanggungan(utility);
                }
                if(Objects.equals(b,"Keadaan Rumah")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getKeadaanRumah()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getKeadaanRumah())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setKeadaanRumah(utility);
                }
                if(Objects.equals(b,"Bahan Bakar Masak")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getBahanBakarHarian()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getBahanBakarHarian())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setBahanBakarHarian(utility);
                }
                if(Objects.equals(b,"Disabilitas")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getDisabilitas()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getDisabilitas())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setDisabilitas(utility);
                }
                if(Objects.equals(b,"Tingkat Pendidikan")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getPendidikan()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getPendidikan())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setPendidikan(utility);
                }
                if(Objects.equals(b,"Fasilitas MCK")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getFasilitasMck()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getFasilitasMck())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setFasilitasMck(utility);
                }
                if(Objects.equals(b,"Kepemilikan Kendaraan")){
                    List<BigDecimal> nilai = new ArrayList<>();
                    if(!allNilai.isEmpty()){
                        allNilai.forEach(c-> nilai.add(c.getKepemilikanKendaraan()));
                    }
                    BigDecimal maxNilai = BigDecimal.ZERO;
                    BigDecimal minNilai = BigDecimal.ZERO;
                    BigDecimal utility;
                    if(!nilai.isEmpty()){
                        maxNilai = Collections.max(nilai);
                        minNilai = Collections.min(nilai);
                    }
                    BigDecimal divider = maxNilai.subtract(minNilai);
                    if(!divider.equals(BigDecimal.ZERO)){
                        utility = (((nilaiBobot.get().getKepemilikanKendaraan())).subtract(minNilai))
                                .divide(divider,2, RoundingMode.HALF_DOWN);
                    }else {
                        utility = BigDecimal.ZERO;
                    }
                    bobotUser.setKepemilikanKendaraan(utility);
                }
            });
            nilaiUtilityRepo.save(bobotUser);
        });
    }

    private void setNilaiAkhir(Long id,
                               List<Receiver> user,
                               List<String> criteria,
                               Long programId){
        user.forEach(a -> {
            NilaiAkhir bobotUser = new NilaiAkhir();
            bobotUser.setPenilaianId(id);
            bobotUser.setReceiverId(a.getId());
            criteria.forEach(b->{
                Optional<NilaiUtility> nilaiBobot = nilaiUtilityRepo.findByReceiverIdAndPenilaianId(a.getId(), id);
                if(Objects.equals(b,"Penghasilan")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getPenghasilan()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);

                    bobotUser.setPenghasilan(utility);
                }
                if(Objects.equals(b,"Status Tempat Tinggal")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getStatusTempatTinggal()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);

                    bobotUser.setStatusTempatTinggal(utility);
                }
                if(Objects.equals(b,"Pekerjaan")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getPekerjaan()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);

                    bobotUser.setPekerjaan(utility);
                }
                if(Objects.equals(b,"Status Pernikahan Kepala keluarga")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getStatusPerkawinan()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);

                    bobotUser.setStatusPerkawinan(utility);
                }
                if(Objects.equals(b,"Jumlah Tanggungan")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getJumlahTanggungan()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);

                    bobotUser.setJumlahTanggungan(utility);
                }
                if(Objects.equals(b,"Keadaan Rumah")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getKeadaanRumah()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);
                    bobotUser.setKeadaanRumah(utility);
                }
                if(Objects.equals(b,"Bahan Bakar Masak")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getBahanBakarHarian()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);
                    bobotUser.setBahanBakarHarian(utility);
                }
                if(Objects.equals(b,"Disabilitas")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getDisabilitas()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);
                    bobotUser.setDisabilitas(utility);
                }
                if(Objects.equals(b,"Tingkat Pendidikan")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getPendidikan()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);
                    bobotUser.setPendidikan(utility);
                }
                if(Objects.equals(b,"Fasilitas MCK")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getFasilitasMck()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);
                    bobotUser.setFasilitasMck(utility);
                }
                if(Objects.equals(b,"Kepemilikan Kendaraan")){
                    Optional<Criteria> criteriaData = criteriaRepo.findByTitle(b);
                    Optional<ProgramCriteria> getCriteriaWeight = programCriteriaRepo.findByProgramIdAndCriteriaId(programId,criteriaData.get().getId());

                    BigDecimal utility = nilaiBobot.get().getKepemilikanKendaraan()
                            .multiply(BigDecimal.valueOf(getCriteriaWeight.get().getWeight())
                                    .divide(BigDecimal.valueOf(100L),2, RoundingMode.HALF_DOWN)).setScale(2, RoundingMode.HALF_DOWN);
                    bobotUser.setKepemilikanKendaraan(utility);
                }
            });
            nilaiAkhirRepo.save(bobotUser);
        });
    }

    private void setRanking(Long id,
                            List<Receiver> user,
                            Long penerima){
        List<Ranking> rankings = new ArrayList<>();
        user.forEach(a -> {
            Ranking ranking = new Ranking();
            ranking.setPenilaianId(id);
            ranking.setReceiverId(a.getId());
            Optional<NilaiAkhir> getTotal = nilaiAkhirRepo.findByReceiverIdAndPenilaianId(a.getId(), id);
            BigDecimal total = BigDecimal.ZERO;
            if(getTotal.isPresent()){
                NilaiAkhir totalValue = getTotal.get();
                total = (totalValue.getUmur() == null ? BigDecimal.ZERO :totalValue.getUmur())
                        .add(totalValue.getPekerjaan() == null ? BigDecimal.ZERO :totalValue.getPekerjaan())
                        .add(totalValue.getPenghasilan() == null ? BigDecimal.ZERO :totalValue.getPenghasilan())
                        .add(totalValue.getStatusTempatTinggal() == null ? BigDecimal.ZERO :totalValue.getStatusTempatTinggal())
                        .add(totalValue.getStatusPerkawinan() == null ? BigDecimal.ZERO :totalValue.getStatusPerkawinan())
                        .add(totalValue.getJumlahTanggungan() == null ? BigDecimal.ZERO :totalValue.getJumlahTanggungan())
                        .add(totalValue.getKeadaanRumah() == null ? BigDecimal.ZERO :totalValue.getKeadaanRumah())
                        .add(totalValue.getDisabilitas() == null ? BigDecimal.ZERO :totalValue.getDisabilitas())
                        .add(totalValue.getPendidikan() == null ? BigDecimal.ZERO :totalValue.getPendidikan())
                        .add(totalValue.getFasilitasMck() == null ? BigDecimal.ZERO :totalValue.getFasilitasMck())
                        .add(totalValue.getBahanBakarHarian() == null ? BigDecimal.ZERO :totalValue.getBahanBakarHarian())
                        .add(totalValue.getKepemilikanKendaraan() == null ? BigDecimal.ZERO :totalValue.getKepemilikanKendaraan());

            }
            ranking.setTotal(total);
            rankings.add(ranking);
        });
        List<Ranking> rankedList = rankings.stream()
                .sorted(Comparator.comparing(Ranking::getTotal).reversed())
                .toList();

        for (int i = 0; i < rankedList.size(); i++) {
            rankedList.get(i).setRangking(i + 1);
            rankedList.get(i).setIsRanked(i < penerima);
            if( rankedList.get(i).getIsRanked().equals(Boolean.TRUE)){
                rankedList.get(i).setStatus("Yes");
            }else {
                rankedList.get(i).setStatus("No");
            }
        }
            rankingRepo.saveAll(rankedList);
        }
}
