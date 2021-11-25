package com.bgi.handle;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.bgi.log.MyLog;
import com.bgi.model.ReportModel;
import com.bgi.util.LogUtil;
import com.bgi.util.POIUtil;

public class HistoryHandle implements Handle {
	private File file;
	private List<ReportModel> list;
	private MyLog log=LogUtil.getLog();
	public HistoryHandle(File file) {
		this.file = file;
	}

	@Override
	public void doFilter(HandleChain chain, List<ReportModel> data) {
		this.list = data;
		log.info("读取汇总表.......");
		readExcel(file);
		log.info("汇总表解析完毕");
		chain.doFilter(data);

	}

	@Override
	public List<ReportModel> readExcel(File file) {
		Workbook workbook = ReportHandle.tryPasswordFile(file);
		if (workbook != null) {
			Sheet sheet=null;
			try {
				sheet = workbook.getSheet("富集");
			} catch (Exception e1) {
			}
			if (sheet == null) {
				sheet = workbook.getSheetAt(0);
			}
			//Map<String, List<ReportModel>> map = list.stream().collect(Collectors.groupingBy(ReportModel::getId));
			int count=0;
			for (Row xrow : sheet) {				
				if(count==0) {
					count++;
					continue;
				}
				String sample = POIUtil.getCellValue(xrow.getCell(2));
				if (StringUtils.isEmpty(sample)) {
					break;
				}
				String id = ReportHandle.getId(sample);
					//List<ReportModel> reportModels = map.get(id);
					List<ReportModel> reportModels = list.stream().filter(d->d.getId().equals(id)).collect(Collectors.toList());
					if (reportModels != null&&reportModels.size()>0) {
						for(ReportModel d:reportModels) {
							if(d.isHistory()) {
								continue;
							}
							d.setNum(reportModels.size()+1);
							/*if(d.getNum()>2) {
								log.info("warning:数据异常，样本"+d.getSample()+"数量多于2");
							}*/
							// W列
							String w = POIUtil.getCellValue(xrow.getCell(22));
							d.setHistoryw(w);
							StringBuilder target = new StringBuilder(d.getTarget());
							d.setTarget(target.append("c").toString());
						}						
						ReportModel model = new ReportModel();
						ReportHandle.setModel(model, xrow);
						model.setTarget("c");
						model.setHistory(true);
						list.add(model);
					}
					count++;
			}
			log.info("文件行数："+count);
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void writeExcel(List<ReportModel> data, String filename) {
		// TODO Auto-generated method stub

	}
}
