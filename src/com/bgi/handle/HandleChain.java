package com.bgi.handle;

import java.util.List;

import com.bgi.model.ReportModel;

public interface HandleChain {

	 public void doFilter(List<ReportModel> data);

}
