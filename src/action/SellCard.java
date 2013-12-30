package action;

import info.CardCheck;
import info.GetUserInfo;
import info.UserCardsInfo;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import net.Process;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import start.Go;
import start.Info;

public class SellCard
  implements Runnable
{
  public static final ActionRegistry.Action Name = ActionRegistry.Action.SELL_CARD;
  public static boolean tried = false;

  private static final String URL_SELL_CARD = Info.LoginServer + 
    "/connect/app/trunk/sell?cyt=1";
  private static byte[] response;
  static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
  static FileWriter fileWriter = null;
  public static boolean isrun = false;

  public void run() {
    if (!(Info.autoSellCards))
      return;
    if (isrun) {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      run();
    }
    isrun = true;
    while (CardCheck.isrun || Process.info.userCardsInfos.size() != Process.info.cardNum)
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    System.out.print("读取卡片");
    ArrayList<NameValuePair> post = new ArrayList<>();
    String SellList = "";
    int number = 0;
    int price = 0;
    try {
      if (Info.log) {
        fileWriter = new FileWriter("CardSell.log", true);
        fileWriter.write(df.format(new Date()) + "\r\n");
      }
		for (int i = 0; i < Process.info.userCardsInfos.size(); i++){
			UserCardsInfo card = Process.info.userCardsInfos.get(i);
			if (					
					(
						(
							(Info.smartSell && ( card.lv < 5 
									&& (card.sale_price > 60 && card.sale_price < 200) ||(card.sale_price == 600)
									&& (card.hp > 5 || card.atk > 5)
									)
								)
							||
							(Info.CanBeSold.contains(card.master_card_id + "")
								&& card.lv < 5 									
							)
						)
					)&& !card.holography
					
					)
			{
				if (SellList == "" || SellList.isEmpty() || SellList == null)
					SellList = Integer.toString(card.serialId);
				else
				SellList += "," + card.serialId;
				System.out.print(".");
				number++;
				price = price + card.sale_price;
				if(Info.log)
					fileWriter.write(card.master_card_id +" " + card.sale_price + " " + card.lv +"\r\n");						
					
				}
			}
		if(Info.log)
			fileWriter.write("总计 " + number + " 张 " + price + "Gold\r\n");
			fileWriter.close();
		} catch(IOException e) {
			System.out.println("无法生成记录");
			Info.log = false;
		}
		post.add(new BasicNameValuePair("serial_id", SellList));
		if (number > 0){
			tried = false;
			System.out.println("读取完成，总计 " + number + " 张");
			} else {
      if (!(tried)) {
        System.out.println("无卡可卖,尝试重新登录");
        try {
          Login.run();
        } catch (Exception e) {
          e.printStackTrace();
        }
        tried = true;
        isrun = false;
        run();
        return;
      }
      System.out.println("无卡可卖");
      tried = false;
      isrun = false;
      return;
    }
    try {
    	while (Process.connect.Lock())
            Thread.sleep(100L);
      response = Process.connect.connectToServer(URL_SELL_CARD, post);
    }
    catch (Exception localException1)
    {
    }

    Document doc = null;
    try {
      doc = Process.ParseXMLBytes(response);
      GetUserInfo.getUserInfo(doc, false);
      CardCheck check = new CardCheck();
      check.doc = doc;
      Thread T1 = new Thread(check);
      T1.start();
      isrun = false;
      Process.info.events.push(Info.EventType.fairyAppear);
    }
    catch (Exception localException2)
    {
    }

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    try
    {
      String errorMessage = xpath.evaluate(
        "/response/header/error/message", doc);

      if (!(xpath.evaluate("/response/header/error/code", doc).equals(
        "1010"))) {
        Go.log(errorMessage);

        return;
      }

      Go.log(errorMessage);
      return;
    }
    catch (Exception localException3)
    {
    }
  }
}