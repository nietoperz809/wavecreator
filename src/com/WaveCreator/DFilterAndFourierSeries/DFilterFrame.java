package com.WaveCreator.DFilterAndFourierSeries;

//import javazoom.jl.decoder.Decoder;
//import javazoom.jl.decoder.Bitstream;
//import javazoom.jl.decoder.Header;
//import javazoom.jl.decoder.SampleBuffer;

import com.WaveCreator.FrameManager;
import com.WaveCreator.ScopeWindow;
import com.WaveCreator.Wave16;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * New Class.
 * User: Administrator
 * Date: 04.01.2009
 * Time: 12:45:04
 */
public class DFilterFrame extends Frame
        implements ComponentListener, ActionListener, AdjustmentListener,
                   MouseMotionListener, MouseListener, ItemListener
{
    /**
     * Filtered output buffer (in bytes!!!)
     */
    private final ByteBucket m_bucket = new ByteBucket(1000000);
    private byte[] outputBuffer;

    private Dimension winSize;
    private Image dbimage;
    private View respView;
    private View impulseView;
    private View phaseView;
    private View stepView;
    private View spectrumView;
    private View waveformView;
    private View poleInfoView;
    private View polesView;

    Random random;
    //int maxSampleCount = 70; // was 50
    //int sampleCountR, sampleCountTh;
    //int modeCountR, modeCountTh;
    //int maxDispRModes = 5, maxDispThModes = 5;
    //public static final double epsilon = .00001;
    //public static final double epsilon2 = .003;
    public static final double log10 = 2.30258509299404568401;
    private static final int WINDOW_KAISER = 4;

//    public String getAppletInfo()
//    {
//        return "DFilterAndFourierSeries Series by Paul Falstad";
//    }

    private Checkbox soundCheck;
    private Checkbox displayCheck;
    private Checkbox shiftSpectrumCheck;
    //Checkbox woofCheck;
    private CheckboxMenuItem freqCheckItem;
    private CheckboxMenuItem phaseCheckItem;
    private CheckboxMenuItem spectrumCheckItem;
    private CheckboxMenuItem impulseCheckItem;
    private CheckboxMenuItem stepCheckItem;
    private CheckboxMenuItem waveformCheckItem;
    CheckboxMenuItem logFreqCheckItem;
    private CheckboxMenuItem logAmpCheckItem;
    private CheckboxMenuItem allWaveformCheckItem;
    private CheckboxMenuItem ferrisCheckItem;
    private MenuItem snapShot;
    //    MenuItem exitItem;
    private Choice filterChooser;
    private int selection;
    private final int SELECT_RESPONSE = 1;
    private final int SELECT_SPECTRUM = 2;
    private final int SELECT_POLES = 3;
    private int filterSelection;
    private Choice inputChooser;
    Choice windowChooser;
    private Choice rateChooser;
    Scrollbar auxBars[];
    Label auxLabels[];
    private Label inputLabel;
    Scrollbar inputBar;
    private Label shiftFreqLabel;
    private Scrollbar shiftFreqBar;
    private Label kaiserLabel;
    Scrollbar kaiserBar;
    //boolean editingFunc;
    //boolean dragStop;
    double inputW;
    static final double pi = 3.14159265358979323846;
    //double step;
    private double waveGain = 1. / 65536;
    private double outputGain = 1;
    int sampleRate;
    //int xpoints[] = new int[4];
    //int ypoints[] = new int[4];
    private int dragX;
    private int dragY;
    //int dragStartX, dragStartY;
    private int mouseX;
    private int mouseY;
    int selectedPole, selectedZero;
    int lastPoleCount = 2;
    //boolean dragSet, dragClear;
    //boolean dragging;
    private boolean unstable;
    private MemoryImageSource imageSource;
    private Image memimage;
    private int[] pixels;
    //double t;
    //private int pause;
    private PlayThread playThread;
    private Filter curFilter;
    private FilterType filterType;
    private double[] spectrumBuf;
    private FFT spectrumFFT;
    private Waveform wformInfo;
    private PhaseColor[] phaseColors;
    private static final int phaseColorCount = 50 * 8;
    private boolean filterChanged;
    private DFilterCanvas cv;
    NumberFormat showFormat;
    final ScopeWindow m_sourceWindow;

    class View extends Rectangle
    {
//        View(Dimension r)
//        {
//            super(r);
//        }

        View(int a, int b, int c, int d)
        {
            super(a, b, c, d);
            right = a + c - 1;
            bottom = b + d - 1;
        }

        final int right;
        final int bottom;

        void drawLabel(Graphics g, String str)
        {
            g.setColor(Color.white);
            centerString(g, str, y - 5);
        }
    }

//    int getrand(int x)
//    {
//        int q = random.nextInt();
//        if (q < 0)
//        {
//            q = -q;
//        }
//        return q % x;
//    }

    public DFilterFrame(ScopeWindow src)
    {
        super("Digital Filters Applet v1.2");
        m_sourceWindow = src;
    }

    public void init()
    {
        int j;
        int pc8 = phaseColorCount / 8;
        phaseColors = new PhaseColor[phaseColorCount];
        int i;
        for (i = 0; i != 8; i++)
        {
            for (j = 0; j != pc8; j++)
            {
                double ang = Math.atan(j / (double) pc8);
                phaseColors[i * pc8 + j] = genPhaseColor(i, ang);
            }
        }

        customPoles = new Complex[20];
        customZeros = new Complex[20];
        for (i = 0; i != customPoles.length; i++)
        {
            customPoles[i] = new Complex();
        }
        for (i = 0; i != customZeros.length; i++)
        {
            customZeros[i] = new Complex();
        }

        setLayout(new DFilterLayout());
        cv = new DFilterCanvas(this);
        cv.addComponentListener(this);
        cv.addMouseMotionListener(this);
        cv.addMouseListener(this);
        add(cv);

        MenuBar mb = new MenuBar();
        Menu m = new Menu("View");
        mb.add(m);

        freqCheckItem = getCheckItem("Frequency Response", true);
        m.add(freqCheckItem);
        phaseCheckItem = getCheckItem("Phase Response", false);
        m.add(phaseCheckItem);
        spectrumCheckItem = getCheckItem("Spectrum", true);
        m.add(spectrumCheckItem);
        waveformCheckItem = getCheckItem("Waveform", true);
        m.add(waveformCheckItem);
        impulseCheckItem = getCheckItem("Impulse Response", true);
        m.add(impulseCheckItem);
        stepCheckItem = getCheckItem("Step Response", false);
        m.add(stepCheckItem);
        m.addSeparator();
        logFreqCheckItem = getCheckItem("Log Frequency Scale", false);
        m.add(logFreqCheckItem);
        allWaveformCheckItem = getCheckItem("Show Entire Waveform", false);
        m.add(allWaveformCheckItem);
        ferrisCheckItem = getCheckItem("Ferris Plot", false);
        m.add(ferrisCheckItem);
        // this doesn't fully work when turned off
        logAmpCheckItem = getCheckItem("Log Amplitude Scale", true);

        m.addSeparator();
        snapShot = new MenuItem("Snapshot");
        snapShot.addActionListener(this);
        m.add(snapShot);

        setMenuBar(mb);

        soundCheck = new Checkbox("Sound On");
        soundCheck.setState(true);
        soundCheck.addItemListener(this);
        add(soundCheck);

        displayCheck = new Checkbox("Stop Display");
        displayCheck.addItemListener(this);
        add(displayCheck);

        shiftSpectrumCheck = new Checkbox("Shift Spectrum");
        shiftSpectrumCheck.addItemListener(this);
        add(shiftSpectrumCheck);

        /*woofCheck = new Checkbox("Woof");
      woofCheck.addItemListener(this);
      add(woofCheck);*/

        inputChooser = new Choice();
        add(inputChooser);
        inputChooser.add("Input = Noise");
        inputChooser.add("Input = Sine Wave");
        inputChooser.add("Input = Sawtooth");
        inputChooser.add("Input = Triangle Wave");
        inputChooser.add("Input = Square Wave");
        inputChooser.add("Input = Periodic Noise");
        inputChooser.add("Input = Sweep");
        inputChooser.add("Input = Impulses");
        inputChooser.add("Input = Wave16");
//        for (i = 0; mp3List[i] != null; i++)
//        {
//            inputChooser.add("Input = " + mp3List[i]);
//        }
        inputChooser.addItemListener(this);
        inputChooser.select(8);  // Use Wave16 object on start

        filterChooser = new Choice();
        add(filterChooser);
        filterChooser.add("Filter = FIR Low-pass");
        filterChooser.add("Filter = FIR High-pass");
        filterChooser.add("Filter = FIR Band-pass");
        filterChooser.add("Filter = FIR Band-stop");
        filterChooser.add("Filter = Custom FIR");
        filterChooser.add("Filter = None");
        filterChooser.add("Filter = Butterworth Low-pass");
        filterChooser.add("Filter = Butterworth High-pass");
        filterChooser.add("Filter = Butterworth Band-pass");
        filterChooser.add("Filter = Butterworth Band-stop");
        filterChooser.add("Filter = Chebyshev Low-pass");
        filterChooser.add("Filter = Chebyshev High-pass");
        filterChooser.add("Filter = Chebyshev Band-pass");
        filterChooser.add("Filter = Chebyshev Band-stop");
        filterChooser.add("Filter = Inv Cheby Low-pass");
        filterChooser.add("Filter = Inv Cheby High-pass");
        filterChooser.add("Filter = Inv Cheby Band-pass");
        filterChooser.add("Filter = Inv Cheby Band-stop");
        filterChooser.add("Filter = Elliptic Low-pass");
        filterChooser.add("Filter = Elliptic High-pass");
        filterChooser.add("Filter = Elliptic Band-pass");
        filterChooser.add("Filter = Elliptic Band-stop");
        filterChooser.add("Filter = Comb (+)");
        filterChooser.add("Filter = Comb (-)");
        filterChooser.add("Filter = Delay");
        filterChooser.add("Filter = Plucked String");
        filterChooser.add("Filter = Inverse Comb");
        filterChooser.add("Filter = Reson");
        filterChooser.add("Filter = Reson w/ Zeros");
        filterChooser.add("Filter = Notch");
        filterChooser.add("Filter = Moving Average");
        filterChooser.add("Filter = Triangle");
        filterChooser.add("Filter = Allpass");
        filterChooser.add("Filter = Gaussian");
        filterChooser.add("Filter = Random");
        filterChooser.add("Filter = Custom IIR");
        filterChooser.add("Filter = Box");
        filterChooser.addItemListener(this);
        filterSelection = -1;

        windowChooser = new Choice();
        add(windowChooser);
        windowChooser.add("Window = Rectangular");
        windowChooser.add("Window = Hamming");
        windowChooser.add("Window = Hann");
        windowChooser.add("Window = Blackman");
        windowChooser.add("Window = Kaiser");
        windowChooser.add("Window = Bartlett");
        windowChooser.add("Window = Welch");
        windowChooser.addItemListener(this);
        windowChooser.select(1);

        rateChooser = new Choice();
        add(rateChooser);
        rateChooser.add("Sampling Rate = 8000");
        rateChooser.add("Sampling Rate = 11025");
        rateChooser.add("Sampling Rate = 16000");
        rateChooser.add("Sampling Rate = 22050");
        rateChooser.add("Sampling Rate = 32000");
        rateChooser.add("Sampling Rate = 44100");
        rateChooser.select(3);
        sampleRate = 22050;
        rateChooser.addItemListener(this);

        auxLabels = new Label[5];
        auxBars = new Scrollbar[5];
        for (i = 0; i != 5; i++)
        {
            auxLabels[i] = new Label("", Label.CENTER);
            add(auxLabels[i]);
            auxBars[i] = new Scrollbar(Scrollbar.HORIZONTAL, 25, 1, 1, 999);
            add(auxBars[i]);
            auxBars[i].addAdjustmentListener(this);
        }

        inputLabel = new Label("Input Frequency", Label.CENTER);
        add(inputLabel);
        inputBar = new Scrollbar(Scrollbar.HORIZONTAL, 40, 1, 1, 999);
        add(inputBar);
        inputBar.addAdjustmentListener(this);

        shiftFreqLabel = new Label("Shift Frequency", Label.CENTER);
        add(shiftFreqLabel);
        shiftFreqBar = new Scrollbar(Scrollbar.HORIZONTAL, 10, 1, 0, 1001);
        add(shiftFreqBar);
        shiftFreqBar.addAdjustmentListener(this);
        shiftFreqLabel.setVisible(false);
        shiftFreqBar.setVisible(false);

        kaiserLabel = new Label("Kaiser Parameter", Label.CENTER);
        add(kaiserLabel);
        kaiserBar = new Scrollbar(Scrollbar.HORIZONTAL, 500, 1, 1, 999);
        add(kaiserBar);
        kaiserBar.addAdjustmentListener(this);

        random = new Random();
        setInputLabel();
        reinit();
        cv.setBackground(Color.black);
        cv.setForeground(Color.lightGray);

        showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);

        setSize(640, 640);
        handleResize();
        Dimension x = getSize();
        Dimension screen = getToolkit().getScreenSize();
        setLocation((screen.width - x.width) / 2,
                    (screen.height - x.height) / 2);
        setVisible(true);
    }

    void reinit()
    {
        setupFilter();
        setInputW();
    }

