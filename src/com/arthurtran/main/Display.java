package com.arthurtran.main;

import com.arthurtran.Arch2D.main.Engine;
import com.arthurtran.Arch2D.main.Window;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

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
    private long beforeTimeAlph = System.currentTimeMillis();

    private int hours;
    private int minutes;
    private int seconds;
    private int localHour;

    private boolean entered = false;
    private boolean enteredSettings = false;
    private boolean overEST = false;
    private boolean overPST = true;

    private float alph = 0f;
    private float alphDigital = 0f;
    private float alphSettings = 0f;
    private float alphSettings2 = 0f;

    private TimeZone tz;

    public enum TIMEZONE {
        pst, est
    }

    private TIMEZONE timeZone;

    public Display(int hours, int minutes, int seconds) {
        window = new Window(700, 700, this);
        window.createWindowWindowed();
        window.getFrame().setTitle("Clock | Arthur Tran");

        thread = new Thread(this);
        thread.start();

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

        this.addKeyListener(new KeyInput(this));
        this.addMouseListener(new MouseInput(this));

        tz = Calendar.getInstance().getTimeZone();
        if(tz.getDisplayName().equalsIgnoreCase("Pacific Standard Time")) {
            timeZone = TIMEZONE.pst;
        }

        String time = new SimpleDateFormat("HHmmss").format(Calendar.getInstance().getTime());

        this.hours = Integer.parseInt(time.substring(0, 2));
        if(this.hours > 12) {
            this.hours -= 12;
        }
        this.localHour = Integer.parseInt(time.substring(0, 2));
        if(this.localHour > 12) {
            this.localHour -= 12;
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

        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        int x = mousePos.x;
        int y = mousePos.y;

        if(x > window.getFrame().getLocation().x && x < window.getFrame().getLocation().x + window.getFrameWidth()
                && y > window.getFrame().getLocation().y + 30 && y < window.getFrame().getLocation().y + window.getFrameHeight()) {
            entered = true;

            if(x > window.getFrame().getLocation().x && x < window.getFrame().getLocation().x + 55) {
                if(y > window.getFrame().getLocation().y + 30 && y < window.getFrame().getLocation().y + 85) {
                    enteredSettings = true;
                    overPST = true;
                } else {
                    overPST = false;
                }
                if(y > window.getFrame().getLocation().y + 105 && y < window.getFrame().getLocation().y + 155) {
                    overEST = true;
                } else {
                    overEST = false;
                }

            } else {
                enteredSettings = false;
            }
        } else {
            entered = false;
            enteredSettings = false;
        }

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

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawClock(g, g2);

        alphaTransition();

        g2.setColor(Color.black);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alph));
        g2.fill(new Rectangle2D.Double(0, 0, window.getFrameWidth(), window.getFrameHeight()));

        g.setColor(Color.white);
        g.setFont(new Font("arial", Font.BOLD, 20));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphSettings));

        g2.drawString(tz.getDisplayName(), 60, 35);
        g2.fill(new Rectangle2D.Double(0, 0, 50, 50));

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphSettings2));

        g2.fill(new Rectangle2D.Double(0, 75, 50, 50));
        g2.drawString("Eastern Standard Time", 60, 110);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphDigital));

        g.setFont(new Font("arial", Font.BOLD, 100));
        g2.drawString(getTimeOut(),
                (int) ((window.getFrameWidth() / 2) - (g.getFontMetrics().stringWidth(getTimeOut())) / 2),
                (int) (window.getFrameHeight() / 2 + 25));

        g.dispose();
        strat.show();
    }

    public String getTimeOut() {
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
        return time;
    }

    public void alphaTransition() {
        if(entered) {
            if(System.currentTimeMillis() - beforeTimeAlph >= 5) {
                if(alph >= 0.48f) {
                    alphDigital = 1f;

                } else {
                    alphDigital += 0.02f;
                    alph += 0.01f;
                }
                beforeTimeAlph = System.currentTimeMillis();
            }
        } else {
            if(System.currentTimeMillis() - beforeTimeAlph >= 5) {
                if(alph <= .02f) {
                    alph = 0f;
                    alphDigital = 0f;

                } else {
                    alph -= 0.01f;
                    alphDigital -= 0.02f;
                }
                beforeTimeAlph = System.currentTimeMillis();
            }
        }
    }

    public void drawClock(Graphics g, Graphics2D g2) {
        double x = (int) window.getFrameWidth() / 2;
        double y = (int) window.getFrameHeight() / 2;

        g2.fill(new Rectangle2D.Double(0, 0, window.getFrameWidth(), window.getFrameHeight()));

        double deg = Math.toRadians(angle - 90);
        double deg2 = Math.toRadians(minAngle - 90);
        double deg3 = Math.toRadians(hourAngle - 90);

        double x2 = 200 * Math.cos(deg);
        double y2 = 200 * Math.sin(deg);
        double x2Min = 150 * Math.cos(deg2);
        double y2Min = 150 * Math.sin(deg2);
        double x2Hour = 75 * Math.cos(deg3);
        double y2Hour = 75 * Math.sin(deg3);

        float strokeThickness = 13.0f;
        g2.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.setColor(Color.magenta);
        g2.draw(new Line2D.Double(x - ((int) strokeThickness / 2), y - ((int) strokeThickness / 2),
                x2Min - ((int) strokeThickness / 2) + x, y2Min - ((int) strokeThickness / 2) + y));
        g2.draw(new Arc2D.Double(new Rectangle2D.Double(x - 250 - 6, y - 250 - 6, 500, 500),
                90, -minAngle, Arc2D.OPEN));

        g.setColor(Color.cyan);
        g2.draw(new Line2D.Double(x - ((int) strokeThickness / 2), y - ((int) strokeThickness / 2),
                x2Hour - ((int) strokeThickness / 2) + x, y2Hour - ((int) strokeThickness / 2) + y));
        g2.draw(new Arc2D.Double(new Rectangle2D.Double(x - 237 - 6, y - 237 - 6, 474, 474),
                90, -hourAngle, Arc2D.OPEN));

        g.setColor(Color.white);
        g2.draw(new Line2D.Double(x - ((int) strokeThickness / 2), y - ((int) strokeThickness / 2),
                x2 - ((int) strokeThickness / 2) + x, y2 - ((int) strokeThickness / 2) + y));
        g2.draw(new Arc2D.Double(new Rectangle2D.Double(x - 224 - 6, y - 224 - 6, 448, 448),
                90, -angle, Arc2D.OPEN));
    }

    public boolean getEntered() {
        return entered;
    }

    public boolean getEnteredSettings() {
        return enteredSettings;
    }

    public boolean getOverEST() {
        return overEST;
    }

    public void setOverEST(boolean set) {
        overEST = set;
    }

    public boolean getOverPST() {
        return overPST;
    }

    public void setOverPST(boolean set) {
        overPST = set;
    }

    public int getHour() {
        return hours;
    }

    public void setHour(int hour) {
        this.hours = hour;
    }

    public void setHourAngle(int hour) {
        this.hourAngle = hour * 30 + ((int) (this.minAngle / 60)) * 5;
    }

    public int getLocalHour() {
        return this.localHour;
    }

    public TIMEZONE getTimeZoneE() {
        return timeZone;
    }

    public void setTimeZoneE(TIMEZONE tz) {
        this.timeZone = tz;
    }

    public TimeZone getTimeZone() {
        return tz;
    }
}
