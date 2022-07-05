package utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.nimbusds.oauth2.sdk.util.StringUtils;

public class SampleDataExtractor {

	private static String exportPath = "D:\\development\\ONDC\\NABARD-ONDC-Challenge\\sample-data\\";

	private static File pricefile = new File("D:\\\\development\\\\ONDC\\\\NABARD-ONDC-Challenge\\\\sample-data\\prices.csv");
	
	public static void main(String[] args) throws IOException {
		List<String> brandLines = FileUtils.readLines(pricefile);
		Set<String> units = new LinkedHashSet<String>();
		Map<String, List<Map<String, String>>> pricemap = new HashMap<>();
		for (String line : brandLines) {
			if(StringUtils.isNotBlank(line)) {
				List<String> vals = Arrays.asList(line.split(";"));
				if(vals.size() > 3 && StringUtils.isNotBlank(vals.get(0)) && StringUtils.isNotBlank(vals.get(1))) {
					if(pricemap.containsKey(vals.get(1).trim())) {
						List<Map<String, String>> prices = pricemap.get(vals.get(1).trim());
						Map<String, String> psu = new HashMap<String, String>();
						psu.put("code", vals.get(0).trim());
						psu.put("price", vals.get(2).trim());
						psu.put("stock", vals.get(3).trim());
						String unit = "unit" + prices.size();
						psu.put("unit", unit);
						units.add(unit);
						prices.add(psu);
					} else {
						List<Map<String, String>> prices = new ArrayList<Map<String,String>>();
						Map<String, String> psu = new HashMap<String, String>();
						psu.put("code", vals.get(0).trim());
						psu.put("price", vals.get(2).trim());
						psu.put("stock", vals.get(3).trim());
						String unit = "unit" + prices.size();
						psu.put("unit", unit);
						units.add(unit);
						prices.add(psu);
						pricemap.put(vals.get(1).trim(), prices);
					}
				}
			}
		}
		
		List<String> INRpsus = new ArrayList<>();
		INRpsus.add("&PSU=com.xhopfront.entities.PSU");
		INRpsus.add("");
		INRpsus.add("");
		INRpsus.add("UPSERT &PSU|product(attribute=code,unique=true)|unit(attribute=code,unique=true)|currency(attribute=isocode,unique=true)|price|availableStock");
		for (Entry<String, List<Map<String, String>>> entry : pricemap.entrySet()) {
			for (Map<String, String> psu : entry.getValue()) {
				INRpsus.add(String.format("|%s|%s|%s|%s|%s", psu.get("code"), psu.get("unit"), "INR", psu.get("price"), psu.get("stock")));
			}
		}
		
		File INRPSUFile = new File(exportPath + "INR-psu.psv");
		FileUtils.writeLines(INRPSUFile, INRpsus);
		
		List<String> grppsv = new ArrayList<>();
		grppsv.add("&OrderableUnit=com.xhopfront.entities.OrderableUnit");
		grppsv.add("");
		grppsv.add("");
		grppsv.add("UPSERT &OrderableUnit	|code(unique=true)	|name				|active");
		for (String string : units) {
			grppsv.add(String.format("						|%s	|%s	|%s", string, string, "TRUE"));
		}
		File unitsfile = new File(exportPath + "units.psv");
		FileUtils.writeLines(unitsfile, grppsv);
		
		System.out.println("COMPLETED!");
		
	}
}