//    MenuItem getMenuItem()
//    {
//        MenuItem mi = new MenuItem("Exit");
//        mi.addActionListener(this);
//        return mi;
//    }

    CheckboxMenuItem getCheckItem(String s, boolean b)
    {
        CheckboxMenuItem mi = new CheckboxMenuItem(s);
        mi.setState(b);
        mi.addItemListener(this);
        return mi;
    }

    int getPower2(int n)
    {
        int o = 2;
        while (o < n)
        {
            o *= 2;
        }
        return o;
    }

    PhaseColor genPhaseColor(int sec, double ang)
    {
        // convert to 0 .. 2*pi angle
        ang += sec * pi / 4;
        // convert to 0 .. 6
        ang *= 3 / pi;
        int hsec = (int) ang;
        double a2 = ang % 1;
        double a3 = 1. - a2;
        PhaseColor c = null;
        switch (hsec)
        {
            case 6:
            case 0:
                c = new PhaseColor(1, a2, 0);
                break;
            case 1:
                c = new PhaseColor(a3, 1, 0);
                break;
            case 2:
                c = new PhaseColor(0, 1, a2);
                break;
            case 3:
                c = new PhaseColor(0, a3, 1);
                break;
            case 4:
                c = new PhaseColor(a2, 0, 1);
                break;
            case 5:
                c = new PhaseColor(1, 0, a3);
                break;
        }
        return c;
    }

    class PhaseColor
    {
        public final double r;
        public final double g;
        public final double b;

        PhaseColor(double rr, double gg, double bb)
        {
            r = rr;
            g = gg;
            b = bb;
        }
    }

    void handleResize()
    {
        winSize = cv.getSize();
        Dimension d = winSize; 
        if (winSize.width == 0)
        {
            return;
        }
        int ct = 1;
        respView = null;
        spectrumView = null;
        impulseView = null;
        phaseView = null;
        stepView = null;
        waveformView = null;
        if (freqCheckItem.getState())
        {
            ct++;
        }
        if (phaseCheckItem.getState())
        {
            ct++;
        }
        if (spectrumCheckItem.getState())
        {
            ct++;
        }
        if (waveformCheckItem.getState())
        {
            ct++;
        }
        if (impulseCheckItem.getState())
        {
            ct++;
        }
        if (stepCheckItem.getState())
        {
            ct++;
        }

        //int dh3 = d.height / ct;
        dbimage = createImage(d.width, d.height);
        //int bd = 15;

        int i = 0;
        if (freqCheckItem.getState())
        {
            respView = getView(i++, ct);
        }
        if (phaseCheckItem.getState())
        {
            phaseView = getView(i++, ct);
        }
        if (spectrumCheckItem.getState())
        {
            spectrumView = getView(i++, ct);
        }
        if (waveformCheckItem.getState())
        {
            waveformView = getView(i++, ct);
        }
        if (impulseCheckItem.getState())
        {
            impulseView = getView(i++, ct);
        }
        if (stepCheckItem.getState())
        {
            stepView = getView(i++, ct);
        }
        poleInfoView = getView(i, ct);
        if (poleInfoView.height > 200)
        {
            poleInfoView.height = 200;
        }
        polesView = new View(poleInfoView.x, poleInfoView.y,
                             poleInfoView.height, poleInfoView.height);
        getPoleBuffer();
    }

    View getView(int i, int ct)
    {
        int dh3 = winSize.height / ct;
        int bd = 5;
        int tpad = 15;
        return new View(bd, bd + i * dh3 + tpad, winSize.width - bd * 2, dh3 - bd * 2 - tpad);
    }

    void getPoleBuffer()
    {
        int i;
        pixels = null;
        try
        {
            memimage = new BufferedImage(polesView.width, polesView.height, BufferedImage.TYPE_INT_RGB);
            Raster ras = ((BufferedImage) memimage).getRaster();
            DataBuffer db = ras.getDataBuffer();
            pixels = ((DataBufferInt) db).getData();
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
            //System.out.println("BufferedImage failed");
        }
        if (pixels == null)
        {
            pixels = new int[polesView.width * polesView.height];
            for (i = 0; i != polesView.width * polesView.height; i++)
            {
                pixels[i] = 0xFF000000;
            }
            imageSource = new MemoryImageSource(polesView.width, polesView.height,
                                                pixels, 0, polesView.width);
            imageSource.setAnimated(true);
            imageSource.setFullBufferUpdates(true);
            memimage = cv.createImage(imageSource);
        }
    }

    void centerString(Graphics g, String s, int y)
    {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, (winSize.width - fm.stringWidth(s)) / 2, y);
    }

    public void paint(Graphics g)
    {
        cv.repaint();
    }

    //long lastTime;
    private double minlog;
    private double logrange;

    public void updateDFilter(Graphics realg)
    {
        Graphics g = dbimage.getGraphics();
        if (winSize == null || winSize.width == 0 || dbimage == null)
        {
            return;
        }

        if (curFilter == null)
        {
            Filter f = filterType.genFilter();
            curFilter = f;
            if (playThread != null)
            {
                playThread.setFilter(f);
            }
            filterChanged = true;
            unstable = false;
        }

        if (playThread == null && !unstable && soundCheck.getState())
        {
            playThread = new PlayThread();
            playThread.start();
        }

        if (displayCheck.getState())
        {
            return;
        }

        g.setColor(cv.getBackground());
        g.fillRect(0, 0, winSize.width, winSize.height);
        g.setColor(cv.getForeground());

        double minf = 40. / sampleRate;
        minlog = Math.log(minf);
        logrange = Math.log(.5) - minlog;
        Complex cc = new Complex();

        int i;
        if (respView != null)
        {
            respView.drawLabel(g, "Frequency Response");
            g.setColor(Color.darkGray);
            g.fillRect(respView.x, respView.y, respView.width, respView.height);
            g.setColor(Color.black);
            /*i = respView.x + respView.width/2;
             g.drawLine(i, respView.y, i, respView.y+respView.height);*/
            double ym = .069;
            for (i = 0; ; i += 2)
            {
                double q = ym * i;
                if (q > 1)
                {
                    break;
                }
                int y = respView.y + (int) (q * respView.height);
                g.drawLine(respView.x, y, respView.right, y);
            }
            for (i = 1; ; i++)
            {
                double ll = logrange - i * Math.log(2);
                int x;
                if (logFreqCheckItem.getState())
                {
                    x = (int) (ll * respView.width / logrange);
                }
                else
                {
                    x = respView.width / (1 << i);
                }
                if (x <= 0)
                {
                    break;
                }
                x += respView.x;
                g.drawLine(x, respView.y, x, respView.bottom);
            }
            g.setColor(Color.white);
            int ox = -1, oy = -1, ox2 = -1, oy2 = -1;
            for (i = 0; i != respView.width; i++)
            {
                double w;
                if (!logFreqCheckItem.getState())
                {
                    w = pi * i / (respView.width);
                }
                else
                {
                    double f = Math.exp(minlog + i * logrange / respView.width);
                    w = 2 * pi * f;
                }
                filterType.getResponse(w, cc);
                double bw = cc.magSquared();
                double val = -ym * Math.log(bw * bw) / log10;
                int x = i + respView.x;
                if (val > 1)
                {
                    if (ox != -1)
                    {
                        g.drawLine(ox, oy, ox, respView.bottom);
                    }
                    ox = -1;
                }
                else
                {
                    int y = respView.y + (int) (respView.height * val);
                    if (ox != -1)
                    {
                        g.drawLine(ox, oy, x, y);
                    }
                    else if (x > respView.x)
                    {
                        g.drawLine(x, respView.bottom, x, y);
                    }
                    ox = x;
                    oy = y;
                }
                if (filterType instanceof CustomFIRFilter)
                {
                    g.setColor(Color.white);
                    CustomFIRFilter cf = (CustomFIRFilter) filterType;
                    bw = cf.getUserResponse(w);
                    val = -ym * Math.log(bw * bw) / log10;
                    if (val > 1)
                    {
                        if (ox2 != -1)
                        {
                            g.drawLine(ox2, oy2, ox2, respView.bottom);
                        }
                        ox2 = -1;
                    }
                    else
                    {
                        int y = respView.y + (int) (respView.height * val);
                        if (ox2 != -1)
                        {
                            g.drawLine(ox2, oy2, x, y);
                        }
                        else if (x > respView.x)
                        {
                            g.drawLine(x, respView.bottom, x, y);
                        }
                        ox2 = x;
                        oy2 = y;
                    }
                    g.setColor(Color.red);
                }
            }
        }
        g.setColor(Color.white);

        if (phaseView != null)
        {
            phaseView.drawLabel(g, "Phase Response");
            g.setColor(Color.darkGray);
            g.fillRect(phaseView.x, phaseView.y, phaseView.width, phaseView.height);
            g.setColor(Color.black);
            for (i = 0; i < 5; i++)
            {
                double q = i * .25;
                int y = phaseView.y + (int) (q * phaseView.height);
                g.drawLine(phaseView.x, y, phaseView.right, y);
            }
            for (i = 1; ; i++)
            {
                double ll = logrange - i * Math.log(2);
                int x;
                if (logFreqCheckItem.getState())
                {
                    x = (int) (ll * phaseView.width / logrange);
                }
                else
                {
                    x = phaseView.width / (1 << i);
                }
                if (x <= 0)
                {
                    break;
                }
                x += phaseView.x;
                g.drawLine(x, phaseView.y, x, phaseView.bottom);
            }
            g.setColor(Color.white);
            int ox = -1, oy = -1;
            for (i = 0; i != phaseView.width; i++)
            {
                double w;
                if (!logFreqCheckItem.getState())
                {
                    w = pi * i / (phaseView.width);
                }
                else
                {
                    double f = Math.exp(minlog + i * logrange / phaseView.width);
                    w = 2 * pi * f;
                }
                filterType.getResponse(w, cc);
                double val = .5 + cc.phase / (2 * pi);
                int y = phaseView.y + (int) (phaseView.height * val);
                int x = i + phaseView.x;
                if (ox != -1)
                {
                    g.drawLine(ox, oy, x, y);
                }
                else if (x > phaseView.x)
                {
                    g.drawLine(x, phaseView.bottom, x, y);
                }
                ox = x;
                oy = y;
            }
        }

        int polect = filterType.getPoleCount();
        int zeroct = filterType.getZeroCount();
        int infoX = 10;
        int ph = 0, pw = 0, cx = 0, cy = 0;
        if (poleInfoView != null && (polect > 0 || zeroct > 0 || ferrisCheckItem.getState()))
        {
            ph = polesView.height / 2;
            pw = ph;
            cx = polesView.x + pw;
            cy = polesView.y + ph;
            infoX = cx + pw + 10;

            if (!ferrisCheckItem.getState())
            {
                g.setColor(Color.white);
                FontMetrics fm = g.getFontMetrics();
                String s = "Poles/Zeros";
                g.drawString(s, cx - fm.stringWidth(s) / 2, polesView.y - 5);
                g.drawOval(cx - pw, cy - ph, pw * 2, ph * 2);
                g.drawLine(cx, cy - ph, cx, cy + ph);
                g.drawLine(cx - ph, cy, cx + ph, cy);
                Complex c1 = new Complex();
                for (i = 0; i != polect; i++)
                {
                    filterType.getPole(i, c1);
                    g.setColor(i == selectedPole ? Color.yellow : Color.white);
                    int c1x = cx + (int) (pw * c1.re);
                    int c1y = cy - (int) (ph * c1.im);
                    g.drawLine(c1x - 3, c1y - 3, c1x + 3, c1y + 3);
                    g.drawLine(c1x - 3, c1y + 3, c1x + 3, c1y - 3);
                }
                for (i = 0; i != zeroct; i++)
                {
                    filterType.getZero(i, c1);
                    g.setColor(i == selectedZero ? Color.yellow : Color.white);
                    int c1x = cx + (int) (pw * c1.re);
                    int c1y = cy - (int) (ph * c1.im);
                    g.drawOval(c1x - 3, c1y - 3, 6, 6);
                }
                if (filterChanged)
                {
                    setCustomPolesZeros();
                }
            }
            else
            {
                if (filterChanged)
                {
                    int ri, ii;
                    Complex c1 = new Complex();
                    for (ri = 0; ri != polesView.width; ri++)
                    {
                        for (ii = 0; ii != polesView.height; ii++)
                        {
                            c1.set((ri - pw) / (double) pw,
                                   (ii - pw) / (double) pw);
                            if (c1.re == 0 && c1.im == 0)
                            {
                                c1.set(1e-30);
                            }
                            curFilter.evalTransfer(c1);
                            double cv_here = 0, wv = 0;
                            double m = Math.sqrt(c1.mag);
                            if (m < 1)
                            {
                                cv_here = m;
                                wv = 1 - cv_here;
                            }
                            else if (m < 2)
                            {
                                cv_here = 2 - m;
                            }
                            cv_here *= 255;
                            wv *= 255;
                            double p = c1.phase;
                            if (p < 0)
                            {
                                p += 2 * pi;
                            }
                            if (p >= 2 * pi)
                            {
                                p -= 2 * pi;
                            }
                            PhaseColor pc = phaseColors[(int) (p * phaseColorCount / (2 * pi))];
                            pixels[ri + ii * polesView.width] = 0xFF000000 +
                                                                0x10000 * (int) (pc.r * cv_here + wv) +
                                                                0x00100 * (int) (pc.g * cv_here + wv) +
                                                                (int) (pc.b * cv_here + wv);
                        }
                    }
                }
                if (imageSource != null)
                {
                    imageSource.newPixels();
                }
                g.drawImage(memimage, polesView.x, polesView.y, null);
            }
        }
        if (poleInfoView != null)
        {
            g.setColor(Color.white);
            String info[] = new String[10];
            filterType.getInfo(info);
            for (i = 0; i != 10; i++)
            {
                if (info[i] == null)
                {
                    break;
                }
            }
            if (wformInfo.needsFrequency())
            {
                info[i++] = "Input Freq = " + (int) (inputW * sampleRate / (2 * pi));
            }
            info[i] = "Output adjust = " +
                      showFormat.format(-10 * Math.log(outputGain) / Math.log(.1)) + " dB";
            for (i = 0; i != 10; i++)
            {
                if (info[i] == null)
                {
                    break;
                }
                g.drawString(info[i], infoX, poleInfoView.y + 5 + 20 * i);
            }
            if ((respView != null && respView.contains(mouseX, mouseY)) ||
                (spectrumView != null && spectrumView.contains(mouseX, mouseY)))
            {
                double f = getFreqFromX(mouseX, respView);
                if (f >= 0)
                {
                    double fw = 2 * pi * f;
                    f *= sampleRate;
                    g.setColor(Color.yellow);
                    String s = "Selected Freq = " + (int) f;
                    if (respView.contains(mouseX, mouseY))
                    {
                        filterType.getResponse(fw, cc);
                        double bw = cc.magSquared();
                        bw = Math.log(bw * bw) / (2 * log10);
                        s += ", Response = " + showFormat.format(10 * bw) + " dB";
                    }
                    g.drawString(s, infoX, poleInfoView.y + 5 + 20 * i);
                    if (ph > 0)
                    {
                        int x = cx + (int) (pw * Math.cos(fw));
                        int y = cy - (int) (pw * Math.sin(fw));
                        if (ferrisCheckItem.getState())
                        {
                            g.setColor(Color.black);
                            g.fillOval(x - 3, y - 3, 7, 7);
                        }
                        g.setColor(Color.yellow);
                        g.fillOval(x - 2, y - 2, 5, 5);
                    }
                }
            }
        }

        if (impulseView != null)
        {
            impulseView.drawLabel(g, "Impulse Response");
            g.setColor(Color.darkGray);
            g.fillRect(impulseView.x, impulseView.y, impulseView.width, impulseView.height);
            g.setColor(Color.black);
            g.drawLine(impulseView.x, impulseView.y + impulseView.height / 2,
                       impulseView.x + impulseView.width - 1,
                       impulseView.y + impulseView.height / 2);
            g.setColor(Color.white);
            int offset = curFilter.getImpulseOffset();
            double impBuf[] = curFilter.getImpulseResponse(offset);
            int len = curFilter.getImpulseLen(offset, impBuf);
            int ox = -1, oy = -1;
            double mult = .5 / max(impBuf);
            int flen = (len < 50) ? 50 : len;
            if (len < flen && flen < impBuf.length - offset)
            {
                len = flen;
            }
            //System.out.println("cf " + offset + " " + len + " " + impBuf.length);
            for (i = 0; i != len; i++)
            {
                int k = offset + i;
                double q = impBuf[k] * mult;
                int y = impulseView.y + (int) (impulseView.height * (.5 - q));
                int x = impulseView.x + impulseView.width * i / flen;
                if (len < 100)
                {
                    g.drawLine(x, impulseView.y + impulseView.height / 2, x, y);
                    g.fillOval(x - 2, y - 2, 5, 5);
                }
                else
                {
                    if (ox != -1)
                    {
                        g.drawLine(ox, oy, x, y);
                    }
                    ox = x;
                    oy = y;
                }
            }
        }

        if (stepView != null)
        {
            stepView.drawLabel(g, "Step Response");
            g.setColor(Color.darkGray);
            g.fillRect(stepView.x, stepView.y, stepView.width, stepView.height);
            g.setColor(Color.black);
            g.drawLine(stepView.x, stepView.y + stepView.height / 2,
                       stepView.x + stepView.width - 1,
                       stepView.y + stepView.height / 2);
            g.setColor(Color.white);
            int offset = curFilter.getStepOffset();
            double impBuf[] = curFilter.getStepResponse(offset);
            int len = curFilter.getStepLen(offset, impBuf);
            int ox = -1, oy = -1;
            double mult = .5 / max(impBuf);
            int flen = (len < 50) ? 50 : len;
            if (len < flen && flen < impBuf.length - offset)
            {
                len = flen;
            }
            //System.out.println("cf " + offset + " " + len + " " + impBuf.length);
            for (i = 0; i != len; i++)
            {
                int k = offset + i;
                double q = impBuf[k] * mult;
                int y = stepView.y + (int) (stepView.height * (.5 - q));
                int x = stepView.x + stepView.width * i / flen;
                if (len < 100)
                {
                    g.drawLine(x, stepView.y + stepView.height / 2, x, y);
                    g.fillOval(x - 2, y - 2, 5, 5);
                }
                else
                {
                    if (ox != -1)
                    {
                        g.drawLine(ox, oy, x, y);
                    }
                    ox = x;
                    oy = y;
                }
            }
        }

        if (playThread != null)
        {
            int splen = playThread.spectrumLen;
            if (spectrumBuf == null || spectrumBuf.length != splen * 2)
            {
                spectrumBuf = new double[splen * 2];
            }
            int off = playThread.spectrumOffset;
            int i2;
            int mask = playThread.fbufmask;
            for (i = 0, i2 = 0; i != splen; i++, i2 += 2)
            {
                int o = mask & (off + i);
                spectrumBuf[i2] = playThread.fbufLo[o]; // + playThread.fbufRo[o];
                spectrumBuf[i2 + 1] = 0;
            }
        }
        else
        {
            spectrumBuf = null;
        }

        if (waveformView != null && spectrumBuf != null)
        {
            waveformView.drawLabel(g, "Waveform");
            g.setColor(Color.darkGray);
            g.fillRect(waveformView.x, waveformView.y,
                       waveformView.width, waveformView.height);
            g.setColor(Color.black);
            g.drawLine(waveformView.x, waveformView.y + waveformView.height / 2,
                       waveformView.x + waveformView.width - 1,
                       waveformView.y + waveformView.height / 2);
            g.setColor(Color.white);
            int ox = -1, oy = -1;

            if (waveGain < .1)
            {
                waveGain = .1;
            }
            double max = 0;
            for (i = 0; i != spectrumBuf.length; i += 2)
            {
                if (spectrumBuf[i] > max)
                {
                    max = spectrumBuf[i];
                }
                if (spectrumBuf[i] < -max)
                {
                    max = -spectrumBuf[i];
                }
            }
            if (waveGain > 1 / max)
            {
                waveGain = 1 / max;
            }
            else if (waveGain * 1.05 < 1 / max)
            {
                waveGain *= 1.05;
            }
            double mult = .5 * waveGain;
            int nb = waveformView.width;
            if (nb > spectrumBuf.length || allWaveformCheckItem.getState())
            {
                nb = spectrumBuf.length;
            }
            for (i = 0; i < nb; i += 2)
            {
                double bf = .5 - spectrumBuf[i] * mult;
                int ya = (int) (waveformView.height * bf);
                if (ya > waveformView.height)
                {
                    ox = -1;
                    continue;
                }
                int y = waveformView.y + ya;
                int x = waveformView.x + i * waveformView.width / nb;
                if (ox != -1)
                {
                    g.drawLine(ox, oy, x, y);
                }
                ox = x;
                oy = y;
            }
        }

        if (spectrumView != null && spectrumBuf != null)
        {
            spectrumView.drawLabel(g, "Spectrum");
            g.setColor(Color.darkGray);
            g.fillRect(spectrumView.x, spectrumView.y,
                       spectrumView.width, spectrumView.height);
            g.setColor(Color.black);
            double ym = .138;
            for (i = 0; ; i++)
            {
                double q = ym * i;
                if (q > 1)
                {
                    break;
                }
                int y = spectrumView.y + (int) (q * spectrumView.height);
                g.drawLine(spectrumView.x, y, spectrumView.x + spectrumView.width, y);
            }
            for (i = 1; ; i++)
            {
                double ll = logrange - i * Math.log(2);
                int x;
                if (logFreqCheckItem.getState())
                {
                    x = (int) (ll * spectrumView.width / logrange);
                }
                else
                {
                    x = spectrumView.width / (1 << i);
                }
                if (x <= 0)
                {
                    break;
                }
                x += spectrumView.x;
                g.drawLine(x, spectrumView.y, x, spectrumView.bottom);
            }

            g.setColor(Color.white);
            //int isub = spectrumBuf.length / 2;
            double cosmult = 2 * pi / (spectrumBuf.length - 2);
            for (i = 0; i != spectrumBuf.length; i += 2)
            {
                double ht = .54 - .46 * Math.cos(i * cosmult);
                spectrumBuf[i] *= ht;
            }
            if (spectrumFFT == null || spectrumFFT.size != spectrumBuf.length / 2)
            {
                spectrumFFT = new FFT(spectrumBuf.length / 2);
            }
            spectrumFFT.transform(spectrumBuf, false);
            //double logmult = spectrumView.width / Math.log(spectrumBuf.length / 2 + 1);

            //int ox = -1;
            //int oy = -1;
            double bufmult = 1. / (spectrumBuf.length / 2);
            if (logAmpCheckItem.getState())
            {
                bufmult /= 65536;
            }
            else
            {
                bufmult /= 768;
            }
            bufmult *= bufmult;

            double specArray[] = new double[spectrumView.width];
            if (logFreqCheckItem.getState())
            {
                // freq = i*rate/(spectrumBuf.length)
                // min frequency = 40 Hz
                for (i = 0; i != spectrumBuf.length / 2; i += 2)
                {
                    double f = i / (double) spectrumBuf.length;
                    int ix = (int)
                            (specArray.length * (Math.log(f) - minlog) / logrange);
                    if (ix < 0)
                    {
                        continue;
                    }
                    specArray[ix] += spectrumBuf[i] * spectrumBuf[i] +
                                     spectrumBuf[i + 1] * spectrumBuf[i + 1];
                }
            }
            else
            {
                for (i = 0; i != spectrumBuf.length / 2; i += 2)
                {
                    int ix = specArray.length * i * 2 / spectrumBuf.length;
                    specArray[ix] += spectrumBuf[i] * spectrumBuf[i] +
                                     spectrumBuf[i + 1] * spectrumBuf[i + 1];
                }
            }

            int maxi = specArray.length;
            for (i = 0; i != spectrumView.width; i++)
            {
                double bf = specArray[i] * bufmult;
                if (logAmpCheckItem.getState())
                {
                    bf = -ym * Math.log(bf) / log10;
                }
                else
                {
                    bf = 1 - bf;
                }

                int ya = (int) (spectrumView.height * bf);
                if (ya > spectrumView.height)
                {
                    continue;
                }
                int y = spectrumView.y + ya;
                int x = spectrumView.x + i * spectrumView.width / maxi;
                g.drawLine(x, y, x, spectrumView.y + spectrumView.height - 1);
            }
        }

        if (unstable)
        {
            g.setColor(Color.red);
            centerString(g, "Filter is unstable", winSize.height / 2);
        }

        if (respView != null && respView.contains(mouseX, mouseY))
        {
            g.setColor(Color.yellow);
            g.drawLine(mouseX, respView.y,
                       mouseX, respView.y + respView.height - 1);
        }
        if (spectrumView != null && spectrumView.contains(mouseX, mouseY))
        {
            g.setColor(Color.yellow);
            g.drawLine(mouseX, spectrumView.y,
                       mouseX, spectrumView.y + spectrumView.height - 1);
        }
        filterChanged = false;

        realg.drawImage(dbimage, 0, 0, this);
    }

