package com.bgi.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bgi.log.MyLog;
import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;
import com.bgi.rule.RuleConstant;
import com.bgi.util.LogUtil;
import com.bgi.util.POIUtil;

public class ReportHandle implements Handle {

	public final static String normal = "正常";
	public final static String abnormal = "异常";
	
	private List<Rule> rules=new ArrayList<>();
/*	{
		rules.add(new Note1Rule());
		rules.add(new Note1Note2Rule());
		rules.add(new QCRule());
		rules.add(new UQCRule());
		rules.add(new RepeatRule());
		rules.add(new KlmRule());
		rules.add(new KlmoRule());
		rules.add(new Note3Rule());
		rules.add(new PRule());
		rules.add(new PdRule());
		rules.add(new Pd2Rule());
		rules.add(new KlmnRule());
	}
*/
	private static String[] headers = { "样例编号", "审核结果", "Test(chr21)", "Test(chr18)", "Test(chr13）", "Test(性染色体)",
			"Note2", "Note3", "Note1", "Test(常染色体)", "Test区带(重复/缺失)", "QC", "疾病名称", "产品类名称" };
	private static String[] headers2 = { "审核日期", "送检医院", "样例编号", "抽血日期", "姓名", "年龄", "孕周", "产品类名称", "%chrY", "FMD%",
			"Test(chr21)", "Test(chr18)", "Test(chr13）", "Test(性染色体)", "Note2", "Note3", "Note1", "审核结果", "Test(常染色体)",
			"Test区带(重复/缺失)", "Test位点(重复/缺失)", "QC", "备注", "审核时间", "数据类型", "子文库号", "状态", "异常级别", "异常提示", "出生年月", "批次号",
			"FMD", "胎型", "T-score(chr21)", "T-score(chr18)", "T-score(chr13)", "Z-score_check(chr21)",
			"Z-score_check(chr18)", "Z-score_check(chr13)", "风险指数（chr21)", "风险指数（chr18)", "风险百分数(chr13)", "检测者", "审核人",
			"Sample id" ,"预计完成时间","预计出结果时间","疾病名称"};
	private static String sheet1_name = "审核结果";
	private static String sheet2_name = "Repeat";

	private static String output_name="  天津产前报告审核异常情况记录表.xlsx";
	
	private String output_filepath;
	
	private File file;
	
	private MyLog log=LogUtil.getLog();
	
	public ReportHandle(File file) {
		this.file = file;
	}

	@Override
	public void doFilter(HandleChain chain, List<ReportModel> data) {
		log.info("读取报告单...");
		data = readExcel(file);
		log.info("报告单读取完毕");
		if(data.size()<=0) {
			log.info("报告单为空，请确认文件格式或密码是否正确");
			return;
		}
		chain.doFilter(data);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date1 = new Date();
		String currentTime = dateFormat.format(date1);
		log.info("生成结果....");
		data.stream().filter(d->!d.isHistory()).forEach(d->charge2(d));
		writeExcel(data, currentTime+output_name);
		log.info("任务完成，结果："+output_filepath);
	}

