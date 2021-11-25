package com.bgi.handle;

import java.util.Iterator;
import java.util.List;

import com.bgi.model.ReportModel;



public class ChargeHandleChain implements HandleChain {

	private  List<Handle> filters;

	
	private Iterator<Handle> iterator;

	public ChargeHandleChain(List<Handle> filters) {
		this.filters = filters;
	}

	@Override
	public void doFilter(List<ReportModel> data) {
		if (this.iterator == null) {
			this.iterator = this.filters.iterator();
		}

		if (this.iterator.hasNext()) {
			Handle nextFilter = this.iterator.next();
			nextFilter.doFilter(this,data);
		}

	}

}
