package com.comark.app.services.util;

import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.ImmutableBuildingBalance;
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
import java.util.UUID;

@Component
public class FileUtilImpl implements FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilImpl.class);

    @Override
    public Mono<List<PresupuestoItemDto>> loadBudgetFromFile(byte[] fileBytes) {
        return loadFile(fileBytes);
    }

    @Override
    public Mono<List<BuildingBalance>> loadBuildingBalanceFromFile(byte[] fileBytes) {
        List<BuildingBalance> buildingBalanceList = new ArrayList<>();
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
                    var item = ImmutableBuildingBalance
                            .builder()
                            .id(UUID.randomUUID().toString())
                            .apartmentNumber(row.getCell(0).getStringCellValue())
                            .date(row.getCell(1).getDateCellValue().toInstant().toEpochMilli())
                            .administrationCharge(row.getCell(2).getNumericCellValue())
                            .monthCharge(row.getCell(3).getNumericCellValue())
                            .interestRate(row.getCell(4).getNumericCellValue())
                            .interestCharge(row.getCell(5).getNumericCellValue())
                            .interestBalance(row.getCell(6).getNumericCellValue())
                            .additionalCharge(row.getCell(7).getNumericCellValue())
                            .penaltyCharge(row.getCell(8).getNumericCellValue())
                            .legalCharge(row.getCell(9).getNumericCellValue())
                            .otherCharge(row.getCell(10).getNumericCellValue())
                            .totalToPaid(row.getCell(11).getNumericCellValue())
                            .discount(row.getCell(12).getNumericCellValue())
                            .lastPaid(row.getCell(13).getNumericCellValue())
                            .finalCharge(row.getCell(14).getNumericCellValue())
                            .build();
                    buildingBalanceList.add(item);
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
        return Mono.just(buildingBalanceList);
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
