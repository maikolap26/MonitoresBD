package monitores;

import Conexión.DB;
import File.Transaccion;
import File.WriteFile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RefineryUtilities;

public class Monitores extends JFrame {

    private final DB db = new DB();
    private final JFrame ventana;
    CategoryDataset dataset;
    JFreeChart chart;
    ChartPanel chartPanel;
    JPanel panel;
    JPanel panel2;
    JTable table;
    int numTS;
    String nameTS;
    static Double SGA = 1068937216.0 / 1024;
    static Double HWS = 0.50;
    static String[] col = {"Usuario", "SQL", "Fecha", "Memoria", "Tiempo de ejecución"};
    static Object[][] rows = {};
    static String[] columnNames = {"Table Space", "Days to HWM (Blue)", "Days to full (Red)"};
    public static DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();

    public Monitores(String titel) {
        this.ventana = new JFrame(titel);
        ventana.setLayout(new BorderLayout());
        createMenu();
        dataset = createDataset();
        chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        panel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(500, 350));
        panel.add(chartPanel);
        panel.setBackground(Color.white);
        ventana.add(panel, BorderLayout.LINE_START);
        ventana.setDefaultCloseOperation(EXIT_ON_CLOSE);
        ventana.setResizable(false);
        addTable();
        /*Monitor de linea*/
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Grafico",
                "Tiempo", "Memoria",
                createDataset1(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel1 = new ChartPanel(lineChart);
        chartPanel1.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel1);
    }

    private void createMenu() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        JMenuItem menu1;
        menuBar = new JMenuBar();
        menu = new JMenu("Edit");
        menu1 = new JMenu("Select TS");
        menuItem = new JMenuItem("Change HWM");
        String[] items = db.getTableSpace_name();
        for (String item : items) {
            JMenuItem it = new JMenuItem(item);
            it.addActionListener((ActionEvent e) -> {
                pintar(item);
                table.setModel(agregaRows(db.getDays(-1.0)));
            });
            menu1.add(it);
        }
        
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String HWM = JOptionPane.showInputDialog(
                        "Insert new value.", null);
                db.setHWT(Double.valueOf(HWM));
                if (numTS > 1) {
                    pintar();
                } else {
                    pintar(nameTS);
                }
                table.setModel(agregaRows(db.getDays(-1.0)));
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
        menuBar.add(menu1);
        ventana.add(menuBar, BorderLayout.PAGE_START);
    }

    private void addTable() {
        Object[][] data = {};
        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        ArrayList<Object[]> array = db.getDays(-1.0);
        table.setModel(agregaRows(array));
        panel2 = new JPanel();
        panel2.add(scrollPane);
        panel2.setBackground(Color.white);
        ventana.add(panel2, BorderLayout.LINE_END);
    }

    public static DefaultTableModel agregaRows(ArrayList<Object[]> l) {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames);
        Object[] obj;
        for (Object[] al : l) {
            model.addRow(al);
        }
        return model;
    }

    private void pintar() {
        this.dataset = createDataset();
        this.chart = createChart(dataset);
        this.chartPanel = new ChartPanel(chart);
        panel.remove(0);
        panel.add(chartPanel);
        panel.repaint();
        panel.revalidate();
        ventana.pack();
        //ventana.add(chartPanel);
        //ventana.repaint();
    }

    private void pintar(String name) {
        this.dataset = createDatasetTS(name);
        this.chart = createChart(dataset);
        this.chartPanel = new ChartPanel(chart);
        panel.remove(0);
        panel.add(chartPanel);
        panel.repaint();
        panel.revalidate();
        ventana.pack();

    }

    private CategoryDataset createDataset() {
        numTS = db.NumTs;
        String[] bloques = new String[]{"Used", "Free until HWM", "Free between HWM and Full"};
        return DatasetUtilities.createCategoryDataset(bloques, db.getTableSpace_name(), db.getSize());
    }

    private CategoryDataset createDatasetTS(String name) {
        numTS = 1;
        nameTS = name;
        String[] bloques = new String[]{"Used", "Free until HWM", "Free between HWM and Full"};
        String[] names = {name};
        //db.getTableSpace_name();
        double[][] sizes = db.getSizeTS(name);
        return DatasetUtilities.createCategoryDataset(bloques, names, sizes);
    }

    private JFreeChart createChart(final CategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createStackedBarChart(
                "Stacked Bar Chart ", "", "",
                dataset, PlotOrientation.HORIZONTAL, true, true, false);

//        chart.setBackgroundPaint(new Color(100, 100, 100));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(38, 201, 56));
        plot.getRenderer().setSeriesPaint(1, new Color(90, 98, 221));
        plot.getRenderer().setSeriesPaint(2, new Color(241, 48, 48));

        return chart;
    }

    /*Datos grafica linea*/
    public DefaultCategoryDataset createDataset1() {
        dataset1.addValue(700, "Gasto de memoria", "0");
        dataset1.addValue(0, "Gasto de memoria", "0");
        return dataset1;
    }

    public static DefaultTableModel agregaRows1(ArrayList<Alerta> l) {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, col);
        Object[] obj;
        for (Alerta al : l) {
            obj = al.toString().split(";");
            model.addRow(obj);
        }
        return model;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
        }

        Monitores demo = new Monitores("Stacked Bar Chart");
        demo.ventana.pack();
        RefineryUtilities.centerFrameOnScreen(demo.ventana);
        demo.ventana.setVisible(true);
        //demo.ventana.setResizable(true);
        WriteFile wf = new WriteFile();
        ArrayList<Transaccion> t = WriteFile.read();
        for (Transaccion s : t) {
            System.out.println(s.toString());
        }
        /*Ventana de grafico de linea */
        JFrame ventana = new JFrame("Alertas");
        JTable tabla = new JTable(rows, col);
        ventana.setSize(650, 300);
        ventana.add(new JPanel().add(new JScrollPane(tabla)));
        ventana.setVisible(true);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        DB d = new DB();
        ArrayList<Alerta> alert = WriteFile.read1();
        tabla.setModel(agregaRows1(alert));
        Integer x = 5000;
        Integer y = 1;
        Double memoriaSGA = 0.0;
        while (true) {
            try {
                memoriaSGA = d.consultaMemSGA();
                if ((HWS * SGA) <= memoriaSGA) {
                    d.consulta();
                    alert = WriteFile.read1();
                    tabla.setModel(agregaRows1(alert));
                }
                dataset1.addValue(memoriaSGA, "Gasto de memoria", String.valueOf(y++));
                demo.repaint();
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Monitores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