	@Override
	public List<ReportModel> readExcel(File file) {
		Workbook workbook = tryPasswordFile(file);
		List<ReportModel> result = new ArrayList<>();
		List<String> ids=new ArrayList<>();
		List<String> repeatIds=new ArrayList<>();
		if (workbook != null) {
			Sheet sheet = workbook.getSheetAt(0);
			int count=0;
			for (Row xrow : sheet) {
				if(count==0) {
					count++;
					continue;
				}
				String sample = POIUtil.getCellValue(xrow.getCell(2));
				if(StringUtils.isEmpty(sample)) {
					break;
				}
				ReportModel model = new ReportModel();
				setModel(model, xrow);
				StringBuilder target = new StringBuilder();
				if (normal.equals(model.getDataType()) && model.getTest_chr21().contains("未检出")
						&& model.getTest_chr18().contains("未检出") && model.getTest_chr13().contains("未检出")
						&& model.getTest_sex().contains("未检出") && StringUtils.isEmpty(model.getNote3())
						&& StringUtils.isEmpty(model.getNote2()) && StringUtils.isEmpty(model.getNote1())
						&& StringUtils.isEmpty(model.getTest_common()) && StringUtils.isEmpty(model.getTest_area())) {
					target.append("a");
				} else {
					target.append("b");
				}
				
				model.setTarget(target.toString());
				String id=model.getId();
				if(ids.contains(id)) {
					repeatIds.add(id);
				}else {
					ids.add(id);
				}
				//charge(model);
				result.add(model);
				count++;
			}
			log.info("文件行数："+count);
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result.stream().filter(d->repeatIds.contains(d.getId())).forEach(c->c.setTarget(c.getTarget()+"c"));
		return result;
	}

	@Override
	public void writeExcel(List<ReportModel> data, String filename) {
		List<ReportModel> resultList = data.stream().filter(d -> d.getTarget().contains("b")).sorted(Comparator.comparing(ReportModel::getSample))
				.collect(Collectors.toList());
		List<ReportModel> repeatList = data.stream().filter(d -> d.getTarget().contains("c")).sorted(Comparator.comparing(ReportModel::getSample))
				.collect(Collectors.toList());
		Workbook workbook = new XSSFWorkbook();
		File file = new File(filename);
		if(file.exists()) {
			file=new File(System.currentTimeMillis()+"-"+filename);
		}
		if (resultList != null && resultList.size() > 0) {
			writeSheet1(resultList, workbook);
		}
		if (repeatList != null && repeatList.size() > 0) {
			writeSheet2(repeatList, workbook);
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			workbook.write(out);
			out.close();
			workbook.close();
			output_filepath=file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			output_filepath="error";
		}
		
	}

	private void writeSheet2(List<ReportModel> repeatList, Workbook workbook) {
		Sheet sheet = workbook.createSheet(sheet2_name);
		CellStyle headerStyle = getHeaderStyle2(workbook);
		writeHeader(sheet, headers2, headerStyle);
		CellStyle rowStyle = getRowStyle(workbook,false);
		int rownum = 1;
		for (ReportModel m : repeatList) {
			Row row = sheet.createRow(rownum);
			setCellValue(0, m.getReviewDate(), row, rowStyle);
			setCellValue(1, m.getHospital(), row, rowStyle);
			setCellValue(2, m.getSample(), row, rowStyle);
			setCellValue(3, m.getBloodDate(), row, rowStyle);
			setCellValue(4, m.getPatientName(), row, rowStyle);
			setCellValue(5, m.getAge(), row, rowStyle);
			setCellValue(6, m.getPregmentTime(), row, rowStyle);
			setCellValue(7, m.getProductName(), row, rowStyle);
			setCellValue(8, m.getChrY(), row, rowStyle);
			setCellValue(9, m.getFmd_(), row, rowStyle);
			setCellValue(10, m.getTest_chr21(), row, rowStyle);
			setCellValue(11, m.getTest_chr18(), row, rowStyle);
			setCellValue(12, m.getTest_chr13(), row, rowStyle);
			setCellValue(13, m.getTest_sex(), row, rowStyle);
			setCellValue(14, m.getNote2(), row, rowStyle);
			setCellValue(15, m.getNote3(), row, rowStyle);
			setCellValue(16, m.getNote1(), row, rowStyle);
			setCellValue(17, m.getResult(), row, rowStyle);
			setCellValue(18, m.getTest_common(), row, rowStyle);
			setCellValue(19, m.getTest_area(), row, rowStyle);
			setCellValue(20, m.getTest_point(), row, rowStyle);
			setCellValue(21, m.getQc(), row, rowStyle);
			setCellValue(22, m.getRemark(), row, rowStyle);
			setCellValue(23, m.getReviewDate(), row, rowStyle);
			setCellValue(24, m.getDataType(), row, rowStyle);
			setCellValue(25, m.getWenku(), row, rowStyle);
			setCellValue(26, m.getStatus(), row, rowStyle);
			setCellValue(27, m.getAbnormal_level(), row, rowStyle);
			setCellValue(28, m.getAbnormal_tip(), row, rowStyle);
			setCellValue(29, m.getBirthday(), row, rowStyle);
			setCellValue(30, m.getBathno(), row, rowStyle);
			setCellValue(31, m.getFmd(), row, rowStyle);
			setCellValue(32, m.getBabyType(), row, rowStyle);
			setCellValue(33, m.getT_score21(), row, rowStyle);
			setCellValue(34, m.getT_score18(), row, rowStyle);
			setCellValue(35, m.getT_score13(), row, rowStyle);
			setCellValue(36, m.getT_score_check21(), row, rowStyle);
			setCellValue(37, m.getT_score_check18(), row, rowStyle);
			setCellValue(38, m.getT_score_check13(), row, rowStyle);
			setCellValue(39, m.getRisk21(), row, rowStyle);
			setCellValue(40, m.getRisk18(), row, rowStyle);
			setCellValue(41, m.getRisk13(), row, rowStyle);
			setCellValue(42, m.getChecker(), row, rowStyle);
			setCellValue(43, m.getReviewer(), row, rowStyle);
			setCellValue(44, m.getSampleId(), row, rowStyle);
			setCellValue(45, m.getFinishTime(), row, rowStyle);
			setCellValue(46, m.getResultTime(), row, rowStyle);
			setCellValue(47, m.getDisease(), row, rowStyle);
			rownum++;
		}
		for (int i = 0; i < headers2.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}
	}
	private CellStyle getHeaderStyle2(Workbook workbook) {
		CellStyle headerStyle = workbook.createCellStyle();
		//headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		return headerStyle;
	}
	private void writeSheet1(List<ReportModel> resultList, Workbook workbook) {
		//List<ReportModel> data=resultList.stream().sorted(Comparator.comparing(ReportModel::getSample)).collect(Collectors.toList());
		Sheet sheet = workbook.createSheet(sheet1_name);
		CellStyle headerStyle = getHeaderStyle(workbook);
		writeHeader(sheet, headers, headerStyle);
		CellStyle rowStyle = getRowStyle(workbook,true);
		CellStyle rowStyle2 = getRowStyle(workbook,false);
		int rownum = 1;
		for (ReportModel m : resultList) {
			Row row = sheet.createRow(rownum);
			setCellValue(0, m.getSample(), row, rowStyle2);
			setCellValue(1, m.getResult(), row, rowStyle);
			setCellValue(2, m.getTest_chr21(), row, rowStyle2);
			setCellValue(3, m.getTest_chr18(), row, rowStyle2);
			setCellValue(4, m.getTest_chr13(), row, rowStyle2);
			setCellValue(5, m.getTest_sex(), row, rowStyle2);
			setCellValue(6, m.getNote2(), row, rowStyle2);
			setCellValue(7, m.getNote3(), row, rowStyle2);
			setCellValue(8, m.getNote1(), row, rowStyle2);
			setCellValue(9, m.getTest_common(), row, rowStyle2);
			setCellValue(10, m.getTest_area(), row, rowStyle2);
			setCellValue(11, m.getQc(), row, rowStyle2);
			setCellValue(12, m.getDisease(), row, rowStyle2);
			setCellValue(13, m.getProductName(), row, rowStyle2);
			rownum++;
		}
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 12 / 10);
		}
	}

	private void setCellValue(int i, String value, Row row, CellStyle cellStyle) {
		Cell cell = row.createCell(i);
		cell.setCellValue(value);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}

	}

	private CellStyle getRowStyle(Workbook workbook,boolean color) {
		CellStyle rowStyle = workbook.createCellStyle();
		if(color) {
			rowStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
			rowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}		
		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 10);
		rowStyle.setFont(font);
		return rowStyle;
	}

	private CellStyle getHeaderStyle(Workbook workbook) {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		return headerStyle;
	}

	private void writeHeader(Sheet sheet, String[] headers, CellStyle headerStyle) {
		Row row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			if (headerStyle != null) {
				cell.setCellStyle(headerStyle);
			}
		}

	}
	private void charge(ReportModel model) {
		for(Rule r:rules) {
			r.filter(model);
		}
	}
	
	private void charge2(ReportModel model) {
		String dataType = model.getDataType();
		if (abnormal.equalsIgnoreCase(dataType)) {
			if ("退费".equals(model.getNote1())) {
				if (StringUtils.isNotEmpty(model.getNote2()) && model.getNote2().contains("疑似存在孕期肿瘤")) {
					model.setResult(RuleConstant.abnormal_result1);
				} else {
					model.setResult(RuleConstant.abnormal_result2);
				}
			} else if (StringUtils.isNotEmpty(model.getQc()) && (model.getQc().equals("不通过-Dim")
					|| model.getQc().equals("不通过-多条染色体离群") || model.getQc().equals("不通过-胎儿浓度"))) {
				model.setResult(RuleConstant.abnormal_result3);
			}else if(model.getNum()<2&&!(StringUtils.isNotEmpty(model.getQc()) && (model.getQc().equals("不通过-Dim")
					|| model.getQc().equals("不通过-多条染色体离群") || model.getQc().equals("不通过-胎儿浓度")))) {
				model.setResult(RuleConstant.abnormal_result4);
			}else if(model.getNum()==2) {
				if ("重建库".equals(model.getHistoryw())) {
					model.setResult("重抽血");
				} else if ("重抽血".equals(model.getHistoryw())) {
					model.setResult("结束");
				} else {
					model.setResult("待确认");
				}
			}
		} else if (normal.equals(model.getDataType())) {
			if (!(model.getTest_chr21().contains("未检出") && model.getTest_chr18().contains("未检出")
					&& model.getTest_chr13().contains("未检出"))) {
				if (StringUtils.isNotEmpty(model.getNote2())) {
					model.setResult(RuleConstant.normal_result1);
				} else {
					model.setResult(RuleConstant.normal_result2);
				}
			} else if (model.getNote3().contains("chr") || model.getNote3().contains("染色体数目偏多")
					|| model.getNote3().contains("染色体数目偏少") || model.getNote3().equals("XYY高危(+++)")
					|| model.getNote3().equals("XXX高危(+++)") || model.getNote3().equals("XXY高危(+++)")
					|| model.getNote3().equals("XO高危(+++)") || model.getNote3().equals("X(偏少)-M")
					|| model.getNote3().equals("X(偏多)-M")) {
				model.setResult(RuleConstant.normal_result3);
			} else if (model.getNote3().contains("del") || model.getNote3().contains("dup")) {
				if(StringUtils.isEmpty(model.getDisease())) {
					model.setResult(RuleConstant.normal_result4);
				}else {
					if(model.isRef()) {
						model.setResult(RuleConstant.normal_result3);
					}else {
						model.setResult(RuleConstant.normal_result4);
					}
				}
			} else if (model.getTest_chr21().contains("未检出") && model.getTest_chr18().contains("未检出")
					&& model.getTest_chr13().contains("未检出") && model.getTest_sex().contains("未检出")
					&& (StringUtils.isNotEmpty(model.getNote2()) || StringUtils.isNotEmpty(model.getNote1())
					|| StringUtils.isNotEmpty(model.getTest_common()) || StringUtils.isNotEmpty(model.getTest_area()))
					&& StringUtils.isEmpty(model.getNote3())) {
				model.setResult(RuleConstant.normal_result4);
			}
		}

	}

	public static void setModel(ReportModel model, Row xrow) {
		int count=0;
		model.setReviewDate(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setHospital(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setSample(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setBloodDate(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setPatientName(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setAge(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setPregmentTime(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setProductName(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setChrY(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setFmd_(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_chr21(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_chr18(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_chr13(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_sex(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setNote2(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setNote3(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setNote1(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setResult(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_common(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_area(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setTest_point(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setQc(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setRemark(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setReviewDate(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setDataType(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setWenku(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setStatus(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setAbnormal_level(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setAbnormal_tip(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setBirthday(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setBathno(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setFmd(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setBabyType(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setT_score21(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setT_score18(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setT_score13(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setT_score_check21(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setT_score_check18(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setT_score_check13(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setRisk21(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setRisk18(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setRisk13(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setChecker(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setReviewer(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setSampleId(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setFinishTime(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setResultTime(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setDisease(POIUtil.getCellValue(xrow.getCell(count++)));
		model.setId(getId(model.getSample()));
		model.setNum(1);

	}

	public static String getId(String sample) {
		if (sample == null || sample.length() < 11) {
			return sample;
		}
		String head = sample.substring(0, 2);
		String tail = sample.substring(3, 11);
		return head + tail;
	}

	public static String getOutput_name() {
		return output_name;
	}

	public static void setOutput_name(String output_name) {
		ReportHandle.output_name = output_name;
	}

	public String getOutput_filepath() {
		return output_filepath;
	}

	public void setOutput_filepath(String output_filepath) {
		this.output_filepath = output_filepath;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	public static Workbook tryPasswordFile(File file) {
		Workbook workbook = null;
		try {
			File parentFile = file.getParentFile();
			File passFile = new File(parentFile, "password.txt");
			if (passFile.exists()) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(passFile));
					String password = reader.readLine();
					workbook = POIUtil.getEncrypWorkbook(file, password);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			} else {
				workbook = POIUtil.getWorkBook(file);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return workbook;
	}
}
