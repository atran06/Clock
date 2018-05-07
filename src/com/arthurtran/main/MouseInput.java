package com.arthurtran.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener {

    private Display display;

    public MouseInput(Display display) {
        this.display = display;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if(display.getEntered()) {
            if(display.getOverEST()) {
                if(display.getTimeZoneE() == Display.TIMEZONE.pst) {
                    display.setHour(display.getLocalHour() + 3);
                    display.setHourAngle(display.getHour());
                    display.setTimeZoneE(Display.TIMEZONE.est);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
