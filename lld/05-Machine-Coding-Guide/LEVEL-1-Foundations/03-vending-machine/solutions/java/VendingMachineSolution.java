package vending;

public class VendingMachineSolution {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.addProduct(new Product("Coke", 1.5), 10);

        vm.insertMoney(2.0);
        vm.selectProduct("Coke");
    }
}
