
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

//-------PDF------//
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;


public class PdfKleber extends JPanel
        implements ActionListener {
    static private final String newline = "\n";
    JButton openButton1,openButton2, stickButton;
    JTextArea log;
    JFileChooser fc1,fc2;
    //

    public PdfKleber() {
        super(new BorderLayout());

        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);
        fc1 = new JFileChooser();
        fc2 = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf Files", "pdf");
        fc1.setFileFilter(filter);
        fc2.setFileFilter(filter);

        openButton1 = new JButton("1te pdf File öffnen.");
        openButton1.addActionListener(this);

        openButton2 = new JButton("2te pdf File öffnen.");
        openButton2.addActionListener(this);

        stickButton = new JButton("Kleben!");
        stickButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton1);
        buttonPanel.add(openButton2);
        buttonPanel.add(stickButton);

        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }



    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == openButton1) {
            int returnVal = fc1.showOpenDialog(PdfKleber.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file1 = fc1.getSelectedFile();
                log.append("Öffnung: " + file1.getName() + "." + newline);
            } else {
                log.append("Stopped bei user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());

        }

        if (e.getSource() == openButton2) {
            int returnVal = fc2.showOpenDialog(PdfKleber.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file2 = fc2.getSelectedFile();
                log.append("Öffnung: " + file2.getName() + "." + newline);
            } else {
                log.append("Stopped bei user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());

        }

        else if (e.getSource() == stickButton) {
            List<InputStream> list = new ArrayList<InputStream>();
            try {
                list.add(new FileInputStream(fc1.getSelectedFile()));
                list.add(new FileInputStream(fc2.getSelectedFile()));
                OutputStream out = new FileOutputStream(new File("result.pdf"));
                doMerge(list, out);
                log.append("Kleben und Export in Result.pdf" + newline);
            }

            catch(FileNotFoundException ex){
                log.append("Fehler!" + newline);
            }
            catch(DocumentException ex){
                log.append("Fehler!" + newline);
            }
            catch(IOException ex){
                log.append("Fehler!" + newline);
            }
            catch(NullPointerException ex){
                log.append("Bitte, Wählen Sie pdf files!"+ newline);
            }

            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    public static void doMerge(List<InputStream> list, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();

        for (InputStream in : list) {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                PdfImportedPage page = writer.getImportedPage(reader, i);
                cb.addTemplate(page, 0, 0);
            }
        }

        outputStream.flush();
        document.close();
        outputStream.close();
    }


    private static void init() {

        JFrame frame = new JFrame("PdfKleber");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.add(new PdfKleber());

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                init();
            }
        });
    }
}
