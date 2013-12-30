package start;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings({ "serial", "unused" })
public class GUI extends JFrame implements Runnable{
	
/**	  private JTextArea jmpState = new JTextArea(5,5);
	  private JScrollPane scollpane = new JScrollPane(jmpState);
	  private JScrollBar scrollBar = scollpane.getVerticalScrollBar();
**/	  
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
/**		  setLayout(new GridLayout(1,1,0,0));
		  JPanel p1 = new JPanel(new BorderLayout());
		  p1.add(scollpane);
		  jmpState.setEditable(false);
		  jmpState.addComponentListener(new ComponentListener(){
				public void componentResized(ComponentEvent arg0) {
					scrollBar.setValue(scrollBar.getMaximum());
				}

				public void componentShown(ComponentEvent arg0) {
				}

				public void componentHidden(ComponentEvent arg0) {	
				}

				public void componentMoved(ComponentEvent arg0) {					
				}
	        });
		  PrintStream printStream = new PrintStream(new CustomOutputStream(jmpState));
		  System.setOut(printStream);
		  System.setErr(printStream);
		  add(p1);
		  
		  }
	
    
	public void run() {	
		
	}
	public class CustomOutputStream extends OutputStream {
	    private JTextArea textArea;
	     
	    public CustomOutputStream(JTextArea textArea) {
	        this.textArea = textArea;
	    }
	     
	    @Override
	    public void write(int b) throws IOException {
	        // redirects data to the text area
	        textArea.append(URLDecoder.decode(URLEncoder.encode(String.valueOf((char)b), "utf-8"),"GBK"));
	        // scrolls the text area to the end of data
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    }
**/	}

@Override
public void run() {
	// TODO Auto-generated method stub
	
}
}
