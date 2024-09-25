package com.comark.app.services.util;

import com.google.common.io.ByteStreams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.test.StepVerifier;

import java.io.IOException;

public class BudgetUtilImplTest {
    private BudgetUtilImpl budgetUtil;

    @BeforeEach
    void setup(){
        budgetUtil = new BudgetUtilImpl();
    }

    @Test
    public void shouldReadExcelFileSuccessfully() throws IOException {
        byte[] file = getFileFromResources("budget_test_file.xlsx");
        StepVerifier.create(budgetUtil.loadBudgetFromFile(file))
                .assertNext(budget -> Assertions.assertEquals(27, budget.size()))
                .verifyComplete();
    }

    @Test
    public void shouldThrowErrorsWhenFileIsNotValid() throws IOException {
        byte[] file = getFileFromResources("budget_test_with_errors.xlsx");
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
    public static byte[] getFileFromResources(String fileName) throws IOException {
        // Load the resource file using ClassLoader
        return ByteStreams.toByteArray(new ClassPathResource(String.format("budget/%s", fileName)).getInputStream());
    }
}
