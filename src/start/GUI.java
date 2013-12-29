package start;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class GUI extends JFrame{
	
	public GUI() throws IOException, Exception{
		JOptionPane.showMessageDialog(null, "未选择Config.xml文件", "警告", JOptionPane.WARNING_MESSAGE, null);
		JFileChooser c = new JFileChooser();
		  c.setDialogTitle("选择Config.xml文件");
		  if (c.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			  GetConfig.readConfig(c.getSelectedFile().getCanonicalPath());
		  }
		  else{
			  JOptionPane.showMessageDialog(null, "未选择Config.xml文件", "警告", JOptionPane.WARNING_MESSAGE, null);
			  }  
		  }
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
