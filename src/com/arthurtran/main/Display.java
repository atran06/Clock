package com.arthurtran.main;

import com.arthurtran.Arch2D.main.Engine;
import com.arthurtran.Arch2D.main.Window;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
A clock based in Java swing based on local time.
(c) Arthur Tran. 2018
 */

public class Display extends Canvas implements Runnable, Engine {

    private Window window;
    private Thread thread;

    private double angle = 0;
    private double minAngle = 0;
    private double hourAngle = 0;

    private long beforeTime = System.currentTimeMillis();

    private int hours;
    private int minutes;
    private int seconds;

    public Display(int hours, int minutes, int seconds) {
        window = new Window(500, 500, this);
        window.createWindowWindowed();
        window.getFrame().setTitle("Clock | Arthur Tran");

        thread = new Thread(this);
        thread.start();

        String time = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;

        angle = this.seconds * 6;
        minAngle = this.minutes * 6;
        hourAngle = this.hours * 30 + ((int) (minAngle / 60)) * 5;
    }

    public Display() {
        window = new Window(700, 700, this);
        window.createWindowWindowed();
        window.getFrame().setTitle("Clock | Arthur Tran");

        thread = new Thread(this);
        thread.start();

        String time = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        this.hours = Integer.parseInt(time.substring(0, 2));
        if(this.hours > 12) {
            this.hours -= 12;
        }
        this.minutes = Integer.parseInt(time.substring(2, 4));
        this.seconds = Integer.parseInt(time.substring(4, 6));

        angle = this.seconds * 6;
        minAngle = this.minutes * 6;
        hourAngle = this.hours * 30 + ((int) (minAngle / 60)) * 5;
    }

    @Override
    public void run() {
        this.runGame();
    }

    @Override
    public void tick() {

        if(System.currentTimeMillis() - beforeTime >= 1000) {
            seconds++;
            angle += 6;

            if(seconds == 60) {
                angle = 0;
                seconds = 0;
                minutes++;
                minAngle += 6;
            }
            if(minutes == 60) {
                minAngle = 0;
                minutes = 0;
                hours++;
                if(minutes % 10 == 0) {
                    hourAngle += 5;
                }
            }
            if(hours > 12) {
                hours = 1;
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    @Override
    public void render() {
        BufferStrategy strat = this.getBufferStrategy();
        if(strat == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = strat.getDrawGraphics();
        Graphics2D g2 = (Graphics2D) g;

        double x = (int) window.getFrameWidth() / 2;
        double y = (int) window.getFrameHeight() / 2;

        g2.fill(new Rectangle2D.Double(0, 0, window.getFrameWidth(), window.getFrameHeight()));

        double deg = Math.toRadians(angle - 90);
        double deg2 = Math.toRadians(minAngle - 90);
        double deg3 = Math.toRadians(hourAngle - 90);
        double deg4 = Math.toRadians(0);

        double x2 = 200 * Math.cos(deg);
        double y2 = 200 * Math.sin(deg);
        double x2Min = 250 * Math.cos(deg2);
        double y2Min = 250 * Math.sin(deg2);
        double x2Hour = 125 * Math.cos(deg3);
        double y2Hour = 125 * Math.sin(deg3);

        double zeroXSec = 200 * Math.cos(deg4);
        double zeroYSec = 200 * Math.sin(deg4);

        g2.setStroke(new BasicStroke(9.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.setColor(Color.magenta);
        g2.draw(new Line2D.Double(x - 4, y - 4, x2Min - 4 + x, y2Min - 4 + y));
        g2.draw(new Arc2D.Double(new Rectangle2D.Double(x - 250 - 4, y - 250 - 4, 500, 500),
                90, -minAngle, Arc2D.OPEN));

        g.setColor(Color.cyan);
        g2.draw(new Line2D.Double(x - 4, y - 4, x2Hour - 4 + x, y2Hour - 4 + y));
        g2.draw(new Arc2D.Double(new Rectangle2D.Double(x - 243 - 4, y - 243 - 4, 486, 486),
                90, -hourAngle, Arc2D.OPEN));

        g.setColor(Color.white);
        g2.draw(new Line2D.Double(x - 4, y - 4, x2 - 4 + x, y2 - 4 + y));

        g2.draw(new Arc2D.Double(new Rectangle2D.Double(x - 236 - 4, y - 236 - 4, 472, 472),
                90, -angle, Arc2D.OPEN));

        String time = hours + ":" + minutes + ":" + seconds;
        if(minutes < 10) {
            time = hours + ":0" + minutes + ":" + seconds;
        }
        if(seconds < 10) {
            time = hours + ":" + minutes + ":0" + seconds;
        }
        if(hours < 10) {
            time = "0" + hours + ":" + minutes + ":" + seconds;
        }
        if(hours < 10 && minutes < 10 && seconds < 10) {
            time = "0" + hours + ":0" + minutes + ":0" + seconds;
        } else if(hours < 10 && minutes < 10) {
            time = "0" + hours + ":0" + minutes + ":" + seconds;
        } else if(hours < 10 && seconds < 10) {
            time = "0" + hours + ":" + minutes + ":0" + seconds;
        } else if(minutes < 10 && seconds < 10) {
            time = hours + ":0" + minutes + ":0" + seconds;
        }

        g.setFont(new Font("arial", Font.BOLD, 50));
        g2.drawString(time, (int) ((window.getFrameWidth() / 2) - (g.getFontMetrics().stringWidth(time)) / 2),
                (int) (window.getFrameHeight() - g.getFontMetrics().getAscent()));

        g.dispose();
        strat.show();
    }
}
