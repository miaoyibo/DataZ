package com.bgi.handle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.bgi.log.MyLog;
import com.bgi.model.ReportModel;
import com.bgi.util.LogUtil;
import com.bgi.util.POIUtil;

public class RefDiseaseHandle implements Handle {
	private File file;		
	private MyLog log=LogUtil.getLog();
	public RefDiseaseHandle(File file) {
		this.file = file;
	}

	@Override
	public void doFilter(HandleChain chain,List<ReportModel> data) {
		log.info("读取疾病参考表.....");
		List<ReportModel> list=readExcel(file);
		if(list!=null&&list.size()>0) {
			for(ReportModel m : data) {
				Optional<ReportModel> optional = list.stream().filter(r->r.getProductName().equals(m.getProductName())&&m.getDisease().contains(r.getDisease())).findAny();
				if(optional.isPresent()) {
					m.setRef(true);
				}
			}
		}
		log.info("参考表读取完毕");
		chain.doFilter(data);
	}
	@Override
	public List<ReportModel> readExcel(File file) {
		Workbook workbook = ReportHandle.tryPasswordFile(file);
		List<ReportModel> result = new ArrayList<>();
		if (workbook != null) {	
			Sheet sheet=null;
			try {
				sheet = workbook.getSheet("疾病列表");
			} catch (Exception e1) {
			}
			if (sheet == null) {
				sheet = workbook.getSheetAt(0);
			}
			int count=0;
			for (Row xrow : sheet) {
				count++;
				String disease = POIUtil.getCellValue(xrow.getCell(1));
				if (StringUtils.isEmpty(disease)||count==1) {
					continue;
				}
				ReportModel model = new ReportModel();
				model.setDisease(disease);
				model.setProductName(POIUtil.getCellValue(xrow.getCell(2)));
				result.add(model);
				
			}
			log.info("文件行数："+count);
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}

	@Override
	public void writeExcel(List<ReportModel> data, String filename) {
		// TODO Auto-generated method stub

	}

}
