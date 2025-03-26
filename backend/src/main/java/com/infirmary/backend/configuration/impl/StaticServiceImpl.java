package com.infirmary.backend.configuration.impl;

import java.io.IOException;
import org.springframework.util.StreamUtils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.infirmary.backend.configuration.service.StaticService;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class StaticServiceImpl implements StaticService {

    @Override
    public byte[] imageReturn(String filename) {
        try {
            // normalize: replace all dots (except the last one) with underscores
            int lastDotIndex = filename.lastIndexOf('.');
            String namePart = filename.substring(0, lastDotIndex).replace(".", "_");
            String ext = filename.substring(lastDotIndex); // .jpeg or .png
            String safeFilename = namePart + ext;
    
            ClassPathResource imgFile = new ClassPathResource("static/Profile/" + safeFilename);
            return StreamUtils.copyToByteArray(imgFile.getInputStream());
    
        } catch (IOException e) {
            throw new RuntimeException("Image not found: " + filename);
        }
    }
    
}
