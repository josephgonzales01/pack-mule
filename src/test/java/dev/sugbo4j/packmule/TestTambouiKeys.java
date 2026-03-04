package dev.sugbo4j.packmule;

import dev.tamboui.tui.event.KeyCode;
import dev.tamboui.tui.event.KeyEvent;
import java.lang.reflect.Method;

public class TestTambouiKeys {
    public static void main(String[] args) {
        System.out.println("KeyCodes:");
        for (Object enumConstant : KeyCode.class.getEnumConstants()) {
            System.out.print(enumConstant + ", ");
        }
        System.out.println("\n\nKeyEvent Methods:");
        for (Method m : KeyEvent.class.getMethods()) {
            if (m.getDeclaringClass() == KeyEvent.class) {
                System.out.println(m.getName());
            }
        }
    }
}
