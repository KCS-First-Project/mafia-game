package network.client.domain;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class StatusComponent extends JPanel {
    private JLabel statusText;
    private JProgressBar jProgressBar;
    private GridBagConstraints gridBagConstraints;

    public StatusComponent() {
        super(new GridBagLayout());
        statusText = new JLabel();
        jProgressBar = new JProgressBar();
        jProgressBar.setStringPainted(true);
        statusText.setHorizontalAlignment(SwingConstants.LEFT);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(statusText, gridBagConstraints);
    }

    public void setStatusText(String text) {
        statusText.setText(text);
    }

    public GridBagConstraints getConstraints() {
        return gridBagConstraints;
    }
}
