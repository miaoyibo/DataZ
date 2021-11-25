package com.bgi.rule.impl;
import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class UQCRule implements Rule {
	public final static String abnormal_result= "重建库";
	public final static String abnormal = "异常";
	
	private int order=4;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=abnormal.equalsIgnoreCase(model.getDataType());
		boolean flag2=!("不通过-Dim".equals(model.getQc())
				||"不通过-多条染色体离群".equals(model.getQc()) || "不通过-胎儿浓度".equals(model.getQc()));
		boolean flag3=model.getNum()<2;
		if(flag1&&flag2&&flag3) {
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
