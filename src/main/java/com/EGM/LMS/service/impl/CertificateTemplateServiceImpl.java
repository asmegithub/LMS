package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CertificateTemplateDTO;
import com.EGM.LMS.model.CertificateTemplate;
import com.EGM.LMS.repository.CertificateTemplateRepository;
import com.EGM.LMS.service.CertificateTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateTemplateServiceImpl implements CertificateTemplateService {
    private final CertificateTemplateRepository certificateTemplateRepository;

    @Override
    public CertificateTemplateDTO createCertificateTemplate(CertificateTemplateDTO certificateTemplate) {
        return toDto(certificateTemplateRepository.save(toEntity(certificateTemplate)));
    }

    @Override
    public List<CertificateTemplateDTO> getAllCertificateTemplates() {
        var templates = certificateTemplateRepository.findAll();
        var templateDtos = new java.util.ArrayList<CertificateTemplateDTO>();
        for (CertificateTemplate template : templates) {
            templateDtos.add(toDto(template));
        }
        return templateDtos;
    }

    @Override
    public CertificateTemplateDTO getCertificateTemplate(UUID certificateTemplateId) {
        return toDto(certificateTemplateRepository.findById(certificateTemplateId).orElseThrow());
    }

    @Override
    public CertificateTemplateDTO updateCertificateTemplate(UUID certificateTemplateId, CertificateTemplateDTO certificateTemplate) {
        certificateTemplateRepository.findById(certificateTemplateId).orElseThrow();
        var entity = toEntity(certificateTemplate);
        entity.setId(certificateTemplateId);
        return toDto(certificateTemplateRepository.save(entity));
    }

    @Override
    public void deleteCertificateTemplate(UUID certificateTemplateId) {
        certificateTemplateRepository.deleteById(certificateTemplateId);
    }

    private CertificateTemplate toEntity(CertificateTemplateDTO certificateTemplate) {
        return CertificateTemplate.builder()
                .name(certificateTemplate.getName())
                .description(certificateTemplate.getDescription())
                .templateHtml(certificateTemplate.getTemplateHtml())
                .templateCss(certificateTemplate.getTemplateCss())
                .backgroundUrl(certificateTemplate.getBackgroundUrl())
                .isDefault(certificateTemplate.getIsDefault())
                .isActive(certificateTemplate.getIsActive())
                .build();
    }

    private CertificateTemplateDTO toDto(CertificateTemplate certificateTemplate) {
        return CertificateTemplateDTO.builder()
                .id(certificateTemplate.getId())
                .name(certificateTemplate.getName())
                .description(certificateTemplate.getDescription())
                .templateHtml(certificateTemplate.getTemplateHtml())
                .templateCss(certificateTemplate.getTemplateCss())
                .backgroundUrl(certificateTemplate.getBackgroundUrl())
                .isDefault(certificateTemplate.getIsDefault())
                .isActive(certificateTemplate.getIsActive())
                .createdAt(certificateTemplate.getCreatedAt())
                .updatedAt(certificateTemplate.getUpdatedAt())
                .build();
    }
}
