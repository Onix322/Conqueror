package build_tool.cli.console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Console {

    private Process process;
    private boolean alive = false;
    private JFrame frame;
    private JTextArea textArea;

    public Console(Process process) {
        this.init();
        this.process = process;
    }

    public Console() {
        this.init();
    }

    private void init(){
        this.textArea = this.textArea();
        this.frame = this.frame(textArea);
    }

    public void open(){
        if(process == null){
            throw new IllegalStateException("Process is not set!");
        }
        alive = true;
        frame.setVisible(true);
        frame.setFocusable(true);
        new Thread(() -> {
            try (BufferedReader br = process.inputReader()){
                String line;
                while ((line = br.readLine()) != null){
                    String finalLine = line;
                    SwingUtilities.invokeLater(() -> {
                        textArea.append(finalLine + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }


    public void close(){
        alive = false;
        frame.setVisible(false);
        frame.dispose();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public boolean isAlive(){
        return alive;
    }

    public Process getProcess() {
        return process;
    }

    public Console setProcess(Process process) {
        this.process = process;
        return this;
    }

    private JFrame frame(JComponent... jComponents){
        JFrame frame = new JFrame();

        frame.setSize(800, 600);
        frame.setBackground(new Color(Color.black.getRGB()));
        frame.setTitle("Conqueror");

        for(JComponent jc : jComponents){
            frame.add(jc);
        }
        return frame;
    }

    private JTextArea textArea(){
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setVisible(true);
        textArea.setCaretColor(Color.BLACK);
        return textArea;
    }
}
