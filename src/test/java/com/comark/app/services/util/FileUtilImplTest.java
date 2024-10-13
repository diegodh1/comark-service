package com.comark.app.services.util;

import com.google.common.io.ByteStreams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.test.StepVerifier;

import java.io.IOException;

public class FileUtilImplTest {
    private FileUtilImpl budgetUtil;

    @BeforeEach
    void setup(){
        budgetUtil = new FileUtilImpl();
    }

    @Test
    public void shouldReadBudgetExcelFileSuccessfully() throws IOException {
        byte[] file = getBudgetFileFromResources("budget_test_file.xlsx");
        StepVerifier.create(budgetUtil.loadBudgetFromFile(file))
                .assertNext(budget -> Assertions.assertEquals(27, budget.size()))
                .verifyComplete();
    }

    @Test
    public void shouldReadBuildingBalanceExcelFileSuccessfully() throws IOException {
        byte[] file = getBuildingFileFromResources("building_test_file.xlsx");
        StepVerifier.create(budgetUtil.loadBuildingBalanceFromFile(file))
                .assertNext(budget -> Assertions.assertEquals(5, budget.size()))
                .verifyComplete();
    }

    @Test
    public void shouldThrowErrorsWhenBudgetFileIsNotValid() throws IOException {
        byte[] file = getBudgetFileFromResources("budget_test_with_errors.xlsx");
        StepVerifier.create(budgetUtil.loadBudgetFromFile(file))
                .verifyErrorMatches(ex -> ex instanceof IllegalArgumentException);
    }

    /**
     * Gets a file from the resources folder and returns it as a File object.
     *
     * @param fileName The path to the resource file relative to the resources' folder.
     * @return A File object representing the resource file.
     * @throws Exception If the file cannot be created or accessed.
     */
    public static byte[] getBudgetFileFromResources(String fileName) throws IOException {
        // Load the resource file using ClassLoader
        return ByteStreams.toByteArray(new ClassPathResource(String.format("budget/%s", fileName)).getInputStream());
    }

    public static byte[] getBuildingFileFromResources(String fileName) throws IOException {
        // Load the resource file using ClassLoader
        return ByteStreams.toByteArray(new ClassPathResource(String.format("building_balance/%s", fileName)).getInputStream());
    }
}
