package com.mcreater.amcl.patcher;

import com.mcreater.amcl.StableMain;
import com.mcreater.amcl.pages.interfaces.Fonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class DepencyLoadingFrame extends JFrame {
    public final JProgressBar progressBar;
    public final JButton button;
    public final JLabel label;
    public DepencyLoadingFrame() {
        super();
        getContentPane().setLayout(new GridBagLayout());
        setTitle(StableMain.manager.get("ui.pre.depencies.title"));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int) (dimension.getWidth() / 2 - 300 / 2), (int) (dimension.getHeight() / 2 - 150 / 2), 300, 150);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.gridx = 0;
        getContentPane().add(progressBar, gridBagConstraints_1);

        label = new JLabel(StableMain.manager.get("ui.pre.depencies.progress.pre"));
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(12, 0, 0, 0);
        gridBagConstraints_3.gridy = 1;
        gridBagConstraints_3.gridx = 0;
        getContentPane().add(label, gridBagConstraints_3);
        label.setFont(Fonts.awt_t_f);

        button = new JButton();
        button.setText(StableMain.manager.get("ui.pre.depencies.finish.name"));
        button.setEnabled(false);
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(12, 0, 0, 0);
        gridBagConstraints_2.gridy = 2;
        gridBagConstraints_2.gridx = 0;
        getContentPane().add(button, gridBagConstraints_2);
        button.setFont(Fonts.awt_t_f);
    }
    public void processWindowEvent(final WindowEvent e) {

    }
}
