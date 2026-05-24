import saga.SagaContext;
import saga.SagaOrchestrator;
import saga.SagaStep;
import saga.services.InventoryService;
import saga.services.PaymentService;
import saga.services.ShippingService;
import saga.steps.InventoryStep;
import saga.steps.PaymentStep;
import saga.steps.ShippingStep;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Saga Distributed Transaction Orchestrator Simulation ===");

        // 1. Initialize shared services
        PaymentService paymentService = new PaymentService();
        InventoryService inventoryService = new InventoryService();
        ShippingService shippingService = new ShippingService();

        // --- TEST 1: Success Case ---
        System.out.println("\n--- RUNNING SAGA TRANSACTION 1 (SUCCESS CASE) ---");
        SagaContext context1 = new SagaContext("saga-txn-1001");
        context1.set("user", "alice");
        context1.set("address", "123 High Street, London");

        List<SagaStep> steps1 = Arrays.asList(
                new PaymentStep(paymentService, 100.0),
                new InventoryStep(inventoryService, "item-101", 2),
                new ShippingStep(shippingService)
        );

        SagaOrchestrator orchestrator1 = new SagaOrchestrator(steps1, context1);
        boolean success1 = orchestrator1.executeSaga();
        System.out.printf("Saga 1 Transaction Result: %s\n", success1 ? "SUCCESS" : "FAILED");


        // --- TEST 2: Failure Case with Rollback ---
        System.out.println("\n--- RUNNING SAGA TRANSACTION 2 (SHIPPING FAIL -> ROLLBACK) ---");
        SagaContext context2 = new SagaContext("saga-txn-1002");
        context2.set("user", "alice");
        context2.set("address", "invalid-address"); // Will cause ShippingStep to fail

        List<SagaStep> steps2 = Arrays.asList(
                new PaymentStep(paymentService, 150.0),
                new InventoryStep(inventoryService, "item-101", 3),
                new ShippingStep(shippingService)
        );

        SagaOrchestrator orchestrator2 = new SagaOrchestrator(steps2, context2);
        boolean success2 = orchestrator2.executeSaga();
        System.out.printf("Saga 2 Transaction Result: %s\n", success2 ? "SUCCESS" : "FAILED");

        System.out.println("\n=== Saga Simulation Finished ===");
    }
}
