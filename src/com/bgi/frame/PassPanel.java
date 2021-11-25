package com.bgi.frame;

import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.bgi.util.FileChooseUtil;

public class PassPanel extends JPanel implements ActionListener, MouseListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton input1;
	JButton input2;
	JButton input3;
	JButton input4;
	JButton input5;
	JButton input6;

	File file_huizong;// 汇总表
	File file_ref;//92种疾病
	File file_report;//
	StringBuilder message;
	private MyJFrame frame;

	public PassPanel(MyJFrame frame) {
		this.frame = frame;
		//this.setLayout(null);
		input1 = new JButton("选择产前数据汇总表");
		input1.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		input1.setSize(20, 30);
		//this.setLayout(new GridLayout(2, 4));
		//input1.setBounds(30, 40,80, 40);
		input1.addMouseListener(this);
		input2 = new JButton("选择参考疾病列表");
		input2.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		// input2.setBounds(230, 40, 80, 40);
		input2.addMouseListener(this);
		
		input3 = new JButton("选择报告单");
		input3.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		// input2.setBounds(230, 40, 80, 40);
		input3.addMouseListener(this);

		input4 = new JButton("运行");
		input4.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.red));
		input4.addMouseListener(this);


		// toggleBtn = new JToggleButton("批处理");
		// toggleBtn.addChangeListener(this);
		//Box horizontal1 = Box.createHorizontalBox();
		//horizontal1.add(new JButton("测试"));
		this.add(input1);
		this.add(input2);
		this.add(input3);
		this.add(input4);
		//this.add(horizontal1);

		// this.add(toggleBtn);
		this.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this.input1) {
			//TODO
			frame.jta.append("任务单：" );
			frame.jta.append("\r\n");
			message = null;
		} else if (e.getSource() == this.input2) {
			//TODO
			message = null;
		} else if (e.getSource() == this.input3) {
			file_report=getFile();
			frame.jta.append("报告单："+ file_report.getAbsolutePath());
			frame.jta.append("\r\n");
		} else if (e.getSource() == this.input4) {
			if (file_report==null) {
				frame.jta.append("错误：未选择报告单！" );
				frame.jta.append("\r\n");
				return;
			}
			frame.jta.append("任务完成" );
			frame.jta.append("\r\n");
			
		}


	}



	private File getFile() {
		try {
			FileDialog fd = new FileDialog(this.frame, "", 0);
			fd.setVisible(true);
			return new File(fd.getDirectory(), fd.getFile());
		} catch (Exception e) {
			return null;
		}

	}

	private List<File> getFiles() {
		try {
			File[] files = FileChooseUtil.getFiles("请选择文件：", JFileChooser.FILES_ONLY);
			return Arrays.asList(files);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

}
