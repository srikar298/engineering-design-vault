package addons.encoding;

/**
 * <h1>14 - JSON vs. Binary Serialization (DDIA Mastery)</h1>
 * 
 * <b>Scenario:</b> You are sending 10,000 Order objects per second between 
 * microservices. JSON is human-readable but slow and consumes 3x more bandwidth 
 * than Binary (Protobuf/Thrift).
 * 
 * <b>Senior SDE-2 Insights (The DDIA Perspective):</b>
 * 1. <b>Payload Size:</b> Binary formats don't repeat field names (e.g., "orderId") 
 *    in every message. They use a pre-defined Schema.
 * 2. <b>Parsing Speed:</b> Parsing a string (JSON) into an object requires complex 
 *    string manipulation. Binary decoding is just memory mapping.
 * 3. <b>Schema Evolution:</b> Binary formats allow adding/removing fields 
 *    safely while maintaining backward compatibility.
 */

class Order {
    int id = 12345;
    double amount = 99.99;
    String status = "PENDING";
}

public class SerializationSimulatorSDE2 {
    public static void main(String[] args) {
        Order myOrder = new Order();

        // --- SIMULATED JSON (Standard) ---
        String json = "{ \"id\": " + myOrder.id + ", \"amount\": " + myOrder.amount + ", \"status\": \"" + myOrder.status + "\" }";
        int jsonSize = json.length();

        // --- SIMULATED BINARY (DDIA Style) ---
        // Fields are mapped to tags (id=1, amount=2, status=3)
        // Values are stored directly without keys.
        byte[] binary = { 1, 0, 0, 48, 57, 2, 0, 0, 0, 99, 3, 'P' }; // Simplified simulation
        int binarySize = binary.length;

        System.out.println("--- Serialization Efficiency ---");
        System.out.println("JSON Size: " + jsonSize + " bytes");
        System.out.println("Binary Size: " + binarySize + " bytes");
        System.out.println("Savings: " + (100 - (binarySize * 100 / jsonSize)) + "% reduction in network bandwidth.");

        System.out.println("\nSenior Signal: 'For our internal high-throughput services, we avoid JSON " +
                           "parsing overhead by using Protobuf, reducing our P99 latency by 30%.'");
    }
}
