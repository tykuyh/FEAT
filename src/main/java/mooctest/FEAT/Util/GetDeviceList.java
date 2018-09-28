package mooctest.FEAT.Util;

import java.util.ArrayList;
import java.util.List;

public class GetDeviceList {

	public static List<String> getDeviceList() {
		System.out.println("getDevices");
		String[] tmp = OSUtil.runCommand("adb devices").split("\n");
		ArrayList<String> deviceList = new ArrayList<String>();
		for (int i = 1; i < tmp.length; ++i) {
			System.out.println("find Devices " + tmp[i]);
			String[] array = tmp[i].split("\t");
			if (array.length == 2) {
				String d = null;
				String udid = tmp[i].split("\t")[0];
				String status = tmp[i].split("\t")[1];
				if (!status.equals("device")) {
					continue;
				}
				d = udid;
				deviceList.add(d);
			} else {
				System.out.println("invalid message " + tmp[i]);
			}
		}
		return deviceList;
	}
	
	public static void main(String[] args) {
		List<String> list= GetDeviceList.getDeviceList();
		for(int i =0;i<list.size();i++) System.out.println(list.get(i));
	}
}
