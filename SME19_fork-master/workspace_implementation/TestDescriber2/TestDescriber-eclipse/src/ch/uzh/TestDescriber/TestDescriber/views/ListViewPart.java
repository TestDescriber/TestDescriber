package ch.uzh.TestDescriber.TestDescriber.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import javax.inject.Inject;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ListViewPart extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ch.uzh.TestDescriber.TestDescriber.views.ListViewPart";

	@Inject IWorkbench workbench;
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action action3;
	private Action doubleClickAction;
	private String testsFolderPath = "C:\\Users\\Jacob\\Workspace\\SME19_TestDescriberProject\\workspace_implementation\\TestDescriber2\\TestDescriber-JaCoCo\\resources\\Task1\\src\\org\\magee\\math";
	 
	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		
		public TreeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		@Override
		public String toString() {
			return getName();
		}
		@Override
		public <T> T getAdapter(Class<T> key) {
			return null;
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}

	class ViewContentProvider implements ITreeContentProvider {
		private TreeParent invisibleRoot;

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot==null) initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}
		
	    private void createRecursiveTree(String path, TreeParent parent) {
	    	// Get given folder pointer and ensure it is valid
	        File root = new File(path);
	        if (root.exists() && root.isDirectory()) {
	        	
	        	// For each file/folder in given folder
		        for (File f:root.listFiles()) {
		        	
		            if (f.isDirectory()) {
		            	// Add folder entry to tree and recursively explore folder
		            	TreeParent treeFolder = new TreeParent(f.getName());
		            	parent.addChild(treeFolder);
		            	createRecursiveTree(f.getAbsolutePath(), treeFolder);
		            } else {
		            	// Add file entry to tree
		            	TreeObject treeFile = new TreeObject(f.getName());
		            	parent.addChild(treeFile);
		            }
		        }
	        }
	    }

		private void initialize() {
	        invisibleRoot = new TreeParent("");
			createRecursiveTree(testsFolderPath, invisibleRoot);
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return workbench.getSharedImages().getImage(imageKey);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "ch.uzh.TestDescriber.TestDescriber.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ListViewPart.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
		manager.add(new Separator());
		manager.add(action3);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(action3);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(action3);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				viewer.setContentProvider(new ViewContentProvider());
			}
		};
		action1.setText("Refresh");
		action1.setToolTipText("Refresh folder");
		try {
			URL url = new URL("platform:/plugin/org.eclipse.ui/icons/full/elcl16/refresh_nav.png");
			ImageDescriptor image = ImageDescriptor.createFromURL(url);
			action1.setImageDescriptor(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		action2 = new Action() {
			public void run() {
				showMessage("Filter to be implemented");
			}
		};
		action2.setText("Filter");
		action2.setToolTipText("Filter files and folders");
		try {
			URL url = new URL("platform:/plugin/org.eclipse.ui.views.log/icons/elcl16/find_obj.png");
			ImageDescriptor image = ImageDescriptor.createFromURL(url);
			action2.setImageDescriptor(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		action3 = new Action() {
			public void run() {
				String testsFolderPathInput = getInput("Set folder path", testsFolderPath);
				if (testsFolderPathInput != null) {
					testsFolderPath = testsFolderPathInput;
					viewer.setContentProvider(new ViewContentProvider());
				}
			}
		};
		action3.setText("Set Folder");
		action3.setToolTipText("Set folder path");
		try {
			URL url = new URL("platform:/plugin/org.eclipse.buildship.ui/icons/full/obj16/project.png");
			ImageDescriptor image = ImageDescriptor.createFromURL(url);
			action3.setImageDescriptor(image);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider();
				
				// If selection is a file, show tests view
				if (!provider.hasChildren(obj)) {
					
					// Get file path
					String path = obj.toString();
					TreeParent parent = (TreeParent)provider.getParent(obj);
					while (!parent.toString().equals("")) {
						path = parent.toString() + "\\" + path;
						parent = (TreeParent)provider.getParent(parent);
					}
					path = testsFolderPath + "\\" + path;
					
					// Call TestsView with path
					try {
						TestsView testsView = (TestsView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("ch.uzh.TestDescriber.TestDescriber.views.TestsView");
						testsView.setTestPath(path);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"TestDescriber Explorer",
			message);
	}
	private String getInput(String message, String defaultInput) {
		InputDialog inputDialog = new InputDialog(viewer.getControl().getShell(), "TestDescriber Explorer", message, defaultInput, null);
	    String param = null;
	    int dialogCode = inputDialog.open();
	    if (dialogCode == 0) {
	        param = inputDialog.getValue();
	        if (param != null) {
	            param = param.trim();
	            if (param.length() == 0) {
	                return null;
	            }
	        }
	    }
	    return param;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