//    void setCutoff(double f)
//    {
//    }

    void setCustomPolesZeros()
    {
        if (filterType instanceof CustomIIRFilter)
        {
            return;
        }
        int polect = filterType.getPoleCount();
        int zeroct = filterType.getZeroCount();
        int i, n;
        Complex c1 = new Complex();
        for (i = 0, n = 0; i != polect; i++)
        {
            filterType.getPole(i, c1);
            if (c1.im >= 0)
            {
                customPoles[n++].set(c1);
                customPoles[n++].set(c1.re, -c1.im);
                if (n == customPoles.length)
                {
                    break;
                }
            }
        }
        lastPoleCount = n;
        for (i = 0, n = 0; i != zeroct; i++)
        {
            filterType.getZero(i, c1);
            if (c1.im >= 0)
            {
                customZeros[n++].set(c1);
                customZeros[n++].set(c1.re, -c1.im);
                if (n == customZeros.length)
                {
                    break;
                }
            }
        }
        //int lastZeroCount = n;
    }

    int countPoints(double buf[], int offset)
    {
        int len = buf.length;
        double max = 0;
        int i;
        int result = 0;
        double last = 123;
        for (i = offset; i < len; i++)
        {
            double qa = Math.abs(buf[i]);
            if (qa > max)
            {
                max = qa;
            }
            if (Math.abs(qa - last) > max * .003)
            {
                result = i - offset + 1;
                //System.out.println(qa + " " + last + " " + i + " " + max);
            }
            last = qa;
        }
        return result;
    }

    double max(double buf[])
    {
        int i;
        double max = 0;
        for (i = 0; i != buf.length; i++)
        {
            double qa = Math.abs(buf[i]);
            if (qa > max)
            {
                max = qa;
            }
        }
        return max;
    }

    // get freq (from 0 to .5) given an x coordinate
    double getFreqFromX(int x, View v)
    {
        double f = .5 * (x - v.x) / (double) v.width;
        if (f <= 0 || f >= .5)
        {
            return -1;
        }
        if (logFreqCheckItem.getState())
        {
            return Math.exp(minlog + 2 * f * logrange);
        }
        return f;
    }

    void setupFilter()
    {
        int filt = filterChooser.getSelectedIndex();
        switch (filt)
        {
            case 0:
                filterType = new SincLowPassFilter(this);
                break;
            case 1:
                filterType = new SincHighPassFilter(this);
                break;
            case 2:
                filterType = new SincBandPassFilter(this);
                break;
            case 3:
                filterType = new SincBandStopFilter(this);
                break;
            case 4:
                filterType = new CustomFIRFilter(this);
                break;
            case 5:
                filterType = new NoFilter(this);
                break;
            case 6:
                filterType = new ButterLowPass(this);
                break;
            case 7:
                filterType = new ButterHighPass(this);
                break;
            case 8:
                filterType = new ButterBandPass(this);
                break;
            case 9:
                filterType = new ButterBandStop(this);
                break;
            case 10:
                filterType = new ChebyLowPass(this);
                break;
            case 11:
                filterType = new ChebyHighPass(this);
                break;
            case 12:
                filterType = new ChebyBandPass(this);
                break;
            case 13:
                filterType = new ChebyBandStop(this);
                break;
            case 14:
                filterType = new InvChebyLowPass(this);
                break;
            case 15:
                filterType = new InvChebyHighPass(this);
                break;
            case 16:
                filterType = new InvChebyBandPass(this);
                break;
            case 17:
                filterType = new InvChebyBandStop(this);
                break;
            case 18:
                filterType = new EllipticLowPass(this);
                break;
            case 19:
                filterType = new EllipticHighPass(this);
                break;
            case 20:
                filterType = new EllipticBandPass(this);
                break;
            case 21:
                filterType = new EllipticBandStop(this);
                break;
            case 22:
                filterType = new CombFilter(this, 1);
                break;
            case 23:
                filterType = new CombFilter(this, -1);
                break;
            case 24:
                filterType = new DelayFilter(this);
                break;
            case 25:
                filterType = new PluckedStringFilter(this);
                break;
            case 26:
                filterType = new InverseCombFilter(this);
                break;
            case 27:
                filterType = new ResonatorFilter(this);
                break;
            case 28:
                filterType = new ResonatorZeroFilter(this);
                break;
            case 29:
                filterType = new NotchFilter(this);
                break;
            case 30:
                filterType = new MovingAverageFilter(this);
                break;
            case 31:
                filterType = new TriangleFilter(this);
                break;
            case 32:
                filterType = new AllPassFilter(this);
                break;
            case 33:
                filterType = new GaussianFilter(this);
                break;
            case 34:
                filterType = new RandomFilter(this);
                break;
            case 35:
                filterType = new CustomIIRFilter(this);
                break;
            case 36:
                filterType = new BoxFilter(this);
                break;
        }
        if (filterSelection != filt)
        {
            filterSelection = filt;
            int i;
            for (i = 0; i != auxBars.length; i++)
            {
                auxBars[i].setMaximum(999);
            }
            int ax = filterType.select();
            for (i = 0; i != ax; i++)
            {
                auxLabels[i].setVisible(true);
                auxBars[i].setVisible(true);
            }
            for (i = ax; i != auxBars.length; i++)
            {
                auxLabels[i].setVisible(false);
                auxBars[i].setVisible(false);
            }
            if (filterType.needsWindow())
            {
                windowChooser.setVisible(true);
                setWindow();
            }
            else
            {
                windowChooser.setVisible(false);
                setWindow();
            }
            validate();
        }
        filterType.setup();
        curFilter = null;
    }

    void setInputLabel()
    {
        wformInfo = getWaveformObject();
        String inText = wformInfo.getInputText();
        if (inText == null)
        {
            inputLabel.setVisible(false);
            inputBar.setVisible(false);
        }
        else
        {
            inputLabel.setText(inText);
            inputLabel.setVisible(true);
            inputBar.setVisible(true);
        }
        validate();
    }

    Waveform getWaveformObject()
    {
        Waveform wform;
        int ic = inputChooser.getSelectedIndex();
        switch (ic)
        {
            case 0:
                wform = new NoiseWaveform(this);
                break;
            case 1:
                wform = new SineWaveform(this);
                break;
            case 2:
                wform = new SawtoothWaveform(this);
                break;
            case 3:
                wform = new TriangleWaveform(this);
                break;
            case 4:
                wform = new SquareWaveform(this);
                break;
            case 5:
                wform = new PeriodicNoiseWaveform(this);
                break;
            case 6:
                wform = new SweepWaveform(this);
                break;
            case 7:
                wform = new ImpulseWaveform(this);
                break;
            default:
                wform = new Wave16Waveform(this);
                break;
        }
        return wform;
    }

    public void componentHidden(ComponentEvent e)
    {
    }

    public void componentMoved(ComponentEvent e)
    {
    }

    public void componentShown(ComponentEvent e)
    {
        cv.repaint();
    }

    public void componentResized(ComponentEvent e)
    {
        handleResize();
        cv.repaint();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == snapShot)
        {
            m_bucket.reset();
            while (!m_bucket.isFull())
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e1)
                {
                    e1.printStackTrace();
                }
            }
            Wave16 w = new Wave16(m_bucket.getArray(), sampleRate, m_bucket.getSize()).functionsAmplitude.fitValues();
            FrameManager.getInstance().createFrame(w, "Applet snapshot");
        }
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        setupFilter();
        //System.out.print(((Scrollbar) e.getSource()).getValue() + "\n");
        if ((e.getSource()) == inputBar)
        {
            setInputW();
        }
        cv.repaint();
    }

    void setInputW()
    {
        inputW = pi * inputBar.getValue() / 1000.;
    }

    @Override
    public boolean handleEvent(Event ev)
    {
        if (ev.id == Event.WINDOW_DESTROY)
        {
            if (playThread != null)
            {
                playThread.requestShutdown();
            }
            dispose();
            //applet.destroyFrame();
            return true;
        }
        return super.handleEvent(ev);
    }

    public void mouseDragged(MouseEvent e)
    {
        mouseX = e.getX();
        mouseY = e.getY();
        edit(e);
        cv.repaint();
    }

    public void mouseMoved(MouseEvent e)
    {
        mouseX = e.getX();
        dragX = mouseX;
        mouseY = e.getY();
        dragY = mouseY;
        cv.repaint();
        if (respView != null && respView.contains(e.getX(), e.getY()))
        {
            selection = SELECT_RESPONSE;
        }
        if (spectrumView != null && spectrumView.contains(e.getX(), e.getY()))
        {
            selection = SELECT_SPECTRUM;
        }
        if (polesView != null && polesView.contains(e.getX(), e.getY()) &&
            !ferrisCheckItem.getState())
        {
            selection = SELECT_POLES;
            selectPoleZero(e.getX(), e.getY());
        }
    }

    void selectPoleZero(int x, int y)
    {
        selectedPole = -1;
        selectedZero = -1;
        int i;
        int ph = polesView.height / 2;
        int cx = polesView.x + ph;
        int cy = polesView.y + ph;
        Complex c1 = new Complex();
        int polect = filterType.getPoleCount();
        int zeroct = filterType.getZeroCount();
        int bestdist = 10000;
        for (i = 0; i != polect; i++)
        {
            filterType.getPole(i, c1);
            int c1x = cx + (int) (ph * c1.re);
            int c1y = cy - (int) (ph * c1.im);
            int dist = distanceSq(c1x, c1y, x, y);
            if (dist <= bestdist)
            {
                bestdist = dist;
                selectedPole = i;
                selectedZero = -1;
            }
        }
        for (i = 0; i != zeroct; i++)
        {
            filterType.getZero(i, c1);
            int c1x = cx + (int) (ph * c1.re);
            int c1y = cy - (int) (ph * c1.im);
            int dist = distanceSq(c1x, c1y, x, y);
            if (dist < bestdist)
            {
                bestdist = dist;
                selectedPole = -1;
                selectedZero = i;
            }
        }
    }

    int distanceSq(int x1, int y1, int x2, int y2)
    {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        mouseMoved(e);
        edit(e);
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    void edit(MouseEvent e)
    {
        if (selection == SELECT_RESPONSE)
        {
            if (filterType instanceof CustomFIRFilter)
            {
                editCustomFIRFilter(e);
                return;
            }
            double f = getFreqFromX(e.getX(), respView);
            if (f < 0)
            {
                return;
            }
            filterType.setCutoff(f);
            setupFilter();
        }
        if (selection == SELECT_SPECTRUM)
        {
            if (!wformInfo.needsFrequency())
            {
                return;
            }
            double f = getFreqFromX(e.getX(), spectrumView);
            if (f < 0)
            {
                return;
            }
            inputW = 2 * pi * f;
            inputBar.setValue((int) (2000 * f));
        }
        if (selection == SELECT_POLES && filterType instanceof CustomIIRFilter)
        {
            editCustomIIRFilter(e);
        }
    }

    void editCustomFIRFilter(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if (dragX == x)
        {
            editCustomFIRFilterPoint(x, y);
            dragY = y;
        }
        else
        {
            // need to draw a line from old x,y to new x,y and
            // call editFuncPoint for each point on that line.  yuck.
            int x1 = (x < dragX) ? x : dragX;
            int y1 = (x < dragX) ? y : dragY;
            int x2 = (x > dragX) ? x : dragX;
            int y2 = (x > dragX) ? y : dragY;
            dragX = x;
            dragY = y;
            for (x = x1; x <= x2; x++)
            {
                y = y1 + (y2 - y1) * (x - x1) / (x2 - x1);
                editCustomFIRFilterPoint(x, y);
            }
        }
        setupFilter();
    }

    void editCustomFIRFilterPoint(int x, int y)
    {
        double xx1 = getFreqFromX(x, respView) * 2;
        double xx2 = getFreqFromX(x + 1, respView) * 2;
        y -= respView.y;
        double ym = .069;
        double yy = Math.exp(-y * Math.log(10) / (ym * 4 * respView.height));
        if (yy >= 1)
        {
            yy = 1;
        }
        ((CustomFIRFilter) filterType).edit(xx1, xx2, yy);
    }

    void editCustomIIRFilter(MouseEvent e)
    {
        if (ferrisCheckItem.getState())
        {
            return;
        }
        int x = e.getX();
        int y = e.getY();
        int ph = polesView.height / 2;
        int cx = polesView.x + ph;
        int cy = polesView.y + ph;
        Complex c1 = new Complex();
        c1.set((x - cx) / (double) ph, (y - cy) / (double) ph);
        ((CustomIIRFilter) filterType).editPoleZero(c1);
        setupFilter();
    }

    public void itemStateChanged(ItemEvent e)
    {
        filterChanged = true;
        if (e.getSource() == displayCheck)
        {
            cv.repaint();
            return;
        }
        if (e.getSource() == inputChooser)
        {
            if (playThread != null)
            {
                playThread.requestShutdown();
            }
            setInputLabel();
        }
        if ((e.getSource()) == rateChooser)
        {
            if (playThread != null)
            {
                playThread.requestShutdown();
            }
            inputW *= sampleRate;
            switch (rateChooser.getSelectedIndex())
            {
                case 0:
                    sampleRate = 8000;
                    break;
                case 1:
                    sampleRate = 11025;
                    break;
                case 2:
                    sampleRate = 16000;
                    break;
                case 3:
                    sampleRate = 22050;
                    break;
                case 4:
                    sampleRate = 32000;
                    break;
                case 5:
                    sampleRate = 44100;
                    break;
            }
            inputW /= sampleRate;
        }
        if ((e.getSource()) == shiftSpectrumCheck)
        {
            if (shiftSpectrumCheck.getState())
            {
                shiftFreqLabel.setVisible(true);
                shiftFreqBar.setVisible(true);
            }
            else
            {
                shiftFreqLabel.setVisible(false);
                shiftFreqBar.setVisible(false);
            }
            validate();
        }
        if ((e.getSource()) == windowChooser)
        {
            setWindow();
        }
        if (e.getSource() instanceof CheckboxMenuItem)
        {
            handleResize();
        }
        else
        {
            setupFilter();
        }
        cv.repaint();
    }

    void setWindow()
    {
        if (windowChooser.getSelectedIndex() == WINDOW_KAISER &&
            filterType.needsWindow())
        {
            kaiserLabel.setVisible(true);
            kaiserBar.setVisible(true);
        }
        else
        {
            kaiserLabel.setVisible(false);
            kaiserBar.setVisible(false);
        }
        validate();
    }

//    void setSampleRate(int r)
//    {
//        int x = 0;
//        switch (r)
//        {
//            case 8000:
//                x = 0;
//                break;
//            case 11025:
//                x = 1;
//                break;
//            case 16000:
//                x = 2;
//                break;
//            case 22050:
//                x = 3;
//                break;
//            case 32000:
//                x = 4;
//                break;
//            case 44100:
//                x = 5;
//                break;
//        }
//        rateChooser.select(x);
//        sampleRate = r;
//    }

    class PlayThread extends Thread
    {
        SourceDataLine line;
        Waveform wform;
        boolean shutdownRequested;
        Filter filt, newFilter;
        double fbufLi[];
        //double fbufRi[];
        double fbufLo[];
        //double fbufRo[];
        double stateL[];
        int fbufmask, fbufsize;
        int spectrumOffset, spectrumLen;

        PlayThread()
        {
            shutdownRequested = false;
        }

        void requestShutdown()
        {
            shutdownRequested = true;
        }

        void setFilter(Filter f)
        {
            newFilter = f;
        }

        SourceDataLine openLine()
        {
            SourceDataLine lin = null;
            try
            {
                AudioFormat playFormat =
                        new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                                                       playFormat);

//                if (!AudioSystem.isLineSupported(info))
//                {
//                    throw new LineUnavailableException(
//                            "sorry, the sound format cannot be played");
//                }
                lin = (SourceDataLine) AudioSystem.getLine(info);
                lin.open(playFormat, getPower2(sampleRate / 4));
                lin.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return lin;
        }

        int inbp, outbp;
        int spectCt;

        public void run()
        {
            try
            {
                doRun();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            playThread = null;
        }

        void doRun()
        {
            rateChooser.setEnabled(true);
            wform = getWaveformObject();
            unstable = false;
            if (!wform.start())
            {
                cv.repaint();
                try
                {
                    Thread.sleep(1000L);
                }
                catch (Exception ignored)
                {
                }
                return;
            }

            fbufsize = 32768;
            fbufmask = fbufsize - 1;
            fbufLi = new double[fbufsize];
            fbufLo = new double[fbufsize];
            line = openLine();
            inbp = 0;
            outbp = 0;
            spectCt = 0;
            outputGain = 1;
            filt = curFilter;
            newFilter = filt;
            spectrumLen = getPower2(sampleRate / 12);
            int gainCounter = 0;
            boolean maxGain = true;
            boolean useConvolve = false;

            outputBuffer = new byte[16384];
            int shiftCtr = 0;
            while (!shutdownRequested && soundCheck.getState() /*&& applet.ogf != null*/)
            {
                //System.out.println("nf " + newFilter + " " +(inbp-outbp));
                if (newFilter != null)
                {
                    gainCounter = 0;
                    maxGain = !(wform instanceof SweepWaveform ||
                                wform instanceof SineWaveform);
                    outputGain = 1;
                    // we avoid doing this unless necessary because it sounds bad
                    if (filt == null || filt.getLength() != newFilter.getLength())
                    {
                        convBufPtr = 0;
                        inbp = 0;
                        outbp = 0;
                        spectCt = 0;
                    }
                    filt = newFilter;
                    newFilter = null;
                    impulseBuf = null;
                    useConvolve = filt.useConvolve();
                    stateL = filt.createState();
                    //stateR = filt.createState();
                }
                int length = wform.getData();
                if (length == 0)
                {
                    break;
                }
                short ib[] = wform.buffer;

                int i2;
                int i = inbp;
                for (i2 = 0; i2 < length; i2 += 1)
                {
                    fbufLi[i] = ib[i2];
                    i = (i + 1) & fbufmask;
                }
                if (shiftSpectrumCheck.getState())
                {
                    double shiftFreq = shiftFreqBar.getValue() * pi / 1000.;
                    if (shiftFreq > pi)
                    {
                        shiftFreq = pi;
                    }
                    i = inbp;
                    for (i2 = 0; i2 < length; i2 += 1)
                    {
                        double q = Math.cos(shiftFreq * shiftCtr++);
                        fbufLi[i] *= q;
                        i = (i + 1) & fbufmask;
                    }
                }

                if (useConvolve)
                {
                    doConvolveFilter(length, maxGain);
                }
                else
                {
                    doFilter(length);
                    if (unstable)
                    {
                        break;
                    }
                    int outlen = length * 2;
                    doOutput(outlen, maxGain);
                }

                if (unstable)
                {
                    break;
                }

                if (spectCt >= spectrumLen)
                {
                    spectrumOffset = (outbp - spectrumLen) & fbufmask;
                    spectCt -= spectrumLen;
                    cv.repaint();
                }
                gainCounter += length;
                if (maxGain && gainCounter >= sampleRate)
                {
                    gainCounter = 0;
                    maxGain = false;
                    //System.out.println("gain ctr up " + outputGain);
                }
            }
            if (shutdownRequested || unstable || !soundCheck.getState())
            {
                line.flush();
            }
            else
            {
                line.drain();
            }
            cv.repaint();
        }

        void doFilter(int sampleCount)
        {
            filt.run(fbufLi, fbufLo, inbp, fbufmask, sampleCount, stateL);
            inbp = (inbp + sampleCount) & fbufmask;
            double q = fbufLo[(inbp - 1) & fbufmask];
            if (Double.isNaN(q) || Double.isInfinite(q))
            {
                unstable = true;
            }
        }

        double impulseBuf[], convolveBuf[];
        int convBufPtr;
        FFT convFFT;

        void doConvolveFilter(int sampleCount, boolean maxGain)
        {
            int i;
            int fi2 = inbp, i20;
            double filtA[] = ((DirectFilter) filt).aList;
            int cblen = getPower2(512 + filtA.length * 2);
            if (convolveBuf == null || convolveBuf.length != cblen)
            {
                convolveBuf = new double[cblen];
            }
            if (impulseBuf == null)
            {
                // take FFT of the impulse response
                impulseBuf = new double[cblen];
                for (i = 0; i != filtA.length; i++)
                {
                    impulseBuf[i * 2] = filtA[i];
                }
                convFFT = new FFT(convolveBuf.length / 2);
                convFFT.transform(impulseBuf, false);
            }
            int cbptr = convBufPtr;
            // result = impulseLen+inputLen-1 samples long; result length
            // is fixed, so use it to get inputLen
            int cbptrmax = convolveBuf.length + 2 - 2 * filtA.length;
            //System.out.println("reading " + sampleCount);
            for (i = 0; i != sampleCount; i++, fi2++)
            {
                i20 = fi2 & fbufmask;
                convolveBuf[cbptr] = fbufLi[i20];
                //convolveBuf[cbptr + 1] = fbufRi[i20];
                cbptr += 2;
                if (cbptr == cbptrmax)
                {
                    // buffer is full, do the transform
                    convFFT.transform(convolveBuf, false);
                    double mult = 2. / cblen;
                    int j;
                    // multiply transforms to get convolution
                    for (j = 0; j != cblen; j += 2)
                    {
                        double a = convolveBuf[j] * impulseBuf[j] -
                                   convolveBuf[j + 1] * impulseBuf[j + 1];
                        double b = convolveBuf[j] * impulseBuf[j + 1] +
                                   convolveBuf[j + 1] * impulseBuf[j];
                        convolveBuf[j] = a * mult;
                        convolveBuf[j + 1] = b * mult;
                    }
                    // inverse transform to get signal
                    convFFT.transform(convolveBuf, true);
                    int fj2 = outbp, j20;
                    int overlap = cblen - cbptrmax;
                    // generate output that overlaps with old data
                    for (j = 0; j != overlap; j += 2, fj2++)
                    {
                        j20 = fj2 & fbufmask;
                        fbufLo[j20] += convolveBuf[j];
                        //fbufRo[j20] += convolveBuf[j + 1];
                    }
                    // generate new output
                    for (; j != cblen; j += 2, fj2++)
                    {
                        j20 = fj2 & fbufmask;
                        fbufLo[j20] = convolveBuf[j];
                        //fbufRo[j20] = convolveBuf[j + 1];
                    }
                    cbptr = 0;
                    // output the sound
                    doOutput(cbptrmax, maxGain);
                    //System.out.println("outputting " + cbptrmax);
                    // clear transform buffer
                    for (j = 0; j != cblen; j++)
                    {
                        convolveBuf[j] = 0;
                    }
                }
            }
            inbp = fi2 & fbufmask;
            convBufPtr = cbptr;
        }

        void doOutput(int outlen, boolean maxGain)
        {
            if (outputBuffer.length < outlen)
            {
                outputBuffer = new byte[outlen];
            }
            int qi;
            int i, i2;
            while (true)
            {
                int max = 0;
                i = outbp;
                for (i2 = 0; i2 < outlen; i2 += 2)
                {
                    qi = (int) (fbufLo[i] * outputGain);
                    if (qi > max)
                    {
                        max = qi;
                    }
                    if (qi < -max)
                    {
                        max = -qi;
                    }
                    outputBuffer[i2 + 1] = (byte) (qi >> 8);
                    outputBuffer[i2] = (byte) qi;
                    i = (i + 1) & fbufmask;
                }
                // if we're getting overflow, adjust the gain
                if (max > 32767)
                {
                    //System.out.println("max = " + max);
                    outputGain *= 30000. / max;
                    if (outputGain < 1e-8 || Double.isInfinite(outputGain))
                    {
                        unstable = true;
                        break;
                    }
                    continue;
                }
                else if (maxGain && max < 24000)
                {
                    if (max == 0)
                    {
                        if (outputGain == 1)
                        {
                            break;
                        }
                        outputGain = 1;
                    }
                    else
                    {
                        outputGain *= 30000. / max;
                    }
                    continue;
                }
                break;
            }
            if (unstable)
            {
                return;
            }
            //int oldoutbp = outbp;
            outbp = i;

            line.write(outputBuffer, 0, outlen);
            m_bucket.put(outputBuffer, outlen);
            spectCt += outlen / 2;
        }
    }

    String getOmegaText(double wc)
    {
        return ((int) (wc * sampleRate / (2 * pi))) + " Hz";
    }

    double cosh(double x)
    {
        return .5 * (Math.exp(x) + Math.exp(-x));
    }

//    double sinh(double x)
//    {
//        return .5 * (Math.exp(x) - Math.exp(-x));
//    }

    double acosh(double x)
    {
        return Math.log(x + Math.sqrt(x * x - 1));
    }

    String getUnitText(double v, String u)
    {
        double va = Math.abs(v);
        if (va < 1e-17)
        {
            return "0 " + u;
        }
        if (va < 1e-12)
        {
            return showFormat.format(v * 1e15) + " f" + u;
        }
        if (va < 1e-9)
        {
            return showFormat.format(v * 1e12) + " p" + u;
        }
        if (va < 1e-6)
        {
            return showFormat.format(v * 1e9) + " n" + u;
        }
        if (va < 1e-3)
        {
            return showFormat.format(v * 1e6) + " \u03bc" + u;
        }
        if (va < 1e-2 || (u.compareTo("m") != 0 && va < 1))
        {
            return showFormat.format(v * 1e3) + " m" + u;
        }
        if (va < 1)
        {
            return showFormat.format(v * 1e2) + " c" + u;
        }
        if (va < 1e3)
        {
            return showFormat.format(v) + " " + u;
        }
        if (va < 1e6)
        {
            return showFormat.format(v * 1e-3) + " k" + u;
        }
        if (va < 1e9)
        {
            return showFormat.format(v * 1e-6) + " M" + u;
        }
        if (va < 1e12)
        {
            return showFormat.format(v * 1e-9) + " G" + u;
        }
        if (va < 1e15)
        {
            return showFormat.format(v * 1e-12) + " T" + u;
        }
        return v + " " + u;
    }

    double bessi0(double x)
    {
        double ax, ans;
        double y;

        ax = Math.abs(x);
        if (ax < 3.75)
        {
            y = x / 3.75;
            y *= y;
            ans = 1.0 + y * (3.5156229 + y * (3.0899424 + y * (1.2067492
                                                               + y * (0.2659732 + y * (0.360768e-1 + y * 0.45813e-2)))));
        }
        else
        {
            y = 3.75 / ax;
            ans = (Math.exp(ax) / Math.sqrt(ax)) * (0.39894228 + y * (0.1328592e-1
                                                                      + y * (0.225319e-2 + y * (-0.157565e-2 + y * (0.916281e-2
                                                                                                                    + y * (-0.2057706e-1 + y * (0.2635537e-1 + y * (-0.1647633e-1
                                                                                                                                                                    + y * 0.392377e-2))))))));
        }
        return ans;
    }

    double uresp[];

    Complex customPoles[], customZeros[];

}
