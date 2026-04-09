package workers;

import database.InventoryDatabase;

public class WriterTask implements Runnable {
    private final InventoryDatabase database;
    private final int newStock;

    public WriterTask(InventoryDatabase database, int newStock) {
        this.database = database;
        this.newStock = newStock;
    }

    @Override
    public void run() {
        database.updateStock(newStock);
    }
}
