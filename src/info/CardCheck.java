package info;

import java.util.ArrayList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.Process;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import start.Go;
import start.Info;

public class CardCheck implements Runnable {
	public static boolean isrun = false;
	public static Document doc;

	public void run() {
		isrun = true;
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {
			int cardCount = ((NodeList) xpath.evaluate(
					"//owner_card_list/user_card", CardCheck.doc,
					XPathConstants.NODESET)).getLength();
			ArrayList<UserCardsInfo> CardList = new ArrayList<>();
			for (int i = 1; i < cardCount + 1; ++i) {
				UserCardsInfo c = new UserCardsInfo();
				String p = String.format("//owner_card_list/user_card[%d]",
						new Object[] { Integer.valueOf(i) });
				c.serialId = Integer.parseInt(xpath.evaluate(p + "/serial_id",
						CardCheck.doc));
				c.master_card_id = Integer.parseInt(xpath.evaluate(p
						+ "/master_card_id", CardCheck.doc));
				c.lv = Integer.parseInt(xpath
						.evaluate(p + "/lv", CardCheck.doc));
				c.hp = Integer.parseInt(xpath
						.evaluate(p + "/hp", CardCheck.doc));
				c.atk = Integer.parseInt(xpath.evaluate(p + "/power",
						CardCheck.doc));
				c.sale_price = Integer.parseInt(xpath.evaluate(p
						+ "/sale_price", CardCheck.doc));
				c.holography = xpath.evaluate(p + "/holography", CardCheck.doc)
						.equals("1");
				CardList.add(c);
			}
			Process.info.userCardsInfos = CardList;
			Process.info.cardNum = Process.info.userCardsInfos.size();
			isrun = false;
		} catch (XPathExpressionException e) {
			Go.log("卡片读取错误/n" + e);
			if (Info.devMode) {
				CreateXML.createXML(CardCheck.doc, "CardInfo");
			} else {
				Info.devMode = true;
				CreateXML.createXML(CardCheck.doc, "CardInfo");
				Info.devMode = false;
			}
		}
	}
}