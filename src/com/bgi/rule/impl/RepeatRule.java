package com.bgi.rule.impl;
import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;
import com.bgi.rule.RuleConstant;

public class RepeatRule implements Rule {
	public final static String abnormal = "异常";
	public final static String abnormal_result= "重建库";
	
	private int order=5;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=RuleConstant.abnormal.equalsIgnoreCase(model.getDataType());
		boolean flag4=model.getNum()>1;
		if(flag4&&flag1) {
			String w=model.getHistoryw();
			if ("重建库".equals(w)) {
				model.setResult("重抽血");
			} else if ("重抽血".equals(w)) {
				model.setResult("结束");
			} else {
				model.setResult("待确认");
			}
		}

	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}
