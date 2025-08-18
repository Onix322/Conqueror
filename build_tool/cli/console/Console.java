package build_tool.cli.console;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;

public class Console {

    private Process process;
    private boolean alive = false;
    private JFrame frame;
    private JTextArea textArea;
    private JScrollPane scrollPane;

    private final int DEFAULT_WIDTH = 800;
    private final int DEFAULT_HEIGHT = 600;

    public Console(Process process) {
        this.init();
        this.process = process;
    }

    public Console() {
        this.init();
    }

    private void init(){
        this.frame = this.frame();
        this.textArea = this.textArea();
        this.scrollPane = this.scrollPane(textArea);
        frame.add(scrollPane);
    }

    public void open(){
        if(process == null){
            throw new IllegalStateException("Process is not set!");
        }
        alive = true;
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.setFocusableWindowState(true);
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
        frame.dispose();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (process == null) return;
        process.children().forEach(ProcessHandle::destroy);
        process.destroy();
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

        frame.setSize(this.DEFAULT_WIDTH, this.DEFAULT_HEIGHT);
        frame.setBackground(new Color(Color.black.getRGB()));
        frame.setTitle("Conqueror");
        frame.setResizable(true);
        frame.repaint();
        for(JComponent jc : jComponents){
            frame.getContentPane().add(jc);
        }
        return frame;
    }

    private JScrollPane scrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setSize(this.DEFAULT_WIDTH, this.DEFAULT_HEIGHT);
        JScrollBar verticalScrollBar = scrollPane.createVerticalScrollBar();
        verticalScrollBar.setEnabled(true);
        return scrollPane;
    }

    private JTextArea textArea(){
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setVisible(true);
        textArea.setCaretColor(Color.BLACK);
        textArea.setFont(textArea.getFont().deriveFont(16f));
        textArea.setSize(this.DEFAULT_WIDTH, this.DEFAULT_HEIGHT);
        return textArea;
    }
}
