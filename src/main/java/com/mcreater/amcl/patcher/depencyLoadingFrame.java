package com.mcreater.amcl.patcher;

import com.mcreater.amcl.StableMain;

import javax.swing.*;
import java.awt.*;

public class depencyLoadingFrame extends JFrame {
    public final JProgressBar progressBar;
    public final JButton button;
    public depencyLoadingFrame() {
        super();
        getContentPane().setLayout(new GridBagLayout());
        setTitle(StableMain.manager.get("ui.pre.depencies.title"));
        setBounds(100, 100, 300, 150);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        progressBar.setString(StableMain.manager.get("ui.pre.depencies.progress.pre"));
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.gridx = 0;
        getContentPane().add(progressBar, gridBagConstraints_1);

        button = new JButton();
        button.setText(StableMain.manager.get("ui.pre.depencies.finish.name"));
        button.setEnabled(false);
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(0, 8, 0, 0);
        gridBagConstraints_2.gridy = 0;
        gridBagConstraints_2.gridx = 1;
        getContentPane().add(button, gridBagConstraints_2);
    }
}
