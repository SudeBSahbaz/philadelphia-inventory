package com.philadelphia.inventory.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.ArtifactChangeLog;
import com.philadelphia.inventory.entity.User;
import com.philadelphia.inventory.entity.enums.ChangeType;
import com.philadelphia.inventory.repository.ArtifactChangeLogRepository;

@Service
public class ArtifactChangeLogService {

    private final ArtifactChangeLogRepository changeLogRepository;

    public ArtifactChangeLogService(
            ArtifactChangeLogRepository changeLogRepository
    ) {
        this.changeLogRepository = changeLogRepository;
    }

    public List<ArtifactChangeLog> getArtifactHistory(Long artifactId) {
        return changeLogRepository
                .findAllByArtifactIdOrderByChangedAtDesc(artifactId);
    }

    public ArtifactChangeLog recordCreated(
            Artifact artifact,
            User user
    ) {
        ArtifactChangeLog log = new ArtifactChangeLog(
                artifact,
                user,
                ChangeType.CREATED
        );

        return changeLogRepository.save(log);
    }

    public Optional<ArtifactChangeLog> recordUpdated(
            Artifact oldArtifact,
            Artifact newArtifact,
            User user
    ) {
        ArtifactChangeLog log = new ArtifactChangeLog(
                oldArtifact,
                user,
                ChangeType.UPDATED
        );

        addIfChanged(
                log,
                "Buluntu Kodu",
                oldArtifact.getArtifactCode(),
                newArtifact.getArtifactCode()
        );

        addIfChanged(
                log,
                "Tür",
                oldArtifact.getType(),
                newArtifact.getType()
        );

        addIfChanged(
                log,
                "No",
                oldArtifact.getFormNo(),
                newArtifact.getFormNo()
        );

        addIfChanged(
                log,
                "Env. No",
                oldArtifact.getInventoryNo(),
                newArtifact.getInventoryNo()
        );

        addIfChanged(
                log,
                "Etüd No",
                oldArtifact.getStudyNo(),
                newArtifact.getStudyNo()
        );

        addIfChanged(
                log,
                "Torba No",
                oldArtifact.getBagNo(),
                newArtifact.getBagNo()
        );

        addIfChanged(
                log,
                "Kasa No",
                oldArtifact.getBoxNo(),
                newArtifact.getBoxNo()
        );

        addIfChanged(
                log,
                "Derinlik",
                oldArtifact.getDepth(),
                newArtifact.getDepth()
        );

        addIfChanged(
                log,
                "Kasa",
                oldArtifact.getBox(),
                newArtifact.getBox()
        );

        addIfChanged(
                log,
                "Buluntu Yeri",
                oldArtifact.getFindLocation(),
                newArtifact.getFindLocation()
        );

        addIfChanged(
                log,
                "Mevki",
                oldArtifact.getLocality(),
                newArtifact.getLocality()
        );

        addIfChanged(
                log,
                "Sektör",
                oldArtifact.getSector(),
                newArtifact.getSector()
        );

        addIfChanged(
                log,
                "Buluntu Tarihi",
                oldArtifact.getFindDate(),
                newArtifact.getFindDate()
        );

        addIfChanged(
                log,
                "Buluntu Yılı",
                oldArtifact.getFindYear(),
                newArtifact.getFindYear()
        );

        addIfChanged(
                log,
                "Alan",
                oldArtifact.getArea(),
                newArtifact.getArea()
        );

        addIfChanged(
                log,
                "Form",
                oldArtifact.getForm(),
                newArtifact.getForm()
        );

        addIfChanged(
                log,
                "Bezeme Türü",
                oldArtifact.getDecorationType(),
                newArtifact.getDecorationType()
        );

        addIfChanged(
                log,
                "Hamur Yapısı",
                oldArtifact.getPasteStructure(),
                newArtifact.getPasteStructure()
        );

        addIfChanged(
                log,
                "Pişme",
                oldArtifact.getFiring(),
                newArtifact.getFiring()
        );

        addIfChanged(
                log,
                "Teknik",
                oldArtifact.getTechnique(),
                newArtifact.getTechnique()
        );

        addIfChanged(
                log,
                "Katkı",
                oldArtifact.getTemper(),
                newArtifact.getTemper()
        );

        addIfChanged(
                log,
                "Katkı Miktarı",
                oldArtifact.getTemperAmount(),
                newArtifact.getTemperAmount()
        );

        addIfChanged(
                log,
                "Astar Yapısı",
                oldArtifact.getSlipStructure(),
                newArtifact.getSlipStructure()
        );

        addIfChanged(
                log,
                "Açı",
                oldArtifact.getAngle(),
                newArtifact.getAngle()
        );

        addIfChanged(
                log,
                "Dönem",
                oldArtifact.getPeriod(),
                newArtifact.getPeriod()
        );

        addIfChanged(
                log,
                "Cinsi",
                oldArtifact.getKind(),
                newArtifact.getKind()
        );

        addIfChanged(
                log,
                "Munsell",
                oldArtifact.getMunsell(),
                newArtifact.getMunsell()
        );

        addIfChanged(
                log,
                "Çap",
                oldArtifact.getDiameter(),
                newArtifact.getDiameter()
        );

        addIfChanged(
                log,
                "Ağ.",
                oldArtifact.getWeight(),
                newArtifact.getWeight()
        );

        addIfChanged(
                log,
                "Uz.",
                oldArtifact.getLength(),
                newArtifact.getLength()
        );

        addIfChanged(
                log,
                "Gen.",
                oldArtifact.getWidth(),
                newArtifact.getWidth()
        );

        addIfChanged(
                log,
                "Kal.",
                oldArtifact.getThickness(),
                newArtifact.getThickness()
        );

        addIfChanged(
                log,
                "Çiz No",
                oldArtifact.getDrawingNo(),
                newArtifact.getDrawingNo()
        );

        addIfChanged(
                log,
                "Kor. Kısım",
                oldArtifact.getPreservedPart(),
                newArtifact.getPreservedPart()
        );

        addIfChanged(
                log,
                "Madde",
                oldArtifact.getMaterial(),
                newArtifact.getMaterial()
        );

        addIfChanged(
                log,
                "Üretim Yeri",
                oldArtifact.getProductionPlace(),
                newArtifact.getProductionPlace()
        );

        addIfChanged(
                log,
                "Açıklama",
                oldArtifact.getDescription(),
                newArtifact.getDescription()
        );

        addIfChanged(
                log,
                "Bibliyografya",
                oldArtifact.getBibliography(),
                newArtifact.getBibliography()
        );

        addIfChanged(
                log,
                "Görünürlük",
                oldArtifact.getVisibility(),
                newArtifact.getVisibility()
        );

        if (log.getFieldChanges().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(changeLogRepository.save(log));
    }

    public ArtifactChangeLog recordPhotoAdded(
            Artifact artifact,
            User user,
            String photoNo
    ) {
        ArtifactChangeLog log = new ArtifactChangeLog(
                artifact,
                user,
                ChangeType.PHOTO_ADDED
        );

        log.addFieldChange(
                "Fotoğraf",
                null,
                photoNo
        );

        return changeLogRepository.save(log);
    }

    public ArtifactChangeLog recordPhotoRemoved(
            Artifact artifact,
            User user,
            String photoNo
    ) {
        ArtifactChangeLog log = new ArtifactChangeLog(
                artifact,
                user,
                ChangeType.PHOTO_REMOVED
        );

        log.addFieldChange(
                "Fotoğraf",
                photoNo,
                null
        );

        return changeLogRepository.save(log);
    }

    public ArtifactChangeLog recordDeleted(
            Artifact artifact,
            User user
    ) {
        ArtifactChangeLog log = new ArtifactChangeLog(
                artifact,
                user,
                ChangeType.DELETED
        );

        return changeLogRepository.save(log);
    }

    public ArtifactChangeLog recordRestored(
            Artifact artifact,
            User user
    ) {
        ArtifactChangeLog log = new ArtifactChangeLog(
                artifact,
                user,
                ChangeType.RESTORED
        );

        return changeLogRepository.save(log);
    }

    private void addIfChanged(
            ArtifactChangeLog log,
            String fieldName,
            Object oldValue,
            Object newValue
    ) {
        if (!Objects.equals(oldValue, newValue)) {
            log.addFieldChange(
                    fieldName,
                    valueToString(oldValue),
                    valueToString(newValue)
            );
        }
    }

    private String valueToString(Object value) {
        return value == null ? null : value.toString();
    }
}