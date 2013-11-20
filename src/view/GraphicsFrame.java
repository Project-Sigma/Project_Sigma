package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/*
 * Class: GraphicsFrame
 * Author: Keith Lueneburg
 *         Project Sigma
 * Last Update: 11/19/2013  
 * 
 * JFrame subclass for displaying a JPanel with graphics/drawing. Can save to 
 * image file types determined at runtime by Java's ImageIO.
 * 
 * Ideas/Not Fully Implemented:
 *     -Print displayed graphics directly from program. **IN PROGRESS**
 */

public class GraphicsFrame extends JFrame implements Printable {
	// reference to this frame for inner classes
	private GraphicsFrame this_frame = this;

	// panel containing graphics
	final private JPanel panel;

	final private JMenuBar menu;

	// Create a new Frame to display provided JPanel
	public GraphicsFrame(final JPanel newPanel) {
		super(newPanel.getName());

		panel = newPanel;
		menu = createMenuBar();
	}

	// Initialize settings, add sub-components, and show window.
	public void start() {
		// TODO: Disable this when the rest of the program is functional??
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLocationByPlatform(true);

		// Get size from member panel's preferred size.
		setSize(panel.getPreferredSize());

		setResizable(false);

		add(panel);

		setJMenuBar(menu);

		setVisible(true);
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
		
		// We have only one page, and 'page'
	    // is zero-based
	    if (page > 0) {
	         return NO_SUCH_PAGE;
	    }

	    // User (0,0) is typically outside the
	    // imageable area, so we must translate
	    // by the X and Y values in the PageFormat
	    // to avoid clipping.
	    Graphics2D g2d = (Graphics2D)g;
	    g2d.translate(pf.getImageableX(), pf.getImageableY());


	    // Now we perform our rendering
	    panel.paint(g2d);
	    
	    // tell the caller that this page is part
	    // of the printed document
	    return PAGE_EXISTS;
	}
	
	// Build menu bar for this frame.
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem saveAs = new JMenuItem(new SaveAsAction());
		saveAs.setDisplayedMnemonicIndex(5);

		JMenuItem print = new JMenuItem(new PrintAction());

		JMenuItem close = new JMenuItem(new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent event) {
				this_frame.dispose();
			}
		});

		fileMenu.add(saveAs);
		fileMenu.add(print);

		fileMenu.addSeparator();

		fileMenu.add(close);

		menuBar.add(fileMenu);

		return menuBar;
	}

	// Action for save as button. 
	public final class SaveAsAction extends AbstractAction {
		public SaveAsAction() {
			super("Save As...");
			putValue(MNEMONIC_KEY, KeyEvent.VK_A);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			{
				// Create a new image to paint to.
				BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics2D = image.createGraphics(); 

				// Paint to the image.
				panel.paint(graphics2D);

				try{

					JFileChooser chooser = getImageFileSaveChooser();
					int result = chooser.showSaveDialog(null);

					if (result == JFileChooser.APPROVE_OPTION){
						String fileType = chooser.getFileFilter().getDescription();
						File selectedFile = chooser.getSelectedFile();

						// if file extension doesn't match selected file type
						if (!selectedFile.getName().toLowerCase().endsWith(fileType)) {
							selectedFile = new File(selectedFile.getAbsolutePath() + "." + fileType);
						}

						ImageIO.write(image, fileType, selectedFile);
					}
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Save failed!", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// create a file chooser with options for supported image file extensions
		private JFileChooser getImageFileSaveChooser() {
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));

			String[] formatNames = ImageIO.getWriterFormatNames();

			Set<String> formatNameSet = new TreeSet<String>();

			for (String s : formatNames) {
				formatNameSet.add(s.toLowerCase());
			}

			for (final String s : formatNameSet) {
				chooser.addChoosableFileFilter(new FileFilter() {
					private String description = s;

					@Override
					public boolean accept(File file) {
						boolean result = false;

						if (file.getName().toLowerCase().endsWith("." + description)) {
							result = true;
						}
						return result;
					}

					@Override
					public String getDescription() {
						return description;
					}
				});
			}
			return chooser;
		}
	}

	public final class PrintAction extends AbstractAction {
		public PrintAction() {
			super("Print...");
			putValue(MNEMONIC_KEY, KeyEvent.VK_P);
			
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			{
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(this_frame);
				
				boolean doPrint = job.printDialog();

				if (doPrint) {
				    try {
				        job.print();
				    } catch (PrinterException e) {
				    	System.out.println("ERROR PRINTING");
				    	// The job did not successfully
				        // complete
				    }
				}
			}
		}
	}
}