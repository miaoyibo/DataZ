package com.bgi.frame;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import com.bgi.handle.ChargeHandleChain;
import com.bgi.handle.Handle;
import com.bgi.handle.HandleChain;
import com.bgi.handle.HistoryHandle;
import com.bgi.handle.RefDiseaseHandle;
import com.bgi.handle.ReportHandle;
import com.bgi.util.FileChooseUtil;
import com.bgi.util.LogUtil;

public class MyPanel extends JPanel implements ActionListener, MouseListener, ChangeListener {
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
	boolean isrunning=false;

	public MyPanel(MyJFrame frame) {
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
		
		this.add(input3);
		this.add(input1);
		this.add(input2);
		this.add(input4);

		// this.add(toggleBtn);
		this.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("help")) {
			JOptionPane.showMessageDialog(null, "如果表格设有密码，请在表格同一目录下，新建txt文档，输入密码，并命名为password，即 password.txt");
		}else {
			JOptionPane.showMessageDialog(null, "1。文件中间不能出现空行或关键列为空的情况，比如sample,否则空行后面内容会被忽略");
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(isrunning) {
			JOptionPane.showMessageDialog(null, "当前任务未完成！请稍后");
			return;
		}
		if (e.getSource() == this.input1) {
			file_huizong=getFile();
			if(file_huizong!=null) {
				frame.jta.append("汇总表：" + file_huizong.getAbsolutePath());
				frame.jta.append("\r\n");
			}else {
				frame.jta.append("文件未选择" );
				frame.jta.append("\r\n");
			}
			

		} else if (e.getSource() == this.input2) {
			file_ref=getFile();
			if(file_ref!=null) {
				frame.jta.append("疾病参考表：" + file_ref.getAbsolutePath());
				frame.jta.append("\r\n");
			}else {
				frame.jta.append("文件未选择" );
				frame.jta.append("\r\n");
			}

		} else if (e.getSource() == this.input3) {
			file_report=getFile();
			if(file_report!=null) {
				frame.jta.append("报告单：" + file_report.getAbsolutePath());
				frame.jta.append("\r\n");
			}else {
				frame.jta.append("文件未选择" );
				frame.jta.append("\r\n");
			}
		} else if (e.getSource() == this.input4) {
			if (file_report==null) {
				frame.jta.append("错误：未选择报告单！" );
				frame.jta.append("\r\n");
				return;
			}
			isrunning=true;
			Thread thread=new Thread(new Work());
			thread.start();
			
		}


	}



	private void runWork() {
		
		List<Handle> filters=new ArrayList<>();
		Handle handle1=new ReportHandle(file_report);
		Handle handle2=new HistoryHandle(file_huizong);
		Handle handle3=new RefDiseaseHandle(file_ref);
		filters.add(handle1);
		filters.add(handle2);
		filters.add(handle3);
		HandleChain chain=new ChargeHandleChain(filters);
		chain.doFilter(null);
		
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
	class Work implements Runnable{

		@Override
		public void run() {
			
			
			try {
				runWork();
			} catch (Exception e) {
				e.printStackTrace();
				frame.jta.append("任务失败:"+ e);
				frame.jta.append("\r\n");
			}finally {
				isrunning=false;
			}
			
		}
		
	}

}
