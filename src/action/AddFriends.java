package action;

import info.CreateXML;
import info.UserInfo;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.Process;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import start.Go;
import start.Info;

public class AddFriends
  implements Runnable
{
  private static final String URL_FRIEND_NOTICE = Info.LoginServer + 
    "/connect/app/menu/friend_notice?cyt=1";

  private static final String URL_ADD_FRIEND = Info.LoginServer + 
    "/connect/app/friend/approve_friend?cyt=1";
  public static boolean isrun = false;
  private static int list = 0;

  public void run() {
    if (isrun) {
      int no = ++list;
      try {
        Thread.sleep(100000L);
      } catch (InterruptedException localInterruptedException) {
      }
      finally {
        if (no < list)
          return;
        run();
      }
    }
    isrun = true;

    Go.log("现有好友邀请:" + Process.info.invitations + ",查看列表");
    ArrayList<NameValuePair> al = new ArrayList<>();
    al.add(new BasicNameValuePair("move", "1"));
    try {
      while (Process.connect.Lock())
        Thread.sleep(100L);
      byte[] result = Process.connect.connectToServer(URL_FRIEND_NOTICE, al);
      Document doc = Process.ParseXMLBytes(result);

      if (ExceptionCatch.catchException(doc)) {
        return;
      }
      CreateXML.createXML(doc, "FriendNotice");
      prase(doc);
    }
    catch (Exception localException)
    {
    }
    isrun = false;
  }

  static void prase(Document doc)
  {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    try {
      NodeList list = (NodeList)xpath.evaluate("/response/body/friend_notice/user_list/user", doc, 
        XPathConstants.NODESET);
      if (list.getLength() > 0) {
        List<UserInfo> userlists = new ArrayList<UserInfo>();
        UserInfo user = new UserInfo();
        for (int i = 0; i < list.getLength(); ++i) {
          Node f = list.item(i).getFirstChild();
          user = new UserInfo();
          do {
            if (f.getNodeName().equals("id"))
              user.id = f.getFirstChild()
                .getNodeValue();
            else if (f.getNodeName().equals("name"))
              user.name = f.getFirstChild()
                .getNodeValue();
            else if (f.getNodeName().equals("cost"))
              user.cost = Integer.parseInt(f.getFirstChild()
                .getNodeValue());
            else if (f.getNodeName().equals("last_login"))
              user.last_login = f.getFirstChild()
                .getNodeValue();
            else if (f.getNodeName().equals("town_level")) {
              user.level = Integer.parseInt(f.getFirstChild()
                .getNodeValue());
            }
            f = f.getNextSibling(); }
          while (f != null);
          userlists.add(user);
        }
        for (int i = 0; i < userlists.size(); ++i)
        {
          ArrayList<NameValuePair> al = new ArrayList<>();
          al.add(new BasicNameValuePair("dialog", "1"));
          al.add(new BasicNameValuePair("user_id", ((UserInfo)userlists.get(i)).id));
          Go.log("添加好友:" + ((UserInfo)userlists.get(i)).name + 
            " Lv." + ((UserInfo)userlists.get(i)).level + 
            " MaxBC:" + ((UserInfo)userlists.get(i)).cost);
          try {
            while (Process.connect.Lock())
              Thread.sleep(100L);
            byte[] result = Process.connect.connectToServer(URL_ADD_FRIEND, al);
            doc = Process.ParseXMLBytes(result);
            CreateXML.createXML(doc, "FriendAdd");
            ExceptionCatch.catchException(doc);
          }
          catch (Exception ex)
          {
            Go.log(ex.toString());
          }

        }

        Process.info.invitations = 0;
      }
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
  }
}