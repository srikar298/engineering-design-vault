package smarthome.platforms;

public class GoogleNestAPI implements IPlatform {
    @Override
    public void executeAction(String action, String value) {
        System.out.println("   [Google Nest API] Sending HTTP POST -> /api/v1/" + action + "?val=" + value);
    }
}
