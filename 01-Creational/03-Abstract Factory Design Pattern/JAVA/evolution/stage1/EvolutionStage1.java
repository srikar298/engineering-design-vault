package evolution.stage1;

/**
 * Stage 1: The Factory Method Limitation
 * 
 * We have separate Factory Methods for each product.
 * While this follows OCP for each product type, there is no "Contract"
 * that enforces they must come from the same family.
 */

interface IButton {}
class WindowsButton implements IButton { @Override public String toString() { return "Windows Button"; } }
class MacButton implements IButton { @Override public String toString() { return "Mac Button"; } }

interface IButtonFactory { IButton create(); }
class WindowsButtonFactory implements IButtonFactory { public IButton create() { return new WindowsButton(); } }
class MacButtonFactory implements IButtonFactory { public IButton create() { return new MacButton(); } }

interface ICheckbox {}
class WindowsCheckbox implements ICheckbox { @Override public String toString() { return "Windows Checkbox"; } }
class MacCheckbox implements ICheckbox { @Override public String toString() { return "Mac Checkbox"; } }

interface ICheckboxFactory { ICheckbox create(); }
class WindowsCheckboxFactory implements ICheckboxFactory { public ICheckbox create() { return new WindowsCheckbox(); } }
class MacCheckboxFactory implements ICheckboxFactory { public ICheckbox create() { return new MacCheckbox(); } }

class Main {
    public static void main(String[] args) {
        // We have to manage two separate factory hierarchies.
        IButtonFactory btnFactory = new WindowsButtonFactory();
        ICheckboxFactory chkFactory = new MacCheckboxFactory(); // Still possible to mix!
        
        IButton btn = btnFactory.create();
        ICheckbox chk = chkFactory.create();
        
        System.out.println("Created: " + btn + " and " + chk);
        System.out.println("Stage 1: Logic is decoupled, but consistency is still NOT enforced.");
    }
}
