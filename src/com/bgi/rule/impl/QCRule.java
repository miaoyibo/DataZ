package com.bgi.rule.impl;
import org.apache.commons.lang3.StringUtils;
import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class QCRule implements Rule {
	public final static String abnormal = "异常";
	public final static String abnormal_result = "重抽血";
	
	private int order=3;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=abnormal.equalsIgnoreCase(model.getDataType());
		boolean flag2=StringUtils.isNotEmpty(model.getQc()) && (model.getQc().equals("不通过-Dim")
				|| model.getQc().equals("不通过-多条染色体离群") || model.getQc().equals("不通过-胎儿浓度"));
		if(flag1&&flag2) {
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
