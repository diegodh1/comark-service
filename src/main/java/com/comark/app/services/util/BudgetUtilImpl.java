package com.comark.app.services.util;

import com.comark.app.model.enums.Frecuencia;
import com.comark.app.model.dto.budget.ImmutablePresupuestoItemDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.PresupuestoTipo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class BudgetUtilImpl implements BudgetUtil{
    private static final Logger LOGGER = LoggerFactory.getLogger(BudgetUtilImpl.class);

    @Override
    public Mono<List<PresupuestoItemDto>> loadBudgetFromFile(byte[] fileBytes) {
        return loadFile(fileBytes);
    }

    private Mono<List<PresupuestoItemDto>> loadFile(byte[] fileBytes) {
        List<PresupuestoItemDto> budget = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes)) {
            // Try to create a workbook from the byte array
            Workbook workbook = new XSSFWorkbook(byteArrayInputStream); // For .xlsx files
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip the header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // Process each row
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    if(row.getCell(0) == null || row.getCell(0).getStringCellValue().isEmpty()) break;
                    PresupuestoItemDto item = ImmutablePresupuestoItemDto
                            .builder()
                            .tipo(PresupuestoTipo.getPresupuestoTipo(row.getCell(0).getStringCellValue()))
                            .nombre(row.getCell(1).getStringCellValue())
                            .detalle(row.getCell(2).getStringCellValue())
                            .presupuesto(row.getCell(3).getNumericCellValue())
                            .frecuencia(Frecuencia.fromValue((int) row.getCell(4).getNumericCellValue()))
                            .fechaInicio(row.getCell(5).getDateCellValue())
                            .cuentaContableId(row.getCell(6).getStringCellValue())
                            .build();
                    budget.add(item);
                } catch (Exception e) {
                    String message = String.format("Couldn't process row %d, error: %s" , row.getRowNum() + 1, e.getMessage());
                    LOGGER.error(message);
                    errors.add(message);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return Mono.error(new IllegalArgumentException(e));
        }
        if(!errors.isEmpty()){
            return Mono.error(new IllegalArgumentException(String.format("errors: %s", errors)));
        }
        return Mono.just(budget);
    }
}
