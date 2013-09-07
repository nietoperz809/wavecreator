package com.WaveCreator.Monitor;

import javax.swing.*;
import java.awt.*;
import static java.awt.Color.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Date;

/*
 * @(#)MemoryMonitor.java	1.37 06/08/29
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)MemoryMonitor.java	1.37 06/08/29
 */

/**
 * Tracks Memory allocated & used, displayed in graph form.
 */
public class MemoryMonitor extends JPanel
{
    final int sleep = 100;
    private static final JCheckBox dateStampCB = new JCheckBox("Output Date Stamp");
    public final Surface surf = new Surface(sleep);
    private final JPanel controls = new JPanel();
    private boolean doControls;
    private final JTextField tf = new JTextField(""+sleep);

    public MemoryMonitor()
    {
        setLayout(new BorderLayout());
        //setBorder (new EtchedBorder());
        add(surf);
        controls.setPreferredSize(new Dimension(135, 80));
        Font font = new Font("serif", Font.PLAIN, 10);
        JLabel label = new JLabel("Sample Rate");
        label.setFont(font);
        label.setForeground(BLACK);
        controls.add(label);
        tf.setPreferredSize(new Dimension(45, 20));
        controls.add(tf);
        label = new JLabel("ms");
        controls.add(label);
        label.setFont(font);
        label.setForeground(BLACK);
        controls.add(dateStampCB);
        dateStampCB.setFont(font);

        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                removeAll();
                doControls = !doControls;
                if (doControls)
                {
                    surf.stop();
                    add(controls);
                }
                else
                {
                    try
                    {
                        surf.sleepAmount = Long.parseLong(tf.getText().trim());
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    surf.start();
                    add(surf);
                }
                revalidate();
                repaint();
            }
        });
    }

    public class Surface extends JPanel implements Runnable
    {
        //public Thread thread;
        public long sleepAmount;
        private int w, h;
        private BufferedImage bimg;
        private Graphics2D big;
        private final Font font = new Font("Times New Roman", Font.PLAIN, 11);
        private final Runtime r = Runtime.getRuntime();
        private int columnInc;
        private int pts[];
        private int ptNum;
        private int ascent, descent;
        private final Rectangle graphOutlineRect = new Rectangle();
        private final Rectangle2D mfRect = new Rectangle2D.Float();
        private final Rectangle2D muRect = new Rectangle2D.Float();
        private final Line2D graphLine = new Line2D.Float();
        private final Color graphColor = new Color(46, 139, 87);
        private final Color mfColor = new Color(0, 100, 0);
        private String usedStr;
        private boolean running = true;

        public Surface (long sleep)
        {
            sleepAmount = sleep;
            setBackground(BLACK);
        }

        public Dimension getMinimumSize()
        {
            return new Dimension (100,100);
        }

        public Dimension getMaximumSize()
        {
            return getPreferredSize();
        }

        public Dimension getPreferredSize()
        {
            return new Dimension(135, 80);
        }

        public void paint(Graphics g)
        {

            if (big == null)
            {
                return;
            }

            big.setBackground(getBackground());
            big.clearRect(0, 0, w, h);

            float freeMemory = (float) r.freeMemory();
            float totalMemory = (float) r.totalMemory();

            // .. Draw allocated and used strings ..
            big.setColor(GREEN);
            big.drawString(String.valueOf((int) totalMemory / 1024) + "K allocated", 4.0f, (float) ascent + 0.5f);
            usedStr = String.valueOf(((int) (totalMemory - freeMemory)) / 1024)
                      + "K used";
            big.drawString(usedStr, 4, h - descent);

            // Calculate remaining size
            float ssH = ascent + descent;
            float remainingHeight = h - (ssH * 2) - 0.5f;
            float blockHeight = remainingHeight / 10;
            float blockWidth = 20.0f;

            // .. Memory Free ..
            big.setColor(mfColor);
            int MemUsage = (int) ((freeMemory / totalMemory) * 10);
            int i = 0;
            for (; i < MemUsage; i++)
            {
                mfRect.setRect(5, ssH + (i * blockHeight),
                               blockWidth, blockHeight - 1);
                big.fill(mfRect);
            }

            // .. Memory Used ..
            big.setColor(GREEN);
            for (; i < 10; i++)
            {
                muRect.setRect(5, ssH + (i * blockHeight),
                               blockWidth, blockHeight - 1);
                big.fill(muRect);
            }

            // .. Draw History Graph ..
            big.setColor(graphColor);
            int graphX = 30;
            int graphY = (int) ssH;
            int graphW = w - graphX - 5;
            int graphH = (int) remainingHeight;
            graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
            big.draw(graphOutlineRect);

            int graphRow = graphH / 10;

            // .. Draw row ..
            for (int j = graphY; j <= graphH + graphY; j += graphRow)
            {
                graphLine.setLine(graphX, j, graphX + graphW, j);
                big.draw(graphLine);
            }

            // .. Draw animated column movement ..
            int graphColumn = graphW / 15;

            if (columnInc == 0)
            {
                columnInc = graphColumn;
            }

            for (int j = graphX + columnInc; j < graphW + graphX; j += graphColumn)
            {
                graphLine.setLine(j, graphY, j, graphY + graphH);
                big.draw(graphLine);
            }

            --columnInc;

            if (pts == null)
            {
                pts = new int[graphW];
                ptNum = 0;
            }
            else if (pts.length != graphW)
            {
                int tmp[];
                if (ptNum < graphW)
                {
                    tmp = new int[ptNum];
                    System.arraycopy(pts, 0, tmp, 0, tmp.length);
                }
                else
                {
                    tmp = new int[graphW];
                    System.arraycopy(pts, pts.length - tmp.length, tmp, 0, tmp.length);
                    ptNum = tmp.length - 2;
                }
                pts = new int[graphW];
                System.arraycopy(tmp, 0, pts, 0, tmp.length);
            }
            else
            {
                big.setColor(YELLOW);
                pts[ptNum] = (int) (graphY + graphH * (freeMemory / totalMemory));
                for (int j = graphX + graphW - ptNum, k = 0; k < ptNum; k++, j++)
                {
                    if (k != 0)
                    {
                        if (pts[k] != pts[k - 1])
                        {
                            big.drawLine(j - 1, pts[k - 1], j, pts[k]);
                        }
                        else
                        {
                            big.fillRect(j, pts[k], 1, 1);
                        }
                    }
                }
                if (ptNum + 2 == pts.length)
                {
                    // throw out oldest point
                    System.arraycopy(pts, 1, pts, 0, ptNum - 1);
                    --ptNum;
                }
                else
                {
                    ptNum++;
                }
            }
            g.drawImage(bimg, 0, 0, this);
        }

        public void start()
        {
            Thread thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
            running = true;
        }

        public synchronized void stop()
        {
            running = false;
            notify();
        }

        public void run()
        {
            while (running)
            {
                Dimension d = getSize();
                if (d.height > 50)
                {
                    if (d.width != w || d.height != h)
                    {
                        w = d.width;
                        h = d.height;
                        bimg = (BufferedImage) createImage(w, h);
                        big = bimg.createGraphics();
                        big.setFont(font);
                        FontMetrics fm = big.getFontMetrics(font);
                        ascent = fm.getAscent();
                        descent = fm.getDescent();
                    }
                    repaint();
                }
                try
                {
                    Thread.sleep(sleepAmount);
                }
                catch (InterruptedException e)
                {
                    break;
                }
                if (MemoryMonitor.dateStampCB.isSelected())
                {
                    System.out.println(new Date().toString() + " " + usedStr);
                }
            }
            //System.out.println ("stopped");
        }
    }
}
