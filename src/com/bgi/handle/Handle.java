package com.bgi.handle;

import java.io.File;
import java.util.List;

import com.bgi.model.ReportModel;

public interface Handle {
	
	public void doFilter(HandleChain chain,List<ReportModel> data);

	public List<ReportModel> readExcel(File file);
	
	public void writeExcel(List<ReportModel> data,String filename);
	
	
}
