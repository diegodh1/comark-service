package com.comark.app.services.util;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.ImmutableBuildingBalance;
import com.comark.app.model.dto.residentialComplex.ImmutableResidentialComplexItemOwnerDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemOwnerDto;
import com.comark.app.model.enums.Frecuencia;
import com.comark.app.model.dto.budget.ImmutablePresupuestoItemDto;
import com.comark.app.model.dto.budget.PresupuestoItemDto;
import com.comark.app.model.enums.IdentificationType;
import com.comark.app.model.enums.PresupuestoTipo;
import com.comark.app.model.enums.ResidentialComplexType;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.keycloak.authorization.client.util.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.*;

@Component
public class FileUtilImpl implements FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtilImpl.class);

    @Override
    public Mono<List<PresupuestoItemDto>> loadBudgetFromFile(byte[] fileBytes) {
        return loadFile(fileBytes);
    }

    @Override
    public Mono<List<BuildingBalance>> loadBuildingBalanceFromFile(byte[] fileBytes,String residentialComplexId) {
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
                            .lastBalance(row.getCell(10).getNumericCellValue())
                            .otherCharge(row.getCell(11).getNumericCellValue())
                            .totalToPaid(row.getCell(12).getNumericCellValue())
                            .discount(row.getCell(13).getNumericCellValue())
                            .lastPaid(row.getCell(14).getNumericCellValue())
                            .finalCharge(row.getCell(15).getNumericCellValue())
                            .residentialComplexId(residentialComplexId)
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

    @Override
    public Mono<List<ResidentialComplexItemOwnerDto>> loadResidentialComplexItemOwnerFromFile(byte[] fileBytes) {
        List<ResidentialComplexItemOwnerDto> response = new ArrayList<>();
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
                    var item = ImmutableResidentialComplexItemOwnerDto
                            .builder()
                            .buildingNumber(row.getCell(0).getStringCellValue())
                            .residentialComplexCoefficient(row.getCell(1).getNumericCellValue())
                            .description(Optional.ofNullable(row.getCell(2)).map(Cell::getStringCellValue).orElse(""))
                            .type(ResidentialComplexType.valueOf(row.getCell(3).getStringCellValue()))
                            .parkingNumber(row.getCell(4).getStringCellValue())
                            .parkingNumberCoefficient(row.getCell(5).getNumericCellValue())
                            .storageRoomNumber(row.getCell(6).getStringCellValue())
                            .coefficientStorageRoomNumber(row.getCell(7).getNumericCellValue())
                            .rentPrice(row.getCell(8).getNumericCellValue())
                            .capacity((int) row.getCell(9).getNumericCellValue())
                            .restrictions(Optional.ofNullable(row.getCell(10)).map(Cell::getStringCellValue).orElse(""))
                            .identificationNumber(row.getCell(11).getStringCellValue())
                            .identificationType(IdentificationType.valueOf(row.getCell(12).getStringCellValue()))
                            .ownerName(row.getCell(13).getStringCellValue())
                            .ownerLastName(Optional.ofNullable(row.getCell(14)).map(Cell::getStringCellValue).orElse(""))
                            .ownerPhoneNumber(Optional.ofNullable(row.getCell(15)).map(Cell::getStringCellValue).orElse(""))
                            .ownerEmail(row.getCell(16).getStringCellValue())
                            .build();
                    response.add(item);
                } catch (Exception e) {
                    String message = String.format("Couldn't process row %d, error: %s" , row.getRowNum() + 1, e.getMessage());
                    LOGGER.error(message);
                    errors.add(message);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return Mono.error(new ComarkAppException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage()));
        }
        if(!errors.isEmpty()){
            return Mono.error(new ComarkAppException(HttpStatus.SC_BAD_REQUEST, errors.toString()));
        }
        return Mono.just(response);
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
