import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robin on 11/5/16.
 */
public class MyFrame extends JFrame {
    private ChartPanel timeSeriesGraphPanel;
    private ChartPanel fftGraphPanel;
    private JButton revalidateButton;
    private XYPlot timeSeriesPlot;
    private XYPlot fftPlot;
    private JPanel sidePanel;
    private JLabel indicator;
    public MyFrame(){
        this.setSize(new Dimension(100,100));
        Container container=this.getContentPane();
        FlowLayout flowLayout=new FlowLayout();
        container.setLayout(flowLayout);
        revalidateButton=new JButton("Revalidate");
        sidePanel=new JPanel();
        indicator=new JLabel();
        sidePanel.setLayout(new BoxLayout(sidePanel,BoxLayout.Y_AXIS));

        this.setVisible(true);


        final JFreeChart timeSeriesChart = ChartFactory.createXYLineChart(
                "CodeTimeSeries",          // chart title
                "time",               // domain axis label
                "power",                  // range axis label
                null,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,
                false
        );
        timeSeriesPlot = timeSeriesChart.getXYPlot();
        final NumberAxis timeSeriesDomainAxis = new NumberAxis("time");
        final NumberAxis timeSeriesRangeAxis = new NumberAxis("energy");
        timeSeriesPlot.setDomainAxis(timeSeriesDomainAxis);
        timeSeriesPlot.setRangeAxis(timeSeriesRangeAxis);
        timeSeriesDomainAxis.setRange(new Range(0,100));
        timeSeriesRangeAxis.setRange(new Range(-1.5,5));
        timeSeriesChart.setBackgroundPaint(Color.white);
        timeSeriesPlot.setOutlinePaint(Color.black);
        timeSeriesGraphPanel = new ChartPanel(timeSeriesChart);
        timeSeriesGraphPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        final JFreeChart fftChart = ChartFactory.createXYLineChart(
                "Log Axis Demo",          // chart title
                "Category",               // domain axis label
                "Value",                  // range axis label
                null,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,
                false
        );

        fftPlot = fftChart.getXYPlot();
        final NumberAxis fftDomainAxis = new NumberAxis("Hz");
        final NumberAxis fftRangeAxis = new LogarithmicAxis("FFT");
        fftPlot.setDomainAxis(fftDomainAxis);
        fftPlot.setRangeAxis(fftRangeAxis);
        fftRangeAxis.setRange(new Range(0.01,20));
        fftChart.setBackgroundPaint(Color.white);
        fftPlot.setOutlinePaint(Color.black);
        fftGraphPanel = new ChartPanel(fftChart);
        fftGraphPanel.setPreferredSize(new java.awt.Dimension(500, 270));


        container.add(timeSeriesGraphPanel);
        container.add(fftGraphPanel);
        container.add(sidePanel);
        sidePanel.add(revalidateButton);
        sidePanel.add(indicator);
        this.pack();
        revalidateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRevalidate();
            }
        });
    }
    public void setIndicator(String state){
        this.indicator.setText(state);
    }
    public void updateFFTPlot(double[] fftData){
        final XYSeries fft = new XYSeries( "FFT" );
        final XYSeries fft19000 = new XYSeries("19000");
        for(int i=0;i<fftData.length;i+=2){
            double hz=((i/1.0)/fftData.length)*Main.SAMPLES_RATE;
            double vlen=Math.sqrt(fftData[i]*fftData[i]+fftData[i+1]*fftData[i+1]);
            if(vlen!=0)
                fft.add(hz,vlen);
            if(hz>22000)
                break;
            double band=Math.abs(hz-19000);
            if(band<200&&vlen!=0)
                fft19000.add(hz,vlen);
        }
        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( fft );
        dataset.addSeries(fft19000);
        fftPlot.setDataset(dataset);
    }
    private static long timeKeeper=0;
    public void updateTimeSeriesPlot(double[] timeSeries, double[] speakerSeries)
    {
        long count=timeKeeper;
        final XYSeries time = new XYSeries( "timeseries" );
        final XYSeries original=new XYSeries("original");
        for(int i=0;i<timeSeries.length;i++){
            time.add(count,timeSeries[i]);
            count++;
        }
        count=timeKeeper;
        for(int i=0;i<timeSeries.length;i++){
            original.add(count,speakerSeries[i%speakerSeries.length]);
            count++;
        }
        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( time );
        dataset.addSeries(original);
        timeKeeper++;
        final NumberAxis timeSeriesDomainAxis = new NumberAxis("time");
        timeSeriesDomainAxis.setRange(new Range(timeKeeper,timeKeeper+100));
        timeSeriesPlot.setDomainAxis(timeSeriesDomainAxis);
        timeSeriesPlot.setDataset(dataset);
    }
    public void addRevalidateListener(RevalidateListener revalidateListener){
        revalidateListeners.add(revalidateListener);
    }
    public void removeRevalidateListener(RevalidateListener revalidateListener){
        revalidateListeners.remove(revalidateListener);
    }
    public void onRevalidate(){
        for(RevalidateListener revalidateListener:revalidateListeners){
            revalidateListener.onRevalidate(this);
        }
    }
    private List<RevalidateListener> revalidateListeners=new ArrayList<RevalidateListener>();
    public interface RevalidateListener{
        void onRevalidate(JFrame frame);
    }
}
