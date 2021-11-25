package com.bgi.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.bgi.util.LogUtil;

public class MyJFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JMenuItem passwordhelp;
	JMenuItem tips;
	JTextArea jta;

	Container con = getContentPane();
	private MyPanel myPanel;

	public MyJFrame() {
		myPanel = new MyPanel(this);
		JMenuBar jmb = new JMenuBar();
		JMenu jm1 = new JMenu("菜单");
		jmb.add(jm1);
		this.passwordhelp = new JMenuItem("文件有密码怎么办");
		this.passwordhelp.setActionCommand("help");
		this.passwordhelp.addActionListener(myPanel);
		jm1.add(this.passwordhelp);
		this.tips = new JMenuItem("注意事项");
		this.tips.setActionCommand("tip");
		this.tips.addActionListener(myPanel);
		jm1.add(this.tips);
		this.jta = new JTextArea(30, 20);
		this.jta.setBackground(Color.WHITE);
		LogUtil.setLog(this.jta);
		JScrollPane jsp = new JScrollPane(this.jta);
		setLayout(new BorderLayout());
		this.add(myPanel, "North");
		myPanel.setEnabled(true);
		this.add(jsp, "Center");
		setJMenuBar(jmb);
		setTitle("小程序");
		setDefaultCloseOperation(3);
		setBounds(100, 200, 900, 600);
		setVisible(true);
	}

	public static void main(String[] args) {
		try
	    {
			UIManager.put("RootPane.setupButtonVisible", false);
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
	    }
	    catch(Exception e)
	    {
	        //TODO exception
	    }
		new MyJFrame();
	}

	public MyPanel getMyPanel() {
		return myPanel;
	}

	public void setMyPanel(MyPanel myPanel) {
		this.myPanel = myPanel;
	}

	public JTextArea getJta() {
		return jta;
	}

	public void setJta(JTextArea jta) {
		this.jta = jta;
	}

}
