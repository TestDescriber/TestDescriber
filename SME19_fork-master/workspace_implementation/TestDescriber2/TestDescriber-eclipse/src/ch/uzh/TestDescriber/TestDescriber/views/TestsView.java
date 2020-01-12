package ch.uzh.TestDescriber.TestDescriber.views;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.*;
import org.osgi.framework.Bundle;

import ch.uzh.TestDescriber.TestDescriber.views.ListViewPart.TreeParent;
import ch.uzh.TestDescriber.TestDescriber.views.ListViewPart.ViewContentProvider;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.ui.ide.IDE;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class TestsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ch.uzh.TestDescriber.TestDescriber.views.TestsView";

	@Inject IWorkbench workbench;
	
	private Action action1;
	private String testPath;
	RowLayout rowLayout;
	Composite viewParent;
	List<Widget> widgets;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		widgets = new ArrayList<Widget>();
		
		viewParent = parent;
        
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		
	}
	
	public void setTestPath(String path) {
		testPath = path;
		initialize();
	}
	
	private String getTestFunctionName(String line) {
		String pattern = "public [^\\s]+ ([^\\s]+) *\\(";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	private String getTestClassName(String line) {
		String pattern = "public [abstract ]*class ([^\\s]+) *\\{*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(line);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	private String getTestFunctionComment(List<String> allLines, int index, int backward, int forward) {
		String comment = "";
		
		// Get preceding comment start and end
		int maxBackward = index - backward >= 0 ? index - backward : 0;
		int startBackward = -1;
		int endBackward = -1;
		for (int i = index - 1; i >= maxBackward; i--) {
			String line = allLines.get(i);
			if (line.contains("*/")) {
				endBackward = i;
			} else if (line.contains("/*")) {
				startBackward = i;
				break;
			} else if (line.contains("public class")) {
				break;
			}
		}
		
		// Get preceding comment content
		if(startBackward != -1 && endBackward != -1) {
			for (int i = startBackward; i < endBackward; i++) {
				String line = allLines.get(i).replace("\t", "").replace("/*", "").replace("*/", "").replace("*", "").replace("  ", "").replace("OVERVIEW: ", "");
				if (!line.isEmpty()) {
					comment += line + "\n";
				}
			}
		}
		
		// Get following comment
		int maxForward = index + forward < allLines.size() ? index + forward : allLines.size();
		boolean foundComment = false;
		for (int i = index + 1; i < maxForward; i++) {
			String line = allLines.get(i);
			if (line.contains("//")) {
				comment += line.replace("\t", "").replace("//", "").replace("  ", "") + "\n";
				foundComment = true;
			} else if (!line.contains("}")) {
				continue;
			} else if (foundComment == true) {
				break;
			}
		}
		
		// Return comment if found
		if (!comment.isEmpty()) {
			return comment;
		}
		return null;
	}
	
	private String getTestClassComment(List<String> allLines, int index, int backward) {
		String comment = "";
		
		// Get preceding comment start and end
		int maxBackward = index - backward >= 0 ? index - backward : 0;
		int startBackward = -1;
		int endBackward = -1;
		for (int i = index - 1; i >= maxBackward; i--) {
			String line = allLines.get(i);
			if (line.contains("*/")) {
				endBackward = i;
			} else if (line.contains("/*")) {
				startBackward = i;
				break;
			} else if (line.contains("public class")) {
				break;
			}
		}
		
		// Get preceding comment content
		if(startBackward != -1 && endBackward != -1) {
			for (int i = startBackward; i < endBackward; i++) {
				String line = allLines.get(i).replace("\t", "").replace("/*", "").replace("*/", "").replace("*", "").replace("  ", "");
				if (!line.isEmpty()) {
					comment += line + "\n";
				}
			}
		}
		
		// Return comment if found
		if (!comment.isEmpty()) {
			return comment;
		}
		return null;
	}
	
	private IFile fileToIFile (File file) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);
		return ifile;
	}
	
	private void initialize() {
		// Delete old widgets
		for (Widget widget : widgets) {
			widget.dispose();
		}
		widgets = new ArrayList<Widget>();
		
		// Create new layout
		viewParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewParent.setLayout(new GridLayout(1, false));
	    ScrolledComposite sc = new ScrolledComposite(viewParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	    sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    sc.setBackground(viewParent.getBackground());
	    Composite composite = new Composite(sc, SWT.NONE);
	    composite.setLayout(new GridLayout(1, false));
	    composite.setBackground(sc.getBackground());
        widgets.add(sc);
        widgets.add(composite);
		
        // Open given file path
        File testFile = new File(testPath);
        if (testFile.exists() && testFile.isFile()) {
    		try {
    			// Show open file button
				Button button = new Button(composite, SWT.WRAP);
				button.setText("Open file in editor");
				button.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						IFile ifile = fileToIFile(testFile);
					    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					    try {
					        IDE.openEditor(page, ifile, true);
					    } catch (PartInitException e) {
							e.printStackTrace();
					    }
					}
				});
				widgets.add(button);
    			
    			// Get all lines in file
    			List<String> allLines = Files.readAllLines(Paths.get(testFile.getAbsolutePath()));
    			int index = 0;
				
    			for (String line:allLines) {
    				// Get function/class name
    				String testFunctionName = getTestFunctionName(line);
					String testClassName = getTestClassName(line);
    				
    				// Handle function comment
    				if (testFunctionName != null) {
    					// Create function name label
    		            Label headingLabel = new Label(composite, SWT.WRAP);
    		            headingLabel.setBackground(composite.getBackground());
    		            headingLabel.setText(testFunctionName);
    		            FontData[] fD = headingLabel.getFont().getFontData();
    		            fD[0].setHeight(14);
    		            headingLabel.setFont( new Font(null, fD[0]));
    		        	widgets.add(headingLabel);
						
						// Get test function comment
						String testFunctionComment = getTestFunctionComment(allLines, index, 10, 12);
    					if (testFunctionComment != null) {
    						// Get pass/fail status
    						if (testFunctionComment.contains("This test has passed.")) {
    							// Create row
    						    Composite statusComposite = new Composite(composite, SWT.WRAP);
    						    statusComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    						    statusComposite.setBackground(composite.getBackground());
    	    		        	widgets.add(statusComposite);
    						    
    	    		        	// Create tick image
    						    Label imageLabel = new Label(statusComposite, SWT.WRAP);
    						    Bundle bundle = Platform.getBundle("ch.uzh.TestDescriber.TestDescriber");
    						    URL url = FileLocator.find(bundle, new Path("icons/tick.png"), null);
            					ImageDescriptor image = ImageDescriptor.createFromURL(url);
            					imageLabel.setBackground(composite.getBackground());
            					imageLabel.setImage(image.createImage());
    	    		        	widgets.add(imageLabel);
    							
    							// Create pass label
            		            Label statusLabel = new Label(statusComposite, SWT.WRAP);
            		            statusLabel.setBackground(composite.getBackground());
            		            statusLabel.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
            		            statusLabel.setText("Passed" + "\n");
            		        	widgets.add(statusLabel);
            		        	
    						} else if (testFunctionComment.contains("This test has failed.")) {
    							// Create row
    						    Composite statusComposite = new Composite(composite, SWT.WRAP);
    						    statusComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
    						    statusComposite.setBackground(composite.getBackground());
    	    		        	widgets.add(statusComposite);
    						    
    	    		        	// Create tick image
    						    Label imageLabel = new Label(statusComposite, SWT.WRAP);
    						    Bundle bundle = Platform.getBundle("ch.uzh.TestDescriber.TestDescriber");
    						    URL url = FileLocator.find(bundle, new Path("icons/cross.png"), null);
            					ImageDescriptor image = ImageDescriptor.createFromURL(url);
            					imageLabel.setBackground(composite.getBackground());
            					imageLabel.setImage(image.createImage());
    	    		        	widgets.add(imageLabel);
    							
    							// Create pass label
            		            Label statusLabel = new Label(statusComposite, SWT.WRAP);
            		            statusLabel.setBackground(composite.getBackground());
            		            statusLabel.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
            		            statusLabel.setText("Failed" + "\n");
            		        	widgets.add(statusLabel);
    						}
    						
    						// Remove pass/fail status from comment
    						testFunctionComment = testFunctionComment.replace("This test has passed.", "").replace("This test has failed.", "");
    						
    						// Create function comment label
        		            Label commentLabel = new Label(composite, SWT.WRAP);
        		            commentLabel.setBackground(composite.getBackground());
        		            commentLabel.setText(testFunctionComment + "\n");
        		        	widgets.add(commentLabel);
    					}
        					
    				// Handle class comment
    				} else if (testClassName != null) {
    					// Create class name label
    		            Label headingLabel = new Label(composite, SWT.WRAP);
    		            headingLabel.setBackground(composite.getBackground());
    		            headingLabel.setText(testClassName);
    		            FontData[] fD = headingLabel.getFont().getFontData();
    		            fD[0].setHeight(18);
    		            headingLabel.setFont( new Font(null, fD[0]));
    		        	widgets.add(headingLabel);
    					
    					// Get test class comment
						String testClassComment = getTestClassComment(allLines, index, 10);
    					if (testClassComment != null) {
    						// Create class comment label
        		            Label commentLabel = new Label(composite, SWT.WRAP);
        		            commentLabel.setBackground(composite.getBackground());
        		            commentLabel.setText(testClassComment + "\n");
        		        	widgets.add(commentLabel);
    					}
    				}
    				index++;
    			}
    		} catch (IOException e) {
            	Label label = new Label(composite, SWT.WRAP);
            	label.setBackground(composite.getBackground());
            	label.setText("Could not read given file.");
            	widgets.add(label);
    			e.printStackTrace();
    		}
        } else {
        	Label label = new Label(composite, SWT.WRAP);
        	label.setBackground(composite.getBackground());
        	label.setText("Could not find given file.");
        	widgets.add(label);
        }
        
        // Refresh view with new layout
	    sc.setContent(composite);
	    composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        viewParent.pack();
        viewParent.layout(true);
	    viewParent.setSize(viewParent.getParent().getSize());
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TestsView.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				initialize();
			}
		};
		action1.setText("Refresh");
		action1.setToolTipText("Refresh test");
		try {
			URL url = new URL("platform:/plugin/org.eclipse.ui/icons/full/elcl16/refresh_nav.png");
			ImageDescriptor image = ImageDescriptor.createFromURL(url);
			action1.setImageDescriptor(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewParent.getShell(),
			"TestDescriber Test",
			message);
	}

	@Override
	public void setFocus() {
		viewParent.setFocus();
	}
}
