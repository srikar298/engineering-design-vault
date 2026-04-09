package workers;

import database.InventoryDatabase;

public class ReaderTask implements Runnable {
    private final InventoryDatabase database;

    public ReaderTask(InventoryDatabase database) {
        this.database = database;
    }

    @Override
    public void run() {
        database.readStock();
    }
}
