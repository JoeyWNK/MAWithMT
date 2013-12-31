package info;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import start.Info;

import net.Process;

public class GetUserInfo {

	public static void getUserInfo(Document doc, boolean getId)
			throws Exception {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Process.info.cookie = xpath.evaluate("//session_id", doc);
		Process.info.userName = xpath.evaluate("//your_data/name", doc);
		Process.info.userLv = xpath.evaluate("//your_data/town_level", doc);
		Process.info.apMax = Integer.parseInt(xpath.evaluate(
				"//your_data/ap/max", doc));
		Process.info.apCurrent = Integer.parseInt(xpath.evaluate(
				"//your_data/ap/current", doc));
		Process.info.bcMax = Integer.parseInt(xpath.evaluate(
				"//your_data/bc/max", doc));
		Process.info.bcCurrent = Integer.parseInt(xpath.evaluate(
				"//your_data/bc/current", doc));
		Process.info.freeApBcPoint = Integer.parseInt(xpath.evaluate(
				"//your_data/free_ap_bc_point", doc));
		Process.info.friendpoint = Integer.parseInt(xpath.evaluate(
				"//your_data/friendship_point", doc));
		if (getId) {

			Process.info.userId = xpath.evaluate(
					"/response/body/login/user_id", doc);
			Process.info.friends = Integer.parseInt(xpath.evaluate(
					"//your_data/friends", doc));
			Process.info.friendMax = Integer.parseInt(xpath.evaluate(
					"//your_data/friend_max", doc));
			Process.info.invitations = Integer.parseInt(xpath.evaluate(
					"//your_data/friends_invitations", doc));
			Process.info.rewards = Integer.parseInt(xpath.evaluate(
					"/response/body/mainmenu/rewards", doc));
			System.out.print("获取物品列表");
			if ((boolean) xpath.evaluate(
					"count(//your_data/itemlist[item_id=1])>0", doc,
					XPathConstants.BOOLEAN)) {
				Process.info.fullAp = Integer.parseInt(xpath.evaluate(
						"//your_data/itemlist[item_id=1]/num", doc));
			}
			if ((boolean) xpath.evaluate(
					"count(//your_data/itemlist[item_id=2])>0", doc,
					XPathConstants.BOOLEAN)) {
				Process.info.fullBc = Integer.parseInt(xpath.evaluate(
						"//your_data/itemlist[item_id=2]/num", doc));
			}
			if ((boolean) xpath.evaluate(String.format(
					"count(//your_data/itemlist[item_id=%s])>0",
					Process.info.gatherID), doc, XPathConstants.BOOLEAN)) {
				Process.info.gather = Integer.parseInt(xpath.evaluate(String
						.format("//your_data/itemlist[item_id=%s]/num",
								Process.info.gatherID), doc));
			}
			if ((boolean) xpath.evaluate("//your_data/max_card_num > 0", doc,
					XPathConstants.BOOLEAN)) {
				Process.info.cardMax = Integer.parseInt(xpath.evaluate(
						"//your_data/max_card_num", doc));
			}
			System.out.println("[OK]");

			int wolfcount = ((NodeList) xpath
					.evaluate(
							"//your_data/owner_card_list/user_card[master_card_id=124]",
							doc, XPathConstants.NODESET)).getLength();
			int wolflv = 0;
			String wolf = "";
			for (int i = 1; i <= wolfcount; i++)
				if (Integer
						.parseInt(xpath.evaluate(
								String.format(
										"//your_data/owner_card_list/user_card[master_card_id=124][%d]/lv",
										i), doc)) > wolflv) {
					wolf = xpath
							.evaluate(
									String.format(
											"//your_data/owner_card_list/user_card[master_card_id=124][%d]/serial_id",
											i), doc);
				}
			if ((Info.pvpCard.equals(""))
					&& ((wolf == null) || (wolf.equals("")))) {
				Info.isPVP = "0";
			}
			if (null == wolf || wolf.equals("")) {
				Info.wolf = Info.wolf
						+ ",empty,empty,empty,empty,empty,empty,empty,empty,empty,empty,empty";
			} else {
				Info.wolf = wolf
						+ ",empty,empty,empty,empty,empty,empty,empty,empty,empty,empty,empty";
				Info.wolfLr = wolf;
				Info.lickCost = 2;
			}
			Process.info.cardNum = ((NodeList) xpath.evaluate(
					"//owner_card_list/user_card", doc, XPathConstants.NODESET))
					.getLength();
			if (Process.info.cardNum >= Process.info.cardMax) {
				if (CardCheck.isrun)
					Process.cardcheck.interrupt();
				CardCheck.doc = doc;
				Process.cardcheck.setPriority(2);
				Process.cardcheck.start();
			}

		}
	}

}
