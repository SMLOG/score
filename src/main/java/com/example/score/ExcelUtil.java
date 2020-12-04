package com.example.score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public final class ExcelUtil {


	public static void extractValueFromExcelToDB(JdbcTemplate jdbc,String stockCode, final Workbook workbook)
			throws Exception {
		List<? extends Name> names = workbook.getAllNames();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

	     Map<String,Object> rowMap = new LinkedHashMap<String,Object>();
	     rowMap.put("code", stockCode);
	     for(Name name:names)
	        if (name != null) {
	            
	         try{
	            CellReference cellReference = new CellReference(name.getRefersToFormula());
	            Sheet sheet = workbook.getSheet(cellReference.getSheetName());
	            Row row = sheet.getRow(cellReference.getRow());
	            Cell cell = row.getCell(cellReference.getCol());
	            double val = evaluator.evaluate(cell).getNumberValue();
				System.out.println(name.getNameName()+":"+val);
	            rowMap.put(name.getNameName(), val);   
	            }catch(Exception e) {
	            	continue;
	            }
	            
	        }
        
	     DbUtil.saveObj(jdbc, "excel_gz", rowMap);
	}
	public static Workbook generateExcel(String stockCode,JdbcTemplate jdbcTemplate,OutputStream out) throws Exception {


		String[][] zcfzb = new String[][] { 
				{ "MONETARYFUND", "&ensp;&ensp;&ensp;&ensp;货币资金" },
				{ "SETTLEMENTPROVISION", "&ensp;&ensp;&ensp;&ensp;结算备付金" },
				{ "LENDFUND", "&ensp;&ensp;&ensp;&ensp;拆出资金" },
				{ "FVALUEFASSET", "&ensp;&ensp;&ensp;&ensp;以公允价值计量且其变动计入当期损益的金融资产", },
				{ "TRADEFASSET", "&ensp;&ensp;&ensp;&ensp;其中:交易性金融资产" },
				{ "DEFINEFVALUEFASSET",
						"&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;指定为以公允价值计量且其变动计入当期损益的金融资产", },
				{ "ACCOUNTBILLREC", "&ensp;&ensp;&ensp;&ensp;应收票据及应收账款" },
				{ "BILLREC", "&ensp;&ensp;&ensp;&ensp;其中:应收票据" },
				{ "ACCOUNTREC", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;应收账款", },
				{ "ADVANCEPAY", "&ensp;&ensp;&ensp;&ensp;预付款项" }, { "PREMIUMREC", "&ensp;&ensp;&ensp;&ensp;应收保费" },
				{ "RIREC", "&ensp;&ensp;&ensp;&ensp;应收分保账款" },
				{ "RICONTACTRESERVEREC", "&ensp;&ensp;&ensp;&ensp;应收分保合同准备金" },
				{ "TOTAL", "&ensp;&ensp;&ensp;&ensp;其他应收款合计" }, { "INTERESTREC", "&ensp;&ensp;&ensp;&ensp;其中:应收利息" },
				{ "DIVIDENDREC", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;应收股利", },
				{ "OTHERREC", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;其他应收款", },
				{ "EXPORTREBATEREC", "&ensp;&ensp;&ensp;&ensp;应收出口退税" },
				{ "SUBSIDYREC", "&ensp;&ensp;&ensp;&ensp;应收补贴款" }, { "INTERNALREC", "&ensp;&ensp;&ensp;&ensp;内部应收款" },
				{ "BUYSELLBACKFASSET", "&ensp;&ensp;&ensp;&ensp;买入返售金融资产" },
				{ "INVENTORY", "&ensp;&ensp;&ensp;&ensp;存货" },
				{ "CLHELDSALEASS", "&ensp;&ensp;&ensp;&ensp;划分为持有待售的资产" },
				{ "NONLASSETONEYEAR", "&ensp;&ensp;&ensp;&ensp;一年内到期的非流动资产" },
				{ "OTHERLASSET", "&ensp;&ensp;&ensp;&ensp;其他流动资产" }, { "SUMLASSET", "流动资产合计" },
				{ "LOANADVANCES", "&ensp;&ensp;&ensp;&ensp;发放委托贷款及垫款" },
				{ "SALEABLEFASSET", "&ensp;&ensp;&ensp;&ensp;可供出售金融资产" },
				{ "HELDMATURITYINV", "&ensp;&ensp;&ensp;&ensp;持有至到期投资" }, { "LTREC", "&ensp;&ensp;&ensp;&ensp;长期应收款" },
				{ "LTEQUITYINV", "&ensp;&ensp;&ensp;&ensp;长期股权投资" },
				{ "ESTATEINVEST", "&ensp;&ensp;&ensp;&ensp;投资性房地产" }, { "FIXEDASSET", "&ensp;&ensp;&ensp;&ensp;固定资产" },
				{ "CONSTRUCTIONPROGRESS", "&ensp;&ensp;&ensp;&ensp;在建工程" },
				{ "CONSTRUCTIONMATERIAL", "&ensp;&ensp;&ensp;&ensp;工程物资" },
				{ "LIQUIDATEFIXEDASSET", "&ensp;&ensp;&ensp;&ensp;固定资产清理" },
				{ "PRODUCTBIOLOGYASSET", "&ensp;&ensp;&ensp;&ensp;生产性生物资产" },
				{ "OILGASASSET", "&ensp;&ensp;&ensp;&ensp;油气资产" },
				{ "INTANGIBLEASSET", "&ensp;&ensp;&ensp;&ensp;无形资产" }, { "DEVELOPEXP", "&ensp;&ensp;&ensp;&ensp;开发支出" },
				{ "GOODWILL", "&ensp;&ensp;&ensp;&ensp;商誉" }, { "LTDEFERASSET", "&ensp;&ensp;&ensp;&ensp;长期待摊费用" },
				{ "DEFERINCOMETAXASSET", "&ensp;&ensp;&ensp;&ensp;递延所得税资产" },
				{ "OTHERNONLASSET", "&ensp;&ensp;&ensp;&ensp;其他非流动资产" }, { "SUMNONLASSET", "非流动资产合计" },
				{ "SUMASSET", "资产总计" }, { "STBORROW", "&ensp;&ensp;&ensp;&ensp;短期借款" },
				{ "BORROWFROMCBANK", "&ensp;&ensp;&ensp;&ensp;向中央银行借款" },
				{ "DEPOSIT", "&ensp;&ensp;&ensp;&ensp;吸收存款及同业存放" }, { "BORROWFUND", "&ensp;&ensp;&ensp;&ensp;拆入资金" },
				{ "FVALUEFLIAB", "&ensp;&ensp;&ensp;&ensp;以公允价值计量且其变动计入当期损益的金融负债", },
				{ "TRADEFLIAB", "&ensp;&ensp;&ensp;&ensp;其中:交易性金融负债" },
				{ "DEFINEFVALUEFLIAB",
						"&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;指定以公允价值计量且其变动计入当期损益的金融负债", },
				{ "ACCOUNTBILLPAY", "&ensp;&ensp;&ensp;&ensp;应付票据及应付账款" },
				{ "BILLPAY", "&ensp;&ensp;&ensp;&ensp;其中:应付票据" },
				{ "ACCOUNTPAY", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;应付账款", },
				{ "ADVANCERECEIVE", "&ensp;&ensp;&ensp;&ensp;预收款项" },
				{ "SELLBUYBACKFASSET", "&ensp;&ensp;&ensp;&ensp;卖出回购金融资产款" },
				{ "COMMPAY", "&ensp;&ensp;&ensp;&ensp;应付手续费及佣金" }, { "SALARYPAY", "&ensp;&ensp;&ensp;&ensp;应付职工薪酬" },
				{ "TAXPAY", "&ensp;&ensp;&ensp;&ensp;应交税费" }, { "TOTAL", "&ensp;&ensp;&ensp;&ensp;其他应付款合计" },
				{ "INTERESTPAY", "&ensp;&ensp;&ensp;&ensp;其中:应付利息" },
				{ "DIVIDENDPAY", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;应付股利", },
				{ "OTHERPAY", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;其他应付款", },
				{ "RIPAY", "&ensp;&ensp;&ensp;&ensp;应付分保账款" }, { "INTERNALPAY", "&ensp;&ensp;&ensp;&ensp;内部应付款" },
				{ "ANTICIPATELLIAB", "&ensp;&ensp;&ensp;&ensp;预计流动负债" },
				{ "CONTACTRESERVE", "&ensp;&ensp;&ensp;&ensp;保险合同准备金" },
				{ "AGENTTRADESECURITY", "&ensp;&ensp;&ensp;&ensp;代理买卖证券款" },
				{ "AGENTUWSECURITY", "&ensp;&ensp;&ensp;&ensp;代理承销证券款" },
				{ "DEFERINCOMEONEYEAR", "&ensp;&ensp;&ensp;&ensp;一年内的递延收益" },
				{ "STBONDREC", "&ensp;&ensp;&ensp;&ensp;应付短期债券" },
				{ "CLHELDSALELIAB", "&ensp;&ensp;&ensp;&ensp;划分为持有待售的负债" },
				{ "NONLLIABONEYEAR", "&ensp;&ensp;&ensp;&ensp;一年内到期的非流动负债" },
				{ "OTHERLLIAB", "&ensp;&ensp;&ensp;&ensp;其他流动负债" }, { "SUMLLIAB", "流动负债合计" },
				{ "LTBORROW", "&ensp;&ensp;&ensp;&ensp;长期借款" }, { "BONDPAY", "&ensp;&ensp;&ensp;&ensp;应付债券" },
				{ "PREFERSTOCBOND", "&ensp;&ensp;&ensp;&ensp;其中:优先股" },
				{ "SUSTAINBOND", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;永续债", },
				{ "LTACCOUNTPAY", "&ensp;&ensp;&ensp;&ensp;长期应付款" },
				{ "LTSALARYPAY", "&ensp;&ensp;&ensp;&ensp;长期应付职工薪酬" },
				{ "SPECIALPAY", "&ensp;&ensp;&ensp;&ensp;专项应付款" }, { "ANTICIPATELIAB", "&ensp;&ensp;&ensp;&ensp;预计负债" },
				{ "DEFERINCOME", "&ensp;&ensp;&ensp;&ensp;递延收益" },
				{ "DEFERINCOMETAXLIAB", "&ensp;&ensp;&ensp;&ensp;递延所得税负债" },
				{ "OTHERNONLLIAB", "&ensp;&ensp;&ensp;&ensp;其他非流动负债" }, { "SUMNONLLIAB", "非流动负债合计" },
				{ "SUMLIAB", "负债合计" }, { "SHARECAPITAL", "&ensp;&ensp;&ensp;&ensp;实收资本（或股本）" },
				{ "OTHEREQUITY", "&ensp;&ensp;&ensp;&ensp;其他权益工具" },
				{ "PREFERREDSTOCK", "&ensp;&ensp;&ensp;&ensp;其中:优先股" },
				{ "SUSTAINABLEDEBT", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;永续债", },
				{ "OTHEREQUITYOTHER", "&ensp;&ensp;&ensp;&ensp;其他权益工具" },
				{ "CAPITALRESERVE", "&ensp;&ensp;&ensp;&ensp;资本公积" },
				{ "INVENTORYSHARE", "&ensp;&ensp;&ensp;&ensp;库存股" },
				{ "SPECIALRESERVE", "&ensp;&ensp;&ensp;&ensp;专项储备" },
				{ "SURPLUSRESERVE", "&ensp;&ensp;&ensp;&ensp;盈余公积" },
				{ "GENERALRISKPREPARE", "&ensp;&ensp;&ensp;&ensp;一般风险准备" },
				{ "UNCONFIRMINVLOSS", "&ensp;&ensp;&ensp;&ensp;未确定的投资损失" },
				{ "RETAINEDEARNING", "&ensp;&ensp;&ensp;&ensp;未分配利润" },
				{ "PLANCASHDIVI", "&ensp;&ensp;&ensp;&ensp;拟分配现金股利" },
				{ "DIFFCONVERSIONFC", "&ensp;&ensp;&ensp;&ensp;外币报表折算差额" }, { "SUMPARENTEQUITY", "归属于母公司股东权益合计" },
				{ "MINORITYEQUITY", "&ensp;&ensp;&ensp;&ensp;少数股东权益" }, { "SUMSHEQUITY", "股东权益合计" },
				{ "SUMLIABSHEQUITY", "负债和股东权益合计" }, };
		String[][] lrb = new String[][] { { "TOTALOPERATEREVE", "营业总收入" }, { "OPERATEREVE", "营业收入" },
				{ "INTREVE", "利息收入" }, { "PREMIUMEARNED", "已赚保费" }, { "COMMREVE", "手续费及佣金收入" },
				{ "OTHERREVE", "其他业务收入" }, { "TOTALOPERATEEXP", "营业总成本" }, { "OPERATEEXP", "营业成本" },
				{ "INTEXP", "利息支出" }, { "COMMEXP", "手续费及佣金支出" }, { "RDEXP", "研发费用" }, { "SURRENDERPREMIUM", "退保金" },
				{ "NETINDEMNITYEXP", "赔付支出净额" }, { "NETCONTACTRESERVE", "提取保险合同准备金净额" }, { "POLICYDIVIEXP", "保单红利支出" },
				{ "RIEXP", "分保费用" }, { "OTHEREXP", "其他业务成本" }, { "OPERATETAX", "营业税金及附加" }, { "SALEEXP", "销售费用" },
				{ "MANAGEEXP", "管理费用" }, { "FINANCEEXP", "财务费用" }, { "ASSETDEVALUELOSS", "资产减值损失" },
				{ "FVALUEINCOME", "加:公允价值变动收益" }, { "INVESTINCOME", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;投资收益" },
				{ "INVESTJOINTINCOME", "&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;其中:对联营企业和合营企业的投资收益", },
				{ "EXCHANGEINCOME", "汇兑收益" }, { "OPERATEPROFIT", "营业利润" },
				{ "NONOPERATEREVE", "&ensp;&ensp;&ensp;&ensp;加:营业外收入" },
				{ "NONLASSETREVE", "&ensp;&ensp;&ensp;&ensp;其中:非流动资产处置利得" }, { "NONOPERATEEXP", "减:营业外支出" },
				{ "NONLASSETNETLOSS", "&ensp;&ensp;&ensp;&ensp;其中:非流动资产处置净损失" }, { "SUMPROFIT", "利润总额" },
				{ "INCOMETAX", "减:所得税费用" }, { "COMBINEDNETPROFITB", "被合并方在合并前实现利润" }, { "NETPROFIT", "净利润" },
				{ "PARENTNETPROFIT", "&ensp;&ensp;&ensp;&ensp;其中:归属于母公司股东的净利润", }, { "MINORITYINCOME", "少数股东损益" },
				{ "KCFJCXSYJLR", "扣除非经常性损益后的净利润" }, { "BASICEPS", "基本每股收益" }, { "DILUTEDEPS", "稀释每股收益" },
				{ "OTHERCINCOME", "其他综合收益" }, { "PARENTOTHERCINCOME", "归属于母公司股东的其他综合收益" },
				{ "MINORITYOTHERCINCOME", "归属于少数股东的其他综合收益" }, { "SUMCINCOME", "综合收益总额" },
				{ "PARENTCINCOME", "归属于母公司所有者的综合收益总额" }, { "MINORITYCINCOME", "归属于少数股东的综合收益总额" }, };
		String[][] xjllb = new String[][] { { "SALEGOODSSERVICEREC", "&ensp;&ensp;&ensp;&ensp;销售商品、提供劳务收到的现金", },
				{ "NIDEPOSIT", "&ensp;&ensp;&ensp;&ensp;客户存款和同业存放款项净增加额" },
				{ "NIBORROWFROMCBANK", "&ensp;&ensp;&ensp;&ensp;向中央银行借款净增加额" },
				{ "NIBORROWFROMFI", "&ensp;&ensp;&ensp;&ensp;向其他金融机构拆入资金净增加额", },
				{ "PREMIUMREC", "&ensp;&ensp;&ensp;&ensp;收到原保险合同保费取得的现金" },
				{ "NETRIREC", "&ensp;&ensp;&ensp;&ensp;收到再保险业务现金净额" },
				{ "NIINSUREDDEPOSITINV", "&ensp;&ensp;&ensp;&ensp;保户储金及投资款净增加额" },
				{ "NIDISPTRADEFASSET", "&ensp;&ensp;&ensp;&ensp;处置交易性金融资产净增加额" },
				{ "INTANDCOMMREC", "&ensp;&ensp;&ensp;&ensp;收取利息、手续费及佣金的现金" },
				{ "NIBORROWFUND", "&ensp;&ensp;&ensp;&ensp;拆入资金净增加额" },
				{ "NDLOANADVANCES", "&ensp;&ensp;&ensp;&ensp;发放贷款及垫款的净减少额" },
				{ "NIBUYBACKFUND", "&ensp;&ensp;&ensp;&ensp;回购业务资金净增加额" },
				{ "TAXRETURNREC", "&ensp;&ensp;&ensp;&ensp;收到的税费返还" },
				{ "OTHEROPERATEREC", "&ensp;&ensp;&ensp;&ensp;收到其他与经营活动有关的现金" }, { "SUMOPERATEFLOWIN", "经营活动现金流入小计" },
				{ "BUYGOODSSERVICEPAY", "&ensp;&ensp;&ensp;&ensp;购买商品、接受劳务支付的现金", },
				{ "NILOANADVANCES", "&ensp;&ensp;&ensp;&ensp;客户贷款及垫款净增加额" },
				{ "NIDEPOSITINCBANKFI", "&ensp;&ensp;&ensp;&ensp;存放中央银行和同业款项净增加额", },
				{ "INDEMNITYPAY", "&ensp;&ensp;&ensp;&ensp;支付原保险合同赔付款项的现金" },
				{ "INTANDCOMMPAY", "&ensp;&ensp;&ensp;&ensp;支付利息、手续费及佣金的现金" },
				{ "DIVIPAY", "&ensp;&ensp;&ensp;&ensp;支付保单红利的现金" },
				{ "EMPLOYEEPAY", "&ensp;&ensp;&ensp;&ensp;支付给职工以及为职工支付的现金" },
				{ "TAXPAY", "&ensp;&ensp;&ensp;&ensp;支付的各项税费" },
				{ "OTHEROPERATEPAY", "&ensp;&ensp;&ensp;&ensp;支付其他与经营活动有关的现金" }, { "SUMOPERATEFLOWOUT", "经营活动现金流出小计" },
				{ "NETOPERATECASHFLOW", "经营活动产生的现金流量净额" }, { "DISPOSALINVREC", "&ensp;&ensp;&ensp;&ensp;收回投资收到的现金" },
				{ "INVINCOMEREC", "&ensp;&ensp;&ensp;&ensp;取得投资收益收到的现金" },
				{ "DISPFILASSETREC", "&ensp;&ensp;&ensp;&ensp;处置固定资产、无形资产和其他长期资产收回的现金净额", },
				{ "DISPSUBSIDIARYREC", "&ensp;&ensp;&ensp;&ensp;处置子公司及其他营业单位收到的现金净额", },
				{ "REDUCEPLEDGETDEPOSIT", "&ensp;&ensp;&ensp;&ensp;减少质押和定期存款所收到的现金", },
				{ "OTHERINVREC", "&ensp;&ensp;&ensp;&ensp;收到其他与投资活动有关的现金" }, { "SUMINVFLOWIN", "投资活动现金流入小计" },
				{ "BUYFILASSETPAY", "&ensp;&ensp;&ensp;&ensp;购建固定资产、无形资产和其他长期资产支付的现金", },
				{ "INVPAY", "&ensp;&ensp;&ensp;&ensp;投资支付的现金" }, { "NIPLEDGELOAN", "&ensp;&ensp;&ensp;&ensp;质押贷款净增加额" },
				{ "GETSUBSIDIARYPAY", "&ensp;&ensp;&ensp;&ensp;取得子公司及其他营业单位支付的现金净额", },
				{ "ADDPLEDGETDEPOSIT", "&ensp;&ensp;&ensp;&ensp;增加质押和定期存款所支付的现金", },
				{ "OTHERINVPAY", "&ensp;&ensp;&ensp;&ensp;支付其他与投资活动有关的现金" }, { "SUMINVFLOWOUT", "投资活动现金流出小计" },
				{ "NETINVCASHFLOW", "投资活动产生的现金流量净额" }, { "ACCEPTINVREC", "&ensp;&ensp;&ensp;&ensp;吸收投资收到的现金" },
				{ "SUBSIDIARYACCEPT", "&ensp;&ensp;&ensp;&ensp;子公司吸收少数股东投资收到的现金", },
				{ "LOANREC", "&ensp;&ensp;&ensp;&ensp;取得借款收到的现金" },
				{ "ISSUEBONDREC", "&ensp;&ensp;&ensp;&ensp;发行债券收到的现金" },
				{ "OTHERFINAREC", "&ensp;&ensp;&ensp;&ensp;收到其他与筹资活动有关的现金" }, { "SUMFINAFLOWIN", "筹资活动现金流入小计" },
				{ "REPAYDEBTPAY", "&ensp;&ensp;&ensp;&ensp;偿还债务支付的现金" },
				{ "DIVIPROFITORINTPAY", "&ensp;&ensp;&ensp;&ensp;分配股利、利润或偿付利息支付的现金", },
				{ "SUBSIDIARYPAY", "&ensp;&ensp;&ensp;&ensp;子公司支付给少数股东的股利、利润", },
				{ "BUYSUBSIDIARYPAY", "&ensp;&ensp;&ensp;&ensp;购买子公司少数股权而支付的现金", },
				{ "OTHERFINAPAY", "&ensp;&ensp;&ensp;&ensp;支付其他与筹资活动有关的现金" },
				{ "SUBSIDIARYREDUCTCAPITAL", "&ensp;&ensp;&ensp;&ensp;子公司减资支付给少数股东的现金", },
				{ "SUMFINAFLOWOUT", "筹资活动现金流出小计" }, { "NETFINACASHFLOW", "筹资活动产生的现金流量净额" },
				{ "EFFECTEXCHANGERATE", "汇率变动对现金及现金等价物的影响" }, { "NICASHEQUI", "现金及现金等价物净增加额" },
				{ "CASHEQUIBEGINNING", "&ensp;&ensp;&ensp;&ensp;加:期初现金及现金等价物余额", },
				{ "CASHEQUIENDING", "期末现金及现金等价物余额" }, };
		Map<String, String[][]> tabListMap = new LinkedMap();
		tabListMap.put("资产负债表", zcfzb);
		tabListMap.put("利润表", lrb);
		tabListMap.put("现金流量表", xjllb);
		Map<String,String>tabMap = new HashMap<String,String>();
		tabMap.put("资产负债表", "zcfzb");
		tabMap.put("利润表", "lrb");
		tabMap.put("现金流量表", "xjllb");
		//InputStream inputStream = ExcelUtil.class.getClass().getResourceAsStream("/gz.xlsx");

		// String
		String excelTemplatePath="../electron-app/gz.tpl.xlsx";
		final FileInputStream inputStream2 = new FileInputStream(new File(excelTemplatePath));
		final Workbook workbook = WorkbookFactory.create(inputStream2);
		DataFormat format = workbook.createDataFormat();

		for (String tab : tabListMap.keySet()) {
			final Sheet sheet = workbook.getSheet(tab);
			String[][] mapItems = tabListMap.get(tab);
			List list = new ArrayList( Arrays.asList(mapItems));
			
	    	List<Map<String, Object>> result = jdbcTemplate.queryForList("select * from "+tabMap.get(tab)+" where reporttype=1 and code='"+stockCode+"' order by reportdate desc");

			list.add(0, new String[]{ "REPORTDATE", stockCode+"报告年度" });
			mapItems = (String[][]) list.toArray(new String[][] {});
			int rowId = 0;
			CellStyle yiStyle = workbook.createCellStyle();
			yiStyle.setDataFormat(format.getFormat("0!.00,,\"亿\"")); 
			
			CellStyle wStyle = workbook.createCellStyle();
			wStyle.setDataFormat(format.getFormat("0!.00,,\"万\"")); 
			
			
			for (int i = 0; i < mapItems.length; i++) {
				Row newRow = sheet.createRow(rowId);
				String label = mapItems[i][1].replaceAll("&ensp;", " ");
				Cell labelCell = newRow.createCell(1);
				labelCell.setCellValue(label);
				Cell nameCell = newRow.createCell(0);
				String code = mapItems[i][0];

				nameCell.setCellValue(code);

				for (int k2 = 0; k2 < result.size(); k2++) {
					Map<String, Object> items = result.get(k2);

					Cell valCell = newRow.createCell(2 + k2);
					// nameCell.setCellFormula("");

					if (items.get(code) instanceof String) {
						valCell.setCellValue((String) items.get(code));

					}else {
						Double value = (Double) items.get(code);
						if (items.get(code) instanceof Double) {
							valCell.setCellValue((Double) items.get(code));

						} else if (items.get(code) instanceof Integer) {
							valCell.setCellValue((Integer) items.get(code));

						} 		
						if(value!=null) {
							if(Math.abs(value)>100000000)valCell.setCellStyle(yiStyle);
							else if(Math.abs(value)>10000)valCell.setCellStyle(wStyle);			
						}

					}


				}
				rowId++;
			}
			

			for (int i = 0; i < result.size() + 2; i++) {
				sheet.autoSizeColumn((short) (i));
			}
		}

		workbook.setForceFormulaRecalculation(true);
		ExcelUtil.extractValueFromExcelToDB(jdbcTemplate,stockCode, workbook);

		//FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		 //Cell aCell = workbook.getSheetAt(0).getRow(32).getCell('B'-'A');
		// System.out.println(aCell.getStringCellValue());
		 
		 //System.out.println(evaluator.evaluate(aCell).getNumberValue());
		 
		// evaluator.evaluateAll();
		//workbook.setForceFormulaRecalculation(true);
		// Cell formulaCell = jobsheet.getRow(7).getCell('J'-'A');
		// System.out.println(formulaCell.getNumericCellValue());

		// Cell aCell = jobsheet.getRow(7).getCell('B'-'A');

		// aCell.setCellValue(0.5);
		// workbook.setForceFormulaRecalculation(true);
		// HSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);

		// evaluator.evaluateAll();
		// System.out.println(formulaCell.getNumericCellValue());
		// System.out.println(evaluator.evaluate(formulaCell).getNumberValue());

		//inputStream.close();

		String outputFile = "gz.xlsx".replace(".xlsx", ".bak.xlsx");
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		workbook.write(outputStream);
		workbook.write(out);
		outputStream.close();
		workbook.close();

		System.out.println(new File(outputFile).getAbsolutePath());
		System.out.println("");
		System.out.println("Report generated: " + outputFile);
		
		return workbook;

	
	}
}