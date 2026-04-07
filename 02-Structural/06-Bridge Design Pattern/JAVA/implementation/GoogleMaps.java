package implementation;

public class GoogleMaps implements NavigationImpl {
    @Override
    public void navigateTo(String destination) {
        System.out.println("Calculating optimal route to [" + destination + "] using Google Maps API... 📡");
    }
}
