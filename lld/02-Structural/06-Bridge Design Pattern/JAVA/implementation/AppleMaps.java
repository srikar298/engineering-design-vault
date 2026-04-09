package implementation;

public class AppleMaps implements NavigationImpl {
    @Override
    public void navigateTo(String destination) {
        System.out.println("Routing to [" + destination + "] prioritizing privacy via Apple Maps API... 🍏");
    }
}
