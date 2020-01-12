package ch.uzh.TestDescriber.TestDescriber.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ButtonHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Open TestDescriber explorer view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("ch.uzh.TestDescriber.TestDescriber.views.ListViewPart");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
