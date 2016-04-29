package tests;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import customui.ButtonCustomUI;
import customui.PanelCustomUI;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import methods.Methods;
import methods.Test;
import methods.Utils;

public class Audit extends AbstractTest {

	Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.AUDIT));
	NodeList nodelist = doc.getElementsByTagName("q");

	int summ = 0;
	int answer = 0;
	int currentQuestionNumber = 1;
	final int nQuestions = 10;

	JLabel question = new JLabel();

	JButton nextButton = new JButton(InterfaceTextDefaults.getInstance().getDefault("next"));

	ButtonGroup radioButtonGroup = new ButtonGroup();

	ArrayList<JRadioButton> radioButtonList = new ArrayList<JRadioButton>();

	public Audit(Methods methods, int width, int height, Test test) {
		super(methods, width, height, test);
	}

	@Override
	public void showInfo() {
		showStandartInfo();
	}

	@Override
	public void showSettings() {
		showStandartSettings();
	}

	@Override
	public void showTest() {
		nextButton.setUI(new ButtonCustomUI(new Color(144, 106, 96)));
		nextButton.setBorder(null);
		nextButton.setOpaque(false);
		nextButton.setPreferredSize(new Dimension(200, 35));
		nextButton.setMinimumSize(new Dimension(200, 35));
		nextButton.setMaximumSize(new Dimension(200, 35));
		nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentQuestionNumber >= nQuestions) {
					testTime = new Date().getTime() - testTime;
					showResults();
				} else {
					for (int i = 0; i < radioButtonList.size(); i++)
						if (radioButtonList.get(i).isSelected()) {
							answer = i;
							break;
						}
					
					summ += Integer.parseInt(nodelist.item(currentQuestionNumber - 1).getChildNodes().item(answer)
							.getAttributes().getNamedItem("points").getNodeValue());
							currentQuestionNumber++;
					showQuestion();
				}
			}
		});

		this.removeAll();
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 40, 0, 40);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		question.setHorizontalAlignment(JLabel.CENTER);

		this.add(question, c);

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, 40);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 40, 0, 0);
		c.gridx = 1;
		c.gridy = 10;
		this.add(nextButton, c);

		this.revalidate();
		this.repaint();

		testDate = new Date();
		testTime = new Date().getTime();
		showQuestion();

	}

	public void showQuestion() {

		String questionText = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(144, 106, 96);'>"
				+ nodelist.item(currentQuestionNumber - 1).getAttributes().getNamedItem("text").getNodeValue()
				+ "</div></html>";
		question.setText(questionText);

		// Create Radio Buttons

		for (int i = 0; i < radioButtonList.size(); i++) {
			this.remove(radioButtonList.get(i));
			radioButtonGroup.remove(radioButtonList.get(i));
		}
		
		radioButtonList = new ArrayList<JRadioButton>();

		this.revalidate();

		int optionsNum = nodelist.item(currentQuestionNumber - 1).getChildNodes().getLength();

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(20, 300, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		
		for (int i = 0; i < optionsNum; i++) {
			//TODO "if" statement needed to check for "comments" in xml and output a text pane if it exists
			JRadioButton b = new JRadioButton("<html><div style='font: 18pt Arial Narrow; color: rgb(115, 84, 73);'>"
					+ nodelist.item(currentQuestionNumber - 1).getChildNodes().item(i).getTextContent()
					+ "</div></html>");
			radioButtonList.add(b);
			radioButtonGroup.add(b);
			b.setOpaque(false);
			c.gridy = i + 1;
			this.add(b, c);
		}

		radioButtonList.get(0).setSelected(true);

		this.revalidate();
		this.repaint();
	}

	@Override
	public void showResults() {
		showStandartResults();

		JLabel leftCol = new JLabel();
		JLabel rightCol = new JLabel();

		NodeList d = doc.getElementsByTagName("d");

		String t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(144, 106, 96);'>"
				+ d.item(0).getTextContent() + ": <br>" + "</div></html>";
		leftCol.setText(t);

		t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(144, 106, 96);'>" + summ + "<br>";
		t += "</div></html>";
		rightCol.setText(t);

		t = "<html><div style='font: bold 24pt Arial Narrow; color: rgb(144, 106, 96); padding: 10px;'>";
		if (summ >= 20)
			t += d.item(4).getTextContent().toUpperCase();
		if (summ >= 16 && summ <= 19)
			t += d.item(3).getTextContent().toUpperCase();
		if (summ >= 9 && summ <= 15)
			t += d.item(2).getTextContent().toUpperCase();
		if (summ >= 0 && summ <= 8)
			t += d.item(1).getTextContent().toUpperCase();
		t += "</div></html>";

		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setUI(new PanelCustomUI(true, Color.WHITE, new Color(144, 106, 96)));
		p.add(new JLabel(t));

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 1.0;
		c.weighty = 0.0;

		c.insets = new Insets(10, 200, 0, 0);
		c.gridwidth = 1;
		// leftCol.setPreferredSize(new Dimension(300, 350));
		leftCol.setVerticalAlignment(JLabel.TOP);
		resultsPanel.add(leftCol, c);

		c.gridx = 1;

		c.insets = new Insets(10, 20, 0, 0);
		c.gridwidth = 1;
		// rightCol.setPreferredSize(new Dimension(300, 350));
		rightCol.setVerticalAlignment(JLabel.TOP);
		resultsPanel.add(rightCol, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(50, 0, 0, 0);
		resultsPanel.add(p, c);

		this.revalidate();
		this.repaint();
	}

}