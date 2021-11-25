package com.bgi.rule.impl;
import org.apache.commons.lang3.StringUtils;
import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class Note1Note2Rule implements Rule {
	public final static String abnormal = "异常";
	public final static String abnormal_result = "疑似孕期肿瘤退费，注意寄送血浆";
	
	private int order=2;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=abnormal.equalsIgnoreCase(model.getDataType());
		boolean flag2="退费".equals(model.getNote1());
		boolean flag3=StringUtils.isNotEmpty(model.getNote2()) && model.getNote2().contains("疑似存在孕期肿瘤");
		if (flag1&&flag2&&flag3) {
			model.setResult(abnormal_result);
		}

	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}
