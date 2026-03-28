package builder;

import builder.builders.DellDesktopBuilder;
import builder.builders.HpDesktopBuilder;
import builder.products.Desktop;

/**
 * Main entry point for the Classical Builder (Director-based).
 */
public class Main {
    public static void main(String[] args) {
        DesktopDirector director = new DesktopDirector();

        System.out.println("Building Dell Desktop...");
        Desktop dell = director.buildDesktop(new DellDesktopBuilder());
        dell.display();

        System.out.println("Building HP Desktop...");
        Desktop hp = director.buildDesktop(new HpDesktopBuilder());
        hp.display();
    }
}
