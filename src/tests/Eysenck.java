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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import component.CustomRadioButton;
import component.CustomTextField;
import customui.ButtonCustomUI;
import customui.PanelCustomUI;
import customui.ProgressBarCustomUI;
import defaults.ImageLinkDefaults;
import defaults.InterfaceTextDefaults;
import defaults.TextLinkDefaults;
import methods.Methods;
import methods.Test;
import methods.Utils;

public class Eysenck extends AbstractTest {

	Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.EYSENCK));

	JLabel task;
	JLabel blank;
	JPanel answers;
	CustomTextField input = new CustomTextField(20, "");
	ButtonGroup radioButtonGroup = new ButtonGroup();
	ArrayList<JRadioButton> radioButtonList = new ArrayList<JRadioButton>();

	int currentQuestionNumber = 0;
	int summCorrect = 0;
	String questionType;

	private Timer timer;
	private JProgressBar bar;
	private JLabel timeLeft;

	private final int TIME = 30;

	public Eysenck(Methods methods, int width, int height, Test test) {
		super(methods, width, height, test);
	}

	@Override
	public void showInfo() {
		showStandartInfo();
	}

	@Override
	public void showTest() {
		currentQuestionNumber = 0;
		summCorrect = 0;

		task = new JLabel();
		blank = new JLabel();
		answers = new JPanel();
		answers.setOpaque(false);

		JButton nextButton = new JButton(InterfaceTextDefaults.getInstance().getDefault("next"));
		nextButton.setUI(new ButtonCustomUI(new Color(144, 106, 96)));
		nextButton.setBorder(null);
		nextButton.setOpaque(false);
		nextButton.setPreferredSize(new Dimension(200, 35));
		nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String correctAnswer = doc.getElementsByTagName("q").item(currentQuestionNumber).getAttributes().getNamedItem("answer").getNodeValue().toUpperCase();
				switch (questionType) {
				case "text":
					if (correctAnswer.equals(input.getText().trim().toUpperCase())) summCorrect++;
					break;
				case "radio":
					int selected = 0;
					for (int i = 0; i < radioButtonList.size(); i++) {
						if (radioButtonList.get(i).isSelected()) selected = i + 1;
					}
					if (Integer.parseInt(correctAnswer) == selected) summCorrect++;
					break;
				case "picture":
					if (correctAnswer.equals(input.getText().trim().toUpperCase())) summCorrect++;
					break;
				}
				//System.out.println(summCorrect);
				
				if (currentQuestionNumber >= doc.getElementsByTagName("q").getLength() - 1) {
					testTime = new Date().getTime() - testTime;
					showResults();
				} else {
					currentQuestionNumber++;
					showQuestion();
				}
			}
		});

		bar = new JProgressBar();
		bar.setStringPainted(false);
		bar.setMinimum(0);
		bar.setMaximum(TIME);
		bar.setValue(0);
		bar.setUI(new ProgressBarCustomUI());
		bar.setBorder(null);

		timeLeft = new JLabel("<html><div style='font: 16pt Arial Narrow; color: rgb(144, 106, 96);'>"
				+ InterfaceTextDefaults.getInstance().getDefault("time_left").toUpperCase() + ": " + TIME + " "
				+ InterfaceTextDefaults.getInstance().getDefault("min").toUpperCase() + "</div></html>");

		this.removeAll();
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;

		add(task, c);

		c.gridy = 1;
		c.insets = new Insets(10, 0, 10, 0);
		answers.setPreferredSize(new Dimension(800, 350));
		answers.setMinimumSize(new Dimension(800, 350));

		add(answers, c);

		c.anchor = GridBagConstraints.NORTH;
		c.gridy = 3;
		c.weighty = 0.1;
		c.ipady = 5;
		c.insets = new Insets(30, 0, 10, 0);

		add(nextButton, c);

		c.gridy = 4;
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.insets = new Insets(20, 10, 0, 10);
		add(timeLeft, c);

		c.gridy = 5;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.8;
		c.insets = new Insets(0, 10, 20, 10);
		add(bar, c);

		this.revalidate();
		this.repaint();

		timer = new Timer(1000 * 60, new ActionListener() {
			public void actionPerformed(ActionEvent ev) {

				if (bar.getValue() < TIME) {
					bar.setValue(bar.getValue() + 1);
					timeLeft.setText("<html><div style='font: 16pt Arial Narrow; color: rgb(144, 106, 96);'>"
							+ InterfaceTextDefaults.getInstance().getDefault("time_left").toUpperCase() + ": "
							+ (TIME - bar.getValue()) + " " + InterfaceTextDefaults.getInstance().getDefault("min").toUpperCase()
							+ "</div></html>");
					timeLeft.repaint();
				} else {
					testTime = new Date().getTime() - testTime;
					timer.stop();
					showResults();
				}

			}
		});
		timer.start();

		testDate = new Date();
		testTime = new Date().getTime();
		showQuestion();

	}

	public void showQuestion() {
		Node n = doc.getElementsByTagName("q").item(currentQuestionNumber);
		String s = n.getChildNodes().item(0).getTextContent();
		task.setText("<html><div style='font: 24pt Arial Narrow; color: rgb(0, 168, 155);'>" + s.toUpperCase()
				+ "</div></html>");

		questionType = n.getAttributes().getNamedItem("type").getNodeValue();
		answers.removeAll();
		blank.setIcon(null);
		blank.setText("");
		input.setText("");
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(30, 0, 0, 0);
		answers.setLayout(new GridBagLayout());

		switch (questionType) {
		case "text":
			s = n.getAttributes().getNamedItem("blank").getNodeValue();
			blank.setText("<html><div style='font: bold 24pt Arial Narrow; color: rgb(144, 106, 96);'>" + s.toUpperCase()
					+ "</div></html>");
			answers.add(blank, c);
			c.gridy = 1;
			c.ipady = 5;
			answers.add(input, c);
			break;
		case "radio":
			for (int i = 0; i < radioButtonList.size(); i++) {
				this.remove(radioButtonList.get(i));
				radioButtonGroup.remove(radioButtonList.get(i));
			}
			radioButtonList = new ArrayList<JRadioButton>();
			int optionsNum = n.getChildNodes().getLength() - 1;
			for (int i = 0; i < optionsNum; i++) {
				JRadioButton b = new CustomRadioButton(n.getChildNodes().item(i + 1).getTextContent(), false);
				radioButtonList.add(b);
				radioButtonGroup.add(b);
				b.setOpaque(false);
				c.anchor = GridBagConstraints.WEST;
				c.gridy = i;
				c.insets = new Insets(10, 0, 10, 0);
				answers.add(b, c);
			}
			radioButtonList.get(0).setSelected(true);
			break;
		case "picture":
			ImageIcon icon = Utils.createImageIcon(ImageLinkDefaults.getInstance().getLink(ImageLinkDefaults.Key.EYSENCK)
					+ n.getAttributes().getNamedItem("image").getNodeValue());
			blank.setIcon(icon);
			answers.add(blank, c);
			c.gridy = 1;
			c.ipady = 5;
			answers.add(input, c);
			break;
		}

		this.revalidate();
		this.repaint();
	}

	@Override
	public void showResults() {
		showStandartResults();
		
		JLabel leftCol = new JLabel();
		JLabel rightCol = new JLabel();
		
		NodeList d = doc.getElementsByTagName("d");
			
		String t = "<html><div style='font: 20pt Arial Narrow; color: rgb(144, 106, 96); text-align: right;'>"
				+ d.item(0).getTextContent() + ": <br>" + "</div></html>";
		leftCol.setText(t);	
		
		summCorrect = (int) Math.floor(90 + summCorrect * 2); 
		
		t = "<html><div style='font: bold 20pt Arial; color: rgb(38, 166, 154);'>"
				+ summCorrect + "<br>"; 
		t += "</div></html>";
		rightCol.setText(t);
	
		/*t = "<html><div style='font: bold 20pt Arial; color: rgb(144, 106, 96); padding: 10px'>";
		if (summAnxiety <= 11) t += d.item(1).getTextContent().toUpperCase();
		if (summAnxiety >= 12 && summAnxiety <= 30) t += d.item(2).getTextContent().toUpperCase();
		if (summAnxiety >= 31 && summAnxiety <= 45) t += d.item(3).getTextContent().toUpperCase();
		if (summAnxiety >= 46 ) t += d.item(4).getTextContent().toUpperCase();
		t += "</div></html>";
		JPanel conclusion = new JPanel();
		conclusion.add(new JLabel(t));
		conclusion.setUI(new PanelCustomUI(true));*/
		
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
		
		c.insets = new Insets(10, 0, 0, 20);
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 1;
		//leftCol.setPreferredSize(new Dimension(300, 350));
		leftCol.setVerticalAlignment(JLabel.TOP);
		resultsPanel.add(leftCol, c);

		c.gridx = 1;
		
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(10, 20, 0, 0);
		c.gridwidth = 1;
		//rightCol.setPreferredSize(new Dimension(300, 350));
		rightCol.setVerticalAlignment(JLabel.TOP);
		resultsPanel.add(rightCol, c);
		
		/*c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(20, 0, 0, 0);
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 2;
		resultsPanel.add(conclusion, c);*/
		
		this.revalidate();
		this.repaint();
	}

	@Override
	public void showSettings() {
		showStandartSettings();
	}

	@Override
	public void printResults() {
		standartPrintResults();
	}

}
