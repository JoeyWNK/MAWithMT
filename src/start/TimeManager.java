package start;

import info.FloorInfo;
import info.MAPConfigInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import action.GetAreaInfo;
import action.ReturnMain;

import net.Process;

public class TimeManager implements Runnable {
	public static boolean isrun = false;
	public static boolean done = false;
	private static String day = "";
	private static boolean showed = false;
	public static boolean found = false;
	private static int timer = 0;
	private static SimpleDateFormat df = new SimpleDateFormat("MM月dd日 EEEE");
	private static SimpleDateFormat hr = new SimpleDateFormat("HH:mm:ss");

	private static int convert(String day) {
		if (day.contains("一"))
			return 2;
		else if (day.contains("二"))
			return 3;
		else if (day.contains("三"))
			return 4;
		else if (day.contains("四"))
			return 5;
		else if (day.contains("五"))
			return 6;
		else if (day.contains("六"))
			return 7;
		return 1;

	}

	public void run() {
		while (true) {
			isrun = true;
			if (Process.info.events.isEmpty()) {
				Process.info.events.push(Info.EventType.fairyAppear);
			}
			int hour = Integer.parseInt(hr.format(new Date()).substring(0, 2));
			int min = Integer.parseInt(hr.format(new Date()).substring(3, 5));

			if (!day.contains(df.format(new Date()).substring(9, 10))) {
				day = df.format(new Date()).substring(6, 10);
				found = false;
				for (MAPConfigInfo MapConfigInfo : Info.MAPConfigInfos) {
					if (MapConfigInfo.day == convert(day)) {
						Info.maptimelimitDown = MapConfigInfo.maptimelimitDown;
						Info.maptimelimitUp = MapConfigInfo.maptimelimitUp;
						Info.dayFirst = MapConfigInfo.daily;
						String str = "\n" + (df.format(new Date()));
						if (MapConfigInfo.maptimelimitUp != -1)
							str += " 限时秘境时段 " + Info.maptimelimitDown + ":00-"
									+ Info.maptimelimitUp + ":00";
						if (Info.dayFirst.contains("1"))
							str += " 每日秘境优先";
						System.out.println(str);
						break;
					}
				}
			}
			if (Info.maptimelimitUp == -1 || found) {
				Info.maptimelimit = true;
				try {
					Thread.sleep(600000);
				} catch (InterruptedException e) {
				}
				isrun = false;
				continue;
			}
			if (timer < min + 10 && hour >= Info.maptimelimitDown
					&& hour <= Info.maptimelimitUp && !found
					&& !Info.maptimelimit) {
				try {
					ReturnMain.run();
					if (GetAreaInfo.run(true)) {
						if (Process.info.floorInfos.size() > 0) {
							for (FloorInfo fInfo1 : Process.info.floorInfos) {
								if (fInfo1.name.contains("限时")) {
									Info.maptimelimit = true;
									found = true;
									System.out.println("发现限时秘境!");
									if (Integer.parseInt(fInfo1.prog_area) < 100) {
										Info.hasPrivateFairyStopRun = 0;
										done = false;
									} else {
										Info.hasPrivateFairyStopRun = Info.hasPrivateFairyStopRunOriginal;
										done = true;
									}
									Process.info.events
											.push(Info.EventType.needFloorInfo);
									break;
								}
							}

						}
					}
				} catch (Exception e) {
					System.out.println("地图刷新失败:" + e.toString());
				}
				timer = min;
			}

			if (!found
					&& Info.maptimelimitDown
							- (Info.stopRunWhenApLess - Process.info.apCurrent)
							/ 20 <= hour && hour <= Info.maptimelimitUp) {
				if (!showed) {
					showed = true;
					System.out.println("保留AP");
				}
				Info.maptimelimit = false;

			} else {
				Info.maptimelimit = true;
				showed = false;
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
			isrun = false;
		}
	}
}
