package com.nexign;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import static org.hamcrest.MatcherAssert.assertThat;

public class DownloadFileTest {

    ClassLoader cl = DownloadFileTest.class.getClassLoader();
    String csvName = "csv_example.csv",
            xlsName = "xls_example.xlsx",
            xlsCellExpValue = "Brave New World",
            pdfName = "pdf_example.pdf",
            zipName = "fileExamples.zip";

  @Test
    void zipParsingTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/" + zipName));
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream(zipName))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(csvName)) {
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                            List<String[]> csvRows = reader.readAll();
                            org.assertj.core.api.Assertions.assertThat(csvRows).contains(
                                    new String[]{"Hello;QA;Guru"},
                                    new String[]{"Am;I;CSV"}
                            );
                        }
                    }
                }
                if (entry.getName().equals(pdfName)) {
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        PDF pdf = new PDF(inputStream);
                        assertThat(pdf, new ContainsExactText("123"));
                        Assertions.assertEquals(166, pdf.numberOfPages);
                    }
                }
                if (entry.getName().equals(xlsName)) {
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        XLS xls = new XLS(inputStream);
                        String cellValue = xls.excel.getSheetAt(0).getRow(3).getCell(0)
                                .getStringCellValue();
                        org.assertj.core.api.Assertions.assertThat(cellValue).contains(xlsCellExpValue);
                    }
                }
            }
        }
    }
}
