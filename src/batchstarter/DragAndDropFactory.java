package batchstarter;

import java.awt.Label;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import static batchstarter.CsgoSounds.*;

public class DragAndDropFactory {

	private TransferHandler transferhandler = new TransferHandler(){
		@Override
		public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }

            boolean copySupported = (MOVE & support.getSourceDropActions()) == MOVE;

            if (!copySupported) {
                return false;
            }

            support.setDropAction(MOVE);
        

        return true;
		}
		@Override
		public boolean importData(TransferHandler.TransferSupport support){
			if(!canImport(support))
				return false;
			 Transferable t = support.getTransferable();
			 try {
				List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

				processFiles(l);
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
				return false;
			}
			 return true;
		}
	};
	
	public TransferHandler getTransferHandler(){
		return transferhandler;
	}
	
}
