package com.bgi.util;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
 
import javax.swing.*;
 
public class MyFileChoose extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JTextField textField;
	private JPanel panel = new JPanel();
	private JFileChooser fileChooser = new JFileChooser();
	
	public MyFileChoose() {
		setTitle("ѡ����");
		setBounds(400, 400, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JLabel label = new JLabel();
		label.setText("�ļ���");
		panel.add(label);
		
		textField = new JTextField();
		textField.setColumns(20);
		panel.add(textField);
		
		final JButton button = new JButton("�ϴ�");
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				int i = fileChooser.showOpenDialog(getContentPane());// ��ʾ�ļ�ѡ��Ի���
				
				// �ж��û��������Ƿ�Ϊ���򿪡���ť
				if (i == JFileChooser.APPROVE_OPTION) {
					
					File selectedFile = fileChooser.getSelectedFile();// ���ѡ�е��ļ�����
					System.out.println(selectedFile.getName());
					textField.setText(selectedFile.getName());// ��ʾѡ���ļ�������
					dispose();
				}
			}
		});
		panel.add(button);
		
		add(panel, BorderLayout.NORTH);
		setVisible(true);
	}
	
 
}