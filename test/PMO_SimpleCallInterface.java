import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PMO_SimpleCallInterface extends Remote {
	public void secretCode( long value ) throws RemoteException;
}
