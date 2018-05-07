package com.arthurtran.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

    private Display display;

    public KeyInput(Display display) {
        this.display = display;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_0) {
            display.setHour(display.getHour() + 3);
            display.setHourAngle(display.getHour());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
