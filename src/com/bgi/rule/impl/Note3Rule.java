package com.bgi.rule.impl;

import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class Note3Rule implements Rule {
	public final static String normal = "正常";
	public final static String normal_result = "报+说明";
	
	private int order=8;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=normal.equalsIgnoreCase(model.getDataType());
		boolean flag2=model.getNote3().contains("chr") || model.getNote3().contains("染色体数目偏多")
				|| model.getNote3().contains("染色体数目偏少") || model.getNote3().equals("XYY高危(+++)")
				|| model.getNote3().equals("XXX高危(+++)") || model.getNote3().equals("XXY高危(+++)")
				|| model.getNote3().equals("XO高危(+++)") || model.getNote3().equals("X(偏少)-M")
				|| model.getNote3().equals("X(偏多)-M");
		if(flag1&&flag2) {
			model.setResult(normal_result);
		}

	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}
