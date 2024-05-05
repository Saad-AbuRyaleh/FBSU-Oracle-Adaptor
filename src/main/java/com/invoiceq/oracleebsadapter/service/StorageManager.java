package com.invoiceq.oracleebsadapter.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageManager {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${invoices.filepath}")
    private String invoicesFilePath;


    public boolean writeFileToStorage(String fileName, byte[] bytes) {
        LOGGER.info("local is working");
        boolean flag = false;
        try {
            FileUtils.writeByteArrayToFile(new File(invoicesFilePath + fileName), bytes);
            LOGGER.info("File written successfully at{}", invoicesFilePath + fileName);
            flag = true;

        } catch (IOException e) {
            LOGGER.error("Failed To Write Or Create New PDF file for file Path {} and file Name {}", invoicesFilePath, fileName, e);
        }
        return flag;
    }

}
