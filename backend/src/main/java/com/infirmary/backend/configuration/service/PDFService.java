package com.infirmary.backend.configuration.service;

import com.infirmary.backend.configuration.dto.PrescriptionPDFDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
public class PDFService {

    private final Configuration freemarkerConfig;

    // Constructor injection with Qualifier to avoid multiple bean conflict
    public PDFService(@Qualifier("freemarkerConfiguration") Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public byte[] generatePrescriptionPdf(PrescriptionPDFDTO dto) throws Exception {
        // Load the FreeMarker template file
        Template template = freemarkerConfig.getTemplate("prescription.ftlh");

        // Fill the template with model data
        Map<String, Object> model = new HashMap<>();
        model.put("name", dto.getName());
        model.put("id", dto.getId());
        model.put("age", Period.between(dto.getDateOfBirth(), LocalDate.now()).getYears());
        model.put("school", dto.getSchool());
        model.put("date", dto.getDate());
        model.put("time", dto.getTime());
        model.put("designation", dto.getDesignation());
        model.put("residenceType", dto.getResidenceType());
        model.put("sex", dto.getSex());
        model.put("meds", dto.getMeds());
        model.put("diagnosis", dto.getDiagnosis());
        model.put("dietaryRemarks", dto.getDietaryRemarks());
        model.put("testNeeded", dto.getTestNeeded());
        model.put("doctorName", dto.getDoctorName());

        // Generate HTML content using FreeMarker
        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        // Convert HTML to PDF using OpenHTMLToPDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }
}
