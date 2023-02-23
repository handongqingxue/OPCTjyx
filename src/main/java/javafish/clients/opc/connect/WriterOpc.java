package javafish.clients.opc.connect;

//import org.slf4j.LoggerFactory;
import javafish.clients.opc.JOpc;
import javafish.clients.opc.SynchReadItemExample;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.CoInitializeException;
import javafish.clients.opc.exception.CoUninitializeException;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchReadException;
import javafish.clients.opc.exception.SynchWriteException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
import javafish.clients.opc.exception.UnableRemoveGroupException;
import javafish.clients.opc.exception.UnableRemoveItemException;
import javafish.clients.opc.variant.Variant;

public class WriterOpc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 try {
			//第一步，初始化
			JOpc.coInitialize();
			//第二步，建立一个JOpc对象，三个参数，分别是OpcServer的IP，Server的name，还有JOpc的name
			//JOpc jopc = new JOpc("127.0.0.1", "kepware.KEPServerEX.V6", "M3N881PM37O1M1D");
			JOpc jopc = new JOpc("127.0.0.1", "kepware.KEPServerEX.V6", "DESKTOP-POBS1HN");
			//第三步，建立连接
			jopc.connect();
			//第四步，新建一个OPC的group和item，并把item加到group中
			//OpcGroup group = new OpcGroup("group", true, 500, 0.0f);
			OpcGroup group = new OpcGroup("ks.weight", true, 500, 0.0f);
			//OpcItem item = new OpcItem("Door1", true, "");
			OpcItem item = new OpcItem("ks.weight.1#hw", true, "");
			group.addItem(item);
			//第五步，
			jopc.addGroup(group);
			jopc.registerGroup(group);
			jopc.registerItem(group, item);
			//第六步
			item.setValue(new Variant(1));
			jopc.synchWriteItem(group, item);
			//最后，该释放的全释放掉
			jopc.unregisterItem(group, item);
			jopc.unregisterGroup(group);
			JOpc.coUninitialize();
		} catch (ComponentNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoInitializeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnableAddGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnableAddItemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SynchWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnableRemoveItemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnableRemoveGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoUninitializeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
