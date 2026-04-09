package before;

// Concrete Products
class DebugLogger implements ILogger {
    @Override
    public void log(String message) {
        System.out.println("[DEBUG] " + message);
    }
}

class InfoLogger implements ILogger {
    @Override
    public void log(String message) {
        System.out.println("[INFO] " + message);
    }
}

class ErrorLogger implements ILogger {
    @Override
    public void log(String message) {
        System.out.println("[ERROR] " + message);
    }
}
