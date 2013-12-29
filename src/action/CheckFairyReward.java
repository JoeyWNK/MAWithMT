package action;

import info.CardCheck;
import info.CreateXML;
import java.util.ArrayList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import net.Process;

import org.apache.http.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import start.Go;
import start.Info;

public class CheckFairyReward
{
  private static final String URL_FAIRY_REWARD = Info.LoginServer + 
    "/connect/app/menu/fairyrewards?cyt=1";

  public static boolean run() throws Exception {
    if (SellCard.isrun)
      return false;
    if ((!(SellCard.tried)) && 
      (Info.autoSellCards) && 
      (Process.info.fairyRewardCount >= Process.info.cardMax - 
      Process.info.cardNum)) {
      Go.log("现有奖励:" + Process.info.fairyRewardCount + " 领取作战奖励，并卖卡");
      ArrayList<NameValuePair> al = new ArrayList<>();
      byte[] result;
      Document doc;
      try {
    	  while (Process.connect.Lock())
    	        Thread.sleep(100L);
        result = Process.connect.connectToServer(URL_FAIRY_REWARD, al);
        doc = Process.ParseXMLBytes(result);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        if (ExceptionCatch.catchException(doc)) {
          return false;
        }
        CreateXML.createXML(doc, "FairyRewards");
        CardCheck check = new CardCheck();
        check.doc = doc;
        Thread T1 = new Thread(check);
        T1.setPriority(2);
        T1.start();
        int rewardCount = ((NodeList)xpath.evaluate(
          "//fairy_rewards/reward_details", doc, 
          XPathConstants.NODESET)).getLength();
        for (int i = 1; i <= rewardCount; ++i) {
          String msg = "";
          msg = xpath
            .evaluate(
            String.format(
            "//fairy_rewards/reward_details[%d]/fairy/name", new Object[] { 
            Integer.valueOf(i) }), doc).trim();
          msg = msg + 
            " Lv." + 
            xpath.evaluate(
            String.format(
            "//fairy_rewards/reward_details[%d]/fairy/lv", new Object[] { 
            Integer.valueOf(i) }), doc).trim();
          msg = msg + 
            " 获得 " + 
            xpath.evaluate(
            String.format(
            "//fairy_rewards/reward_details[%d]/item_name", new Object[] { 
            Integer.valueOf(i) }), doc).trim();
          System.out.println(msg);
        }
      } catch (Exception ex) {
        throw ex;
      }
      try {
        while (SellCard.isrun);
        Thread.sleep(10000L);
        Go.log("尝试卖卡");
        SellCard sell = new SellCard();
        Thread T1 = new Thread(sell);
        T1.setPriority(2);
        T1.start();
      }
      catch (Exception ex)
      {
        throw ex;
      }
      return true;
    }
    return false;
  }
}